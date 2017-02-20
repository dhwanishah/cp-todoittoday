package com.dhwanishah.todoittoday.helpers.database;

import android.provider.BaseColumns;

/**
 * Created by DhwaniShah on 2/16/17.
 */

public final class TodoItTodayContract {

    private TodoItTodayContract() {};

    public static class MainTodoIt implements BaseColumns {
        public static final String TABLE_NAME = "todoit_data";
        public static final String COLUMN_NAME_TASK = "task";
        public static final String COLUMN_NAME_CREATE_DATE = "create_date";
    }
}
