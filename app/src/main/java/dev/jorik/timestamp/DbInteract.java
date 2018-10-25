package dev.jorik.timestamp;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.model.entities.TimeStamp;

import static dev.jorik.timestamp.model.handlers.DbHandler.Const.ID;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.NAME;
import static dev.jorik.timestamp.model.handlers.DbHandler.Const.TIME;

public class DbInteract implements Model{

    @Override
    public TimeStamp createItem(Date time, String name) {
//        TimeStamp timeStamp = new TimeStamp(time, name);
//        ContentValues contentValues = getValuesFromTimestamp(timeStamp);
//        timeStamp.setId(App.getDbHandler().createItem(contentValues));
//        return timeStamp;
        TimeStamp rTimeStamp = null;
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME, name);
        contentValues.put(TIME, time.getTime());
        long id = App.getDbHandler().createItem(contentValues);
        Cursor cursor = App.getDbHandler().readItem(id);
        if (cursor.moveToFirst()) rTimeStamp = getTimestampFromCursor(cursor);
        return rTimeStamp;
    }

    @Override
    public boolean refreshItem(TimeStamp timeStamp) {
        ContentValues contentValues = getValuesFromTimestamp(timeStamp);
        return 0 < App.getDbHandler().updateItem(timeStamp.getId(), contentValues);
    }

    @Override
    public List<TimeStamp> readAllItems() {
        List<TimeStamp> rList = new ArrayList<>();

        Cursor cursor = App.getDbHandler().readAllItems();
        if (cursor.moveToFirst()) {
            do {
                rList.add(getTimestampFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return rList;
    }

    @Override
    public int getRowsCount() {
        return App.getDbHandler().getRowsCount();
    }

    @Override
    public int deleteAllItems() {
        return App.getDbHandler().deleteAllItems();
    }

    private TimeStamp getTimestampFromCursor(Cursor cursor) {
        TimeStamp rTimestamp = new TimeStamp();
        rTimestamp.setId(cursor.getInt(cursor.getColumnIndex(ID)));
        rTimestamp.setName(cursor.getString(cursor.getColumnIndex(NAME)));
        rTimestamp.setTime(new Date(cursor.getLong(cursor.getColumnIndex(TIME))));
        return rTimestamp;
    }

    private ContentValues getValuesFromTimestamp(TimeStamp timeStamp) {
        ContentValues rValues = new ContentValues();
        rValues.put(NAME, timeStamp.getName());
        rValues.put(TIME, timeStamp.getTime().getTime());
        return rValues;
    }
}
