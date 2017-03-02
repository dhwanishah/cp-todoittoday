package com.dhwanishah.todoittoday;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dhwanishah.todoittoday.adapters.TasksAdapter;
import com.dhwanishah.todoittoday.helpers.database.TodoItDbHelper;
import com.dhwanishah.todoittoday.helpers.database.TodoItTodayContract.MainTodoIt;
import com.dhwanishah.todoittoday.models.Task;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Task> mTasksArray;
    private TasksAdapter mTasksArrayAdapter;
    private ListView mLvItems;
    TextView emptyText;

    private TodoItDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("All Tasks");

        init();
        readAndPopulateListFromDb();
        readDB();
    }

    private void init() {
        // Initialize controls
        mLvItems = (ListView) findViewById(R.id.lvItems);
        emptyText = (TextView)findViewById(R.id.tvNoTask);
        mLvItems.setEmptyView(emptyText);

        // Initialize Database connection and items array
        mDbHelper = new TodoItDbHelper(getApplicationContext());
    }

    public void addTask(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_addtask, null);
        final EditText taskTitle = (EditText) dialogView.findViewById(R.id.etNewTaskTitle);
        final Spinner mCategoriesSpinner = (Spinner) dialogView.findViewById(R.id.spinCategory);
        final Spinner mPrioritySpinner = (Spinner) dialogView.findViewById(R.id.spinPriority);
        builder.setView(dialogView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log.e("DSF", taskTitle.getText().toString());
                        String taskTitleNewVal = taskTitle.getText().toString();
                        String taskCategoryNewVal = Integer.toString(mCategoriesSpinner.getSelectedItemPosition());
                        String taskPriorityNewVal = Integer.toString(mPrioritySpinner.getSelectedItemPosition());
                        if (!taskTitleNewVal.equals("")) {
                            db = mDbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put(MainTodoIt.COLUMN_NAME_TASK, taskTitleNewVal);
                            values.put(MainTodoIt.COLUMN_NAME_CATEGORY, taskCategoryNewVal);
                            values.put(MainTodoIt.COLUMN_NAME_PRIORITY, taskPriorityNewVal);
                            long newRowId = db.insertWithOnConflict(MainTodoIt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                            if (newRowId != -1) {
                                //mTasksArray.add(new Task(taskTitleNewVal, taskCategoryNewVal));
                                //mTasksArrayAdapter.add(new Task(taskTitleNewVal, taskCategoryNewVal));
                                readAndPopulateListFromDb();
                                taskTitle.setText("");
                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong, could not save.", Toast.LENGTH_LONG).show();
                            }
                            db.close();
                        } else {
                            Toast.makeText(getApplicationContext(), "Enter a value first, na?!", Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tvTaskTitle);
        String task = String.valueOf(taskTextView.getText());
        Log.e("delete", task);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//        db.delete(MainTodoIt.TABLE_NAME,
//                MainTodoIt.COLUMN_NAME_TASK + " = ?",
//                new String[]{task});

        // Define 'where' part of query.
                String selection = MainTodoIt.COLUMN_NAME_TASK + " LIKE ?";
        // Specify arguments in placeholder order.
                String[] selectionArgs = { task };
        // Issue SQL statement.
                db.delete(MainTodoIt.TABLE_NAME, selection, selectionArgs);


        db.close();
        readAndPopulateListFromDb();
        //mTasksArrayAdapter.notifyDataSetChanged();
    }

    public void editTask(View view) {
        //readDB();
        View parent = (View) view.getParent().getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tvTaskTitle);
        TextView taskCategoryTextView = (TextView) parent.findViewById(R.id.tvTaskCategory);
        TextView taskPriorityTextView = (TextView) parent.findViewById(R.id.tvTaskPriority);
        Intent openEditTaskActivity = new Intent(getApplicationContext(), EditTaskActivity.class);
        int indexOf = -1;
        for (int i = 0; i < mTasksArray.size(); i++) {
            if (mTasksArray.get(i).getmTaskTitle().equals(taskTextView.getText().toString())) {
                Log.e("beforeSendMainLoop"+i, mTasksArray.get(i).getmTaskTitle() + " " + taskTextView.getText().toString());
                indexOf = i;
            }
        }
        Log.e("beforeSendMainD", mTasksArray.size() + " " + indexOf + " " + taskTextView.getText().toString() + " " + taskCategoryTextView.getText().toString() + " : " + taskPriorityTextView.getTag());
        if (indexOf != -1) {
            openEditTaskActivity.putExtra("currentItemIndex", indexOf);
            openEditTaskActivity.putExtra("currentItemData", taskTextView.getText().toString());
            openEditTaskActivity.putExtra("currentItemCategory", taskCategoryTextView.getText().toString());
            openEditTaskActivity.putExtra("currentItemPriority", taskPriorityTextView.getTag().toString());
            startActivityForResult(openEditTaskActivity, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.e("Settings", "Settings clicked.");
//                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                LayoutInflater inflater = this.getLayoutInflater();
//                final View dialogView = inflater.inflate(R.layout.dialog_addtask, null);
//                final EditText taskTitle = (EditText) dialogView.findViewById(R.id.etNewTaskTitle);
//                final Spinner mCategoriesSpinner = (Spinner) dialogView.findViewById(R.id.spinCategory);
//                final Spinner mPrioritySpinner = (Spinner) dialogView.findViewById(R.id.spinPriority);
//                builder.setView(dialogView)
//                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //Log.e("DSF", taskTitle.getText().toString());
//                                String taskTitleNewVal = taskTitle.getText().toString();
//                                String taskCategoryNewVal = Integer.toString(mCategoriesSpinner.getSelectedItemPosition());
//                                String taskPriorityNewVal = Integer.toString(mPrioritySpinner.getSelectedItemPosition());
//                                if (!taskTitleNewVal.equals("")) {
//                                    db = mDbHelper.getWritableDatabase();
//                                    ContentValues values = new ContentValues();
//                                    values.put(MainTodoIt.COLUMN_NAME_TASK, taskTitleNewVal);
//                                    values.put(MainTodoIt.COLUMN_NAME_CATEGORY, taskCategoryNewVal);
//                                    values.put(MainTodoIt.COLUMN_NAME_PRIORITY, taskPriorityNewVal);
//                                    long newRowId = db.insertWithOnConflict(MainTodoIt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                                    if (newRowId != -1) {
//                                        //mTasksArray.add(new Task(taskTitleNewVal, taskCategoryNewVal));
//                                        //mTasksArrayAdapter.add(new Task(taskTitleNewVal, taskCategoryNewVal));
//                                        readAndPopulateListFromDb();
//                                        taskTitle.setText("");
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "Something went wrong, could not save.", Toast.LENGTH_LONG).show();
//                                    }
//                                    db.close();
//                                } else {
//                                    Toast.makeText(getApplicationContext(), "Enter a value first, na?!", Toast.LENGTH_LONG).show();
//                                }
//
//                            }
//                        })
//                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//                builder.show();
                //readDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int taskIndex = data.getIntExtra("taskIndex", -1);
                String newTaskTitle = data.getStringExtra("editedTitleString");
                int newCategory = Integer.parseInt(data.getStringExtra("editedTaskCategory"));
                int newPriority = Integer.parseInt(data.getStringExtra("editedTaskPriority"));
                String[] categoryArray = getResources().getStringArray(R.array.categories);
                String[] priorityArray = getResources().getStringArray(R.array.priorities);

                Log.e("e", mTasksArray.toString() + " | " + taskIndex + " | " + categoryArray[newCategory] + " | " + priorityArray[newPriority]);
                if (updateDb(mTasksArray.get(taskIndex).getmTaskTitle(), newTaskTitle, Integer.toString(newCategory), Integer.toString(newPriority))) {
                    mTasksArray.set(taskIndex, new Task(newTaskTitle, Integer.toString(newCategory), Integer.toString(newPriority)));
                    mTasksArrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update db.", Toast.LENGTH_LONG).show();
                }
            }
        }
        readAndPopulateListFromDb();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void readAndPopulateListFromDb() {
        //mItems = new ArrayList<>();
        //Log.i("a", mTasksArray.size() + " ");
        mTasksArray = new ArrayList<>();
        Log.i("b", mTasksArray.size() + " ");
        db = mDbHelper.getReadableDatabase();
        String[] projections = {
                MainTodoIt._ID,
                MainTodoIt.COLUMN_NAME_TASK,
                MainTodoIt.COLUMN_NAME_CATEGORY,
                MainTodoIt.COLUMN_NAME_PRIORITY,
                MainTodoIt.COLUMN_NAME_CREATE_DATE
        };
        Cursor cursor = db.query(MainTodoIt.TABLE_NAME, projections, null, null, null, null, null);
        while(cursor.moveToNext()) {
            int columnTaskIndex = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_TASK);
            int columnTaskCategory = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_CATEGORY);
            int columnTaskPriority = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_PRIORITY);
            //mItems.add(cursor.getString(columnTaskIndex));
            mTasksArray.add(new Task(cursor.getString(columnTaskIndex), cursor.getString(columnTaskCategory), cursor.getString(columnTaskPriority)));
            Log.e("DB", cursor.getString(columnTaskIndex) + " : " + cursor.getString(cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_CATEGORY)));
            Log.i("c", mTasksArray.size() + " ");
        }
        if (mTasksArrayAdapter == null) {
            mTasksArrayAdapter = new TasksAdapter(this, mTasksArray);
            mLvItems.setAdapter(mTasksArrayAdapter);
        } else {
            mTasksArrayAdapter.clear();
            mTasksArrayAdapter.addAll(mTasksArray);
            mTasksArrayAdapter.notifyDataSetChanged();
        }
        Log.i("d", mTasksArray.size() + " ");
        cursor.close();
        db.close();
    }

    private void readDB() {
        db = mDbHelper.getReadableDatabase();
        String[] projections = {
                MainTodoIt._ID,
                MainTodoIt.COLUMN_NAME_TASK,
                MainTodoIt.COLUMN_NAME_CATEGORY,
                MainTodoIt.COLUMN_NAME_PRIORITY,
                MainTodoIt.COLUMN_NAME_CREATE_DATE
        };
        Cursor cursor = db.query(MainTodoIt.TABLE_NAME, projections, null, null, null, null, null);
        if (cursor.moveToNext()) {
            while (cursor.moveToNext()) {
                int idx = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_TASK);
                Log.e("readDB", cursor.getString(idx) + " : " + cursor.getString(cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_CATEGORY)) + " " + cursor.getString(cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_PRIORITY)));
            }
        } else {
            Log.e("readDB", "Nothing in the database.");
        }
        cursor.close();
        db.close();
    }

    private boolean updateDb(String originalValue, String newTaskValue, String newCategoryValue, String newPriorityValue) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MainTodoIt.COLUMN_NAME_TASK, newTaskValue);
        if (newCategoryValue != null) {
            values.put(MainTodoIt.COLUMN_NAME_CATEGORY, newCategoryValue);
        }
        if (newPriorityValue != null) {
            values.put(MainTodoIt.COLUMN_NAME_PRIORITY, newPriorityValue);
        }

        // Which row to update, based on the title
        String selection = MainTodoIt.COLUMN_NAME_TASK + " LIKE ?";
        String[] selectionArgs = { originalValue };

        int count = db.update(
                MainTodoIt.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private void delete(String id) {
        db = mDbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = MainTodoIt._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { id };
        // Issue SQL statement.
        db.delete(MainTodoIt.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    private void deleteAll() {
        //SQLiteDatabase db = this.getWritableDatabase();
        // db.delete(TABLE_NAME,null,null);
        //db.execSQL("delete * from"+ TABLE_NAME);
        db = mDbHelper.getWritableDatabase();
        db.delete(MainTodoIt.TABLE_NAME, null, null);
        db.close();
    }
}
