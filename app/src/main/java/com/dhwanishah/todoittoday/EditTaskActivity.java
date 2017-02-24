package com.dhwanishah.todoittoday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class EditTaskActivity extends AppCompatActivity {

    EditText mTaskTitle;
    Spinner mTaskCategory;
    Spinner mTaskPriority;
    int taskIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        setTitle("Edit Task");

        mTaskTitle = (EditText) findViewById(R.id.tvEditTaskTitle);
        mTaskCategory = (Spinner) findViewById(R.id.spinEditCategory);
        mTaskPriority = (Spinner) findViewById(R.id.spinEditPriority);

        taskIndex = getIntent().getIntExtra("currentItemIndex", -1);
        mTaskTitle.append(getIntent().getStringExtra("currentItemData"));

        String passedCategory = getIntent().getStringExtra("currentItemCategory");
        String[] categoryArray = getResources().getStringArray(R.array.categories);
        mTaskCategory.setSelection(Arrays.asList(categoryArray).indexOf(passedCategory));

        String passedPriority = getIntent().getStringExtra("currentItemPriority");
        String[] priorityArray = getResources().getStringArray(R.array.priorities);
        mTaskPriority.setSelection(Arrays.asList(priorityArray).indexOf(passedPriority));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_task_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_task:
                if (!mTaskTitle.getText().toString().equals("")) {
                    Intent openMainActivity = new Intent();
                    Log.e("beforeSend", taskIndex + " " + mTaskTitle.getText().toString() + " " + Integer.toString(mTaskCategory.getSelectedItemPosition()));
                    openMainActivity.putExtra("taskIndex", taskIndex);
                    openMainActivity.putExtra("editedTitleString", mTaskTitle.getText().toString());
                    openMainActivity.putExtra("editedTaskCategory", Integer.toString(mTaskCategory.getSelectedItemPosition()));
                    openMainActivity.putExtra("editedTaskPriority", Integer.toString(mTaskPriority.getSelectedItemPosition()));
                    setResult(RESULT_OK, openMainActivity);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot save an empty task.", Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
