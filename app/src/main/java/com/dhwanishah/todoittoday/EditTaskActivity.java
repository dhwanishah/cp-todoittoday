package com.dhwanishah.todoittoday;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditTaskActivity extends AppCompatActivity {

    EditText mTaskTitle;
    Button mEditButton;
    int taskIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mTaskTitle = (EditText) findViewById(R.id.tvEditTaskTitle);
        mTaskTitle.setSelection(mTaskTitle.getText().length());
        mEditButton = (Button) findViewById(R.id.btEditTaskComplete);

        taskIndex = getIntent().getIntExtra("currentItemIndex", -1);
        String taskTitle = getIntent().getStringExtra("currentItemData");
        mTaskTitle.setText(taskTitle);

    }

    public void editTaskComplete(View view) {
        if (!mTaskTitle.getText().toString().equals("")) {
            Intent openMainActivity = new Intent();
            openMainActivity.putExtra("taskIndex", taskIndex);
            openMainActivity.putExtra("editedTitleString", mTaskTitle.getText().toString());
            setResult(RESULT_OK, openMainActivity);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot save an empty task.", Toast.LENGTH_LONG).show();
        }
    }
}
