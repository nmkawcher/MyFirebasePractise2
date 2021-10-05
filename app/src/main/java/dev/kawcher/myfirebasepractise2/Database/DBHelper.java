package dev.kawcher.myfirebasepractise2.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import dev.kawcher.myfirebasepractise2.Model.InputModel;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ind.db";
    private static final int DATABASE_VERSION =1;
    public static final String TAG = "LOGTAG";


    private SQLiteDatabase db;
    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;


        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " +
                DatabaseContract.UserTable.USER_TABLE + " ( " +
               DatabaseContract.UserTable.USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
              DatabaseContract.UserTable.USER_NAME + " TEXT, " +
                DatabaseContract.UserTable.USER_PASSWORD + " TEXT  " +
                ")";

        db.execSQL(SQL_CREATE_CATEGORY_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.UserTable.USER_TABLE);


        onCreate(db);
    }

    public long insertUser(InputModel model) {
        db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(DatabaseContract.UserTable.USER_NAME, model.getInput1());
        cv.put(DatabaseContract.UserTable.USER_PASSWORD, model.getInput2());

        long id = db.insert(DatabaseContract.UserTable.USER_TABLE, null, cv);

        return id;
    }

    public ArrayList<InputModel> getAllLocalUser() {
        db = getReadableDatabase();
        ArrayList<InputModel> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseContract.UserTable.USER_TABLE, null);

        if (cursor.moveToFirst()) {
            do {

                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DatabaseContract.UserTable.USER_ID));
                @SuppressLint("Range") String input1 = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserTable.USER_NAME));
                @SuppressLint("Range") String input2 = cursor.getString(cursor.getColumnIndex(DatabaseContract.UserTable.USER_PASSWORD));

                list.add(new InputModel( input1,input2));
            } while (cursor.moveToNext());
        }

        return list;
    }

    public void deleteSingleUser(int id) {

        db = getWritableDatabase();
        db.delete(DatabaseContract.UserTable.USER_TABLE, DatabaseContract.UserTable.USER_ID + "=?", new String[]{String.valueOf(id)});

    }

    public void deleteAllInput() {
        db = getReadableDatabase();
        db.execSQL("DELETE FROM " + DatabaseContract.UserTable.USER_TABLE);
        db.close();
    }
    private Bitmap byteToBitmap(byte[] byteData) {
        if (byteData != null ) {
            return BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        } else {
            return null;
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
            return outputStream.toByteArray();
        } else {
            return null;
        }
    }

    private String insertFormatedDate(String date) {
        Date d = null;
        try {
            d = new SimpleDateFormat("dd-MM-yyyy").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(d);
        return formattedDate;
    }

    private String getFormatedDate(String date) {
        Date d = null;
        try {
            d = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String formattedDate2 = new SimpleDateFormat("dd-MM-yyyy").format(d);
        return formattedDate2;

    }

    //get laster itemCode

}
