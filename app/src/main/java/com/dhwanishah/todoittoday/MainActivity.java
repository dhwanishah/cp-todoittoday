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
import android.widget.TextView;
import android.widget.Toast;

import com.dhwanishah.todoittoday.helpers.database.TodoItDbHelper;
import com.dhwanishah.todoittoday.helpers.database.TodoItTodayContract.MainTodoIt;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mItems;
    //private HashMap<String, String> mItemsArr = new HashMap<>();
    private ArrayAdapter<String> mItemsAdapter;
    private ListView mLvItems;
//    private Button mAddButton;
//    private EditText mNewItemToAdd;

    private TodoItDbHelper mDbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        readAndPopulateListFromDb();

        //deleteAll();

//        mAddButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String newItem = mNewItemToAdd.getText().toString();
//                if (!newItem.equals("")) {
//                    db = mDbHelper.getWritableDatabase();
//                    ContentValues values = new ContentValues();
//                    values.put(MainTodoIt.COLUMN_NAME_TASK, newItem);
//                    //long newRowId = db.insert(MainTodoIt.TABLE_NAME, null, values);
//                    long newRowId = db.insertWithOnConflict(MainTodoIt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
//                    if (newRowId != -1) {
//                        mItemsAdapter.add(newItem);
//                        Toast.makeText(getApplicationContext(), "New row: " + newRowId, Toast.LENGTH_LONG).show();
//                        mNewItemToAdd.setText("");
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Something went wrong, could not save.", Toast.LENGTH_LONG).show();
//                    }
//                    db.close();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Enter a value first, na?!", Toast.LENGTH_LONG).show();
//                }
//            }
//        });

//        mLvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView taskTextView = (TextView) parent.findViewById(R.id.tvTaskTitle);
//                String task = String.valueOf(taskTextView.getText());
//                SQLiteDatabase db = mDbHelper.getWritableDatabase();
//                db.delete(MainTodoIt.TABLE_NAME,
//                        MainTodoIt.COLUMN_NAME_TASK + " = ?",
//                        new String[]{task});
//                db.close();
//                //mItems.remove(position);
//                //delete(Integer.toString(position));
//                //mItemsAdapter.notifyDataSetChanged();
//                readAndPopulateListFromDb();
//                return true;
//            }
//        });
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
        Log.e("mItems", mItems.toString());
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.tvTaskTitle);
        Intent openEditTaskActivity = new Intent(getApplicationContext(), EditTaskActivity.class);
        openEditTaskActivity.putExtra("currentItemIndex", mItems.indexOf(String.valueOf(taskTextView.getText())));
        openEditTaskActivity.putExtra("currentItemData", String.valueOf(taskTextView.getText()));
        //startActivity(openEditTaskActivity);
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
                builder.setView(dialogView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("DSF", taskTitle.getText().toString());
                                String newItem = taskTitle.getText().toString();
                                if (!newItem.equals("")) {
                                    db = mDbHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put(MainTodoIt.COLUMN_NAME_TASK, newItem);
                                    //long newRowId = db.insert(MainTodoIt.TABLE_NAME, null, values);
                                    long newRowId = db.insertWithOnConflict(MainTodoIt.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                    if (newRowId != -1) {
                                        mItemsAdapter.add(newItem);
                                        Toast.makeText(getApplicationContext(), "New row: " + newRowId, Toast.LENGTH_LONG).show();
                                        taskTitle.setText("");
//                                        mNewItemToAdd.setText("");
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
                String taskTitle = data.getStringExtra("editedTitleString");
                Log.e("e", mItems.toString() + " | " + taskIndex);
                //Toast.makeText(getApplicationContext(), taskTitle, Toast.LENGTH_LONG).show();
                //mNewItemToAdd.setText(taskIndex + " " + taskTitle);
                if (updateDb(mItems.get(taskIndex), taskTitle)) {
                    mItems.set(taskIndex, taskTitle);
                    mItemsAdapter.notifyDataSetChanged();
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
//        mNewItemToAdd = (EditText) findViewById(R.id.etNewItem);
//        mAddButton = (Button) findViewById(R.id.btnAddItem);
        mLvItems = (ListView) findViewById(R.id.lvItems);

        // Initialize Database connection and items array
        mDbHelper = new TodoItDbHelper(getApplicationContext());
    }

    private void readAndPopulateListFromDb() {
        mItems = new ArrayList<>();
        db = mDbHelper.getReadableDatabase();
        String[] projections = {
                MainTodoIt._ID,
                MainTodoIt.COLUMN_NAME_TASK,
                MainTodoIt.COLUMN_NAME_CREATE_DATE
        };
        Cursor cursor = db.query(MainTodoIt.TABLE_NAME, projections, null, null, null, null, null);
        while(cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(MainTodoIt.COLUMN_NAME_TASK);
            mItems.add(cursor.getString(idx));
            Log.e("DB", cursor.getString(idx) + " : ");
        }
        if (mItemsAdapter == null) {
            mItemsAdapter = new ArrayAdapter<>(this, R.layout.item_todo, R.id.tvTaskTitle, mItems);
            mLvItems.setAdapter(mItemsAdapter);
        } else {
            mItemsAdapter.clear();
            mItemsAdapter.addAll(mItems);
            mItemsAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    private boolean updateDb(String originalValue, String newValue) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MainTodoIt.COLUMN_NAME_TASK, newValue);

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
