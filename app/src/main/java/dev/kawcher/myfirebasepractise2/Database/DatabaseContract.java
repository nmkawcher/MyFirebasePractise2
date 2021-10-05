package dev.kawcher.myfirebasepractise2.Database;

import android.provider.BaseColumns;

public class DatabaseContract {



    public static class UserTable implements BaseColumns {

        public static final String USER_TABLE = "user_table";
        public static final String USER_ID = "user_id";
        public static final String USER_NAME = "user_name";
        public static final String USER_PASSWORD = "user_pass";
    }


}