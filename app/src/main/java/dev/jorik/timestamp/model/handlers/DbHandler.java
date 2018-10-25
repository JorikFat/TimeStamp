package dev.jorik.timestamp.model.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.Utils.DateTimeUtils;
import dev.jorik.timestamp.model.entities.TimeStamp;

import static dev.jorik.timestamp.model.handlers.DbHandler.Const.DB_NAME;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.DB_VERSION;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.ID;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.NAME;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.ARG;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.TABLE_NAME;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.TIME;

public class DbHandler extends SQLiteOpenHelper {

    public static class Const {
        public static final String DB_NAME = "time_stamps";
        public static final int DB_VERSION = 2;
        public static final String ARG = "=?";

        public static final String TABLE_NAME = "times";
        //поля
        public static final String ID = "_id";
        public static final String TIME = "time";
        public static final String NAME = "name";

    }

    public DbHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY, "
                + TIME + " TEXT,"
                + NAME + " TEXT"
                + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String temp = "temp_table";
        if (oldVersion < 2) {
            //todo оптимизировать
            //optimize: перемещением данных по  одному, чтобы не забивать память для больших таблиц
            /**
             * Скачивание всей таблицы и перемещение ее в новую таблицу
             * перемещать записи не через TimeStamp а через ContentValue
             */
//            List<TimeStamp> allTimestamp = readAllItems_v1();//bug: double open db
            List<TimeStamp> allTimestamp = getAllItemsFromDB(db);
            db.execSQL("CREATE TABLE " + temp + "("
                    + ID + " INTEGER PRIMARY KEY, "
                    + TIME + " INTEGER,"
                    + NAME + " TEXT"
                    + ")"
            );

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("ALTER TABLE " + temp + " RENAME TO " + TABLE_NAME);
            for (TimeStamp ts : allTimestamp) {
                db.insert(TABLE_NAME, null, getValuesFromTimestamp(ts));
            }
        }
    }

    public long createItem(ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public Cursor readItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME,
                null,
                ID + ARG,
                new String[]{String.valueOf(id)},
                null, null, null, null
        );
    }

    public int updateItem(long id, ContentValues contentValues) {
        SQLiteDatabase db = getWritableDatabase();
        return db.update(TABLE_NAME, contentValues, ID + ARG, new String[]{String.valueOf(id)});
    }

    public void deleteItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, ID + ARG, new String[]{String.valueOf(id)});
        db.close();
    }

    public int deleteAllItems() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

    public int getRowsCount() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME,
                null, null, null,
                null, null, null);
        int count = cursor.getCount();
        database.close();
        cursor.close();
        return count;
    }

    public Cursor readAllItems(){
        SQLiteDatabase db = getWritableDatabase();
        return db.query(TABLE_NAME, null, null, null,
                null, null, TIME + " ASC");
    }

    //time <- String
    //todo рефакторить под mvp
    /*model не должен генерировать timeStamp*/
    private TimeStamp getTimestampFromCursor_v1(Cursor cursor) {
        TimeStamp rTimestamp = new TimeStamp();
        rTimestamp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        rTimestamp.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        rTimestamp.setTime(getDateFromString(cursor.getString(cursor.getColumnIndex(TIME))));
        return rTimestamp;
    }

    //time <- long
    //todo рефакторить под mvp
    /*model не должен генерировать timeStamp*/
    private TimeStamp getTimestampFromCursor(Cursor cursor) {
        TimeStamp rTimestamp = new TimeStamp();
        rTimestamp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        rTimestamp.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        rTimestamp.setTime(new Date(cursor.getLong(cursor.getColumnIndex(TIME))));
        return rTimestamp;
    }

    //todo рефакторить под mvp
    /*model не должен генерировать timeStamp*/
    private ContentValues getValuesFromTimestamp(TimeStamp timeStamp) {
        ContentValues rValues = new ContentValues();
        rValues.put(NAME, timeStamp.getName());
        rValues.put(TIME, timeStamp.getTime().getTime());
        return rValues;
    }

    //метод для совместимости с первой версией базы
    private String getStringFromDate(Date date) {
        return DateTimeUtils.TIME.format(date);
    }

    //метод для совместимости с первой версией базы
    private Date getDateFromString(String stringDate) {
        Date rDate = new Date();
        try {
            rDate = DateTimeUtils.TIME.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rDate;
    }

    //todo рефакторить под mvp
    /*model не должен генерировать timeStamp*/
    private List<TimeStamp> getAllItemsFromDB(SQLiteDatabase database) {
        List<TimeStamp> rList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                rList.add(getTimestampFromCursor_v1(cursor));
            } while (cursor.moveToNext());
        }
        return rList;
    }
}