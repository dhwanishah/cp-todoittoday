package com.dhwanishah.todoittoday.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dhwanishah.todoittoday.R;
import com.dhwanishah.todoittoday.models.Task;

import java.util.ArrayList;

/**
 * Created by DhwaniShah on 2/21/17.
 */

public class TasksAdapter extends ArrayAdapter<Task> {

    public TasksAdapter(Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_todo, parent, false);
        }

        // Lookup view for data population
        TextView tvTaskTitle = (TextView) convertView.findViewById(R.id.tvTaskTitle);
        TextView tvTaskCategory = (TextView) convertView.findViewById(R.id.tvTaskCategory);
        TextView tvTaskPriority = (TextView) convertView.findViewById(R.id.tvTaskPriority);
        // Populate the data into the template view using the data object
        tvTaskTitle.setText(task.getmTaskTitle());

        // Get specific categories from string array and convert from number string stored in DB to friendly name
        String[] taskCategoryConversion = convertView.getResources().getStringArray(R.array.categories);
        tvTaskCategory.setText(taskCategoryConversion[Integer.parseInt(task.getmTaskCategory())]);


        String[] taskPriorityConversion = convertView.getResources().getStringArray(R.array.priorities);
        String currentPriority = taskPriorityConversion[Integer.parseInt(task.getmTaskPriority())];
        String priorityTextColor = "#333333";
        if (currentPriority.equals("High")) {
            priorityTextColor = "#F44336";
        } else if (currentPriority.equals("Medium")) {
            priorityTextColor = "#FF9800";
        } else if (currentPriority.equals("Low")) {
            priorityTextColor = "#4CAF50";
        }
        tvTaskPriority.setTextColor(Color.parseColor(priorityTextColor));
        tvTaskPriority.setText(currentPriority);
        // Return the completed view to render on screen
        return convertView;
    }
}
