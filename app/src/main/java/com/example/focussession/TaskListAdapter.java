package com.example.focussession;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focussession.model.TaskModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.Backend;
import com.google.firebase.annotations.concurrent.Background;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * This class manages tasks from Firebase. Retrieves tasks, manages them(mark complete, deleted),
 * , and add tasks to the database.
 */
public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private ArrayList<TaskModel> taskDataSet;
    private String searchQuery = "";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tasksList, taskStatusTv;
        LinearLayout containerLL;

        public ViewHolder(View view) {
            super(view);
            tasksList = view.findViewById(R.id.tasksList);
            taskStatusTv = view.findViewById(R.id.taskStatusTv);
            containerLL = view.findViewById(R.id.containerLL);
        }
    }

    public TaskListAdapter(ArrayList<TaskModel> taskDataSet) {
        this.taskDataSet = taskDataSet;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TaskModel task = taskDataSet.get(position);
        viewHolder.tasksList.setText(task.getTaskName());
        viewHolder.taskStatusTv.setText(task.getTaskStatus());
        String status = task.getTaskStatus().toLowerCase();
        String taskName = task.getTaskName();

        // Highlight search query in the task name
        if (!searchQuery.isEmpty()){
            Spannable spannable = new SpannableString(taskName);
            int index = taskName.toLowerCase().indexOf(searchQuery.toLowerCase());
            // Search match is found in the list
            if (index != -1){
                spannable.setSpan(new BackgroundColorSpan(Color.parseColor("#BDBDBD")), index, index + searchQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            viewHolder.tasksList.setText(spannable);
        } else {
            viewHolder.tasksList.setText(taskName);
        }
        viewHolder.taskStatusTv.setText(status);

        switch (status) {
            case "pending":
                viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFFF00")); // yellow
                break;
            case "completed":
                viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#00FF00")); // green
                break;
            default:
                viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFFFFF")); // white
                break;
        }

        viewHolder.containerLL.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), viewHolder.containerLL);
            popupMenu.inflate(R.menu.taskmenu);
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.deleteMenu) {
                    FirebaseFirestore.getInstance().collection("tasks")
                            .document(taskDataSet.get(position).getTaskId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(v.getContext(), "Task Deleted Successfully", Toast.LENGTH_SHORT).show();
                                viewHolder.containerLL.setVisibility(View.GONE);
                            });
                    return true;
                }

                if (item.getItemId() == R.id.markCompleteMenu) {
                    TaskModel completedTask = taskDataSet.get(position);
                    completedTask.setTaskStatus("completed");
                    FirebaseFirestore.getInstance().collection("tasks")
                            .document(taskDataSet.get(position).getTaskId())
                            .set(completedTask)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(v.getContext(), "Task Marked as Completed!", Toast.LENGTH_SHORT).show();
                            });

                    viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#00FF00"));
                    viewHolder.taskStatusTv.setText("COMPLETED");
                    return true;
                }

                return false;
            });

            return false;
        });
    }

    @Override
    public int getItemCount() {
        return taskDataSet.size();
    }

    /**
     * Highlight text when user searches for a specific task.
     * @param query
     */
    public void setSearchQuery(String query) {
        this.searchQuery = query;
        notifyDataSetChanged();
    }
}