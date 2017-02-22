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
import android.widget.ArrayAdapter;
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

    // TODO: Convert edit task method to use new mTaskArrayAdapter

    private ArrayList<String> mItems;
    private ArrayList<Task> mTasksArray;
    private ArrayAdapter<String> mItemsAdapter;
    private TasksAdapter mTasksArrayAdapter;
    private ListView mLvItems;
    TextView emptyText;

    private TodoItDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        readAndPopulateListFromDb();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tvTaskTitle);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(MainTodoIt.TABLE_NAME,
                MainTodoIt.COLUMN_NAME_TASK + " = ?",
                new String[]{task});
        db.close();
        readAndPopulateListFromDb();
    }

    public void editTask(View view) {
        readDB();
        //Log.e("mItems", mItems.toString());
        //Log.e("mTaskArray", mTasksArray.toString());
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tvTaskTitle);
        TextView taskCategoryTextView = (TextView) parent.findViewById(R.id.tvTaskCategory);
        Intent openEditTaskActivity = new Intent(getApplicationContext(), EditTaskActivity.class);
        //openEditTaskActivity.putExtra("currentItemIndex", mTasksArray.indexOf(String.valueOf(taskTextView.getText())));
        //String[] categoryArray = getResources().getStringArray(R.array.categories);
        //Task currentObject = new Task(taskTextView.getText().toString(), Integer.toString(Arrays.asList(categoryArray).indexOf(taskCategoryTextView.getText().toString())));
        int indexOf = -1;
        for (int i = 0; i < mTasksArray.size(); i++) {
            if (mTasksArray.get(i).getmTaskTitle().equals(taskTextView.getText().toString())) {
                indexOf = i;
            }
        }
        //Log.e("ARR", currentObject.getmTaskTitle() + " " + currentObject.getmTaskCategory() + " " + indexOf);
        openEditTaskActivity.putExtra("currentItemIndex", indexOf);
        openEditTaskActivity.putExtra("currentItemData", String.valueOf(taskTextView.getText()));
        openEditTaskActivity.putExtra("currentItemCategory", String.valueOf(taskCategoryTextView.getText()));
        startActivityForResult(openEditTaskActivity, 1);
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
            case R.id.action_add_task:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_addtask, null);
                final EditText taskTitle = (EditText) dialogView.findViewById(R.id.etNewTaskTitle);
                final Spinner mCategoriesSpinner = (Spinner) dialogView.findViewById(R.id.spinCategory);
                builder.setView(dialogView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("DSF", taskTitle.getText().toString());
                                String taskTitleNewVal = taskTitle.getText().toString();
                                String taskCategoryNewVal = Integer.toString(mCategoriesSpinner.getSelectedItemPosition());
                                if (!taskTitleNewVal.equals("")) {
                                    db = mDbHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(MainTodoIt.COLUMN_NAME_TASK, taskTitleNewVal);
                                    values.put(MainTodoIt.COLUMN_NAME_CATEGORY, taskCategoryNewVal);
                                    long newRowId = db.insertWithOnConflict(MainTodoIt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                    if (newRowId != -1) {
                                        //mItemsAdapter.add(taskTitleNewVal);
                                        mTasksArrayAdapter.add(new Task(taskTitleNewVal, taskCategoryNewVal));
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
                readDB();
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
                String[] categoryArray = getResources().getStringArray(R.array.categories);

                Log.e("e", mTasksArray.toString() + " | " + taskIndex + " | " + categoryArray[newCategory]);
//                if (updateDb(mItems.get(taskIndex), taskTitle, category)) {
//                    mItems.set(taskIndex, taskTitle);
//                    mItemsAdapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Failed to update db.", Toast.LENGTH_LONG).show();
//                }
                if (updateDb(mTasksArray.get(taskIndex).getmTaskTitle(), newTaskTitle, Integer.toString(newCategory))) {
                    mTasksArray.set(taskIndex, new Task(newTaskTitle, Integer.toString(newCategory)));
                    //mItems.set(taskIndex, taskTitle);
                    //mItemsAdapter.notifyDataSetChanged();
                    mTasksArrayAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed to update db.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void init() {
        // Initialize controls
        mLvItems = (ListView) findViewById(R.id.lvItems);
        emptyText = (TextView)findViewById(R.id.tvNoTask);
        mLvItems.setEmptyView(emptyText);

        // Initialize Database connection and items array
        mDbHelper = new TodoItDbHelper(getApplicationContext());
    }

    private void readAndPopulateListFromDb() {
        //mItems = new ArrayList<>();
        mTasksArray = new ArrayList<>();
        db = mDbHelper.getReadableDatabase();
        String[] projections = {
                MainTodoIt._ID,
                MainTodoIt.COLUMN_NAME_TASK,
                MainTodoIt.COLUMN_NAME_CATEGORY,
                MainTodoIt.COLUMN_NAME_CREATE_DATE
        };
        Cursor cursor = db.query(MainTodoIt.TABLE_NAME, projections, null, null, null, null, null);
        while(cursor.moveToNext()) {
            int columnTaskIndex = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_TASK);
            int columnTaskCategory = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_CATEGORY);
            //mItems.add(cursor.getString(columnTaskIndex));
            mTasksArray.add(new Task(cursor.getString(columnTaskIndex), cursor.getString(columnTaskCategory)));
            Log.e("DB", cursor.getString(columnTaskIndex) + " : " + cursor.getString(cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_CATEGORY)));
        }
//        if (mItemsAdapter == null) {
//            mItemsAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.tvTaskTitle, mItems);
//            mLvItems.setAdapter(mItemsAdapter);
//        } else {
//            mItemsAdapter.clear();
//            mItemsAdapter.addAll(mItems);
//            mItemsAdapter.notifyDataSetChanged();
//        }
        if (mTasksArrayAdapter == null) {
            mTasksArrayAdapter = new TasksAdapter(this, mTasksArray); //ArrayAdapter<>(this, R.layout.item_todo, R.id.tvTaskTitle, mItems);
            mLvItems.setAdapter(mTasksArrayAdapter);
        } else {
            mTasksArrayAdapter.clear();
            mTasksArrayAdapter.addAll(mTasksArray);
            mTasksArrayAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    private void readDB() {
        db = mDbHelper.getReadableDatabase();
        String[] projections = {
                MainTodoIt._ID,
                MainTodoIt.COLUMN_NAME_TASK,
                MainTodoIt.COLUMN_NAME_CATEGORY,
                MainTodoIt.COLUMN_NAME_CREATE_DATE
        };
        Cursor cursor = db.query(MainTodoIt.TABLE_NAME, projections, null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_TASK);
            Log.e("DB_READ", cursor.getString(idx) + " : " + cursor.getString(cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_CATEGORY)));
        }
        cursor.close();
        db.close();
    }

    private boolean updateDb(String originalValue, String newTaskValue, String newCategoryValue) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MainTodoIt.COLUMN_NAME_TASK, newTaskValue);
        if (newCategoryValue != null) {
            values.put(MainTodoIt.COLUMN_NAME_CATEGORY, newCategoryValue);
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
