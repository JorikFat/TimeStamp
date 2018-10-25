package dev.jorik.timestamp;

import java.util.List;

import dev.jorik.timestamp.model.entities.TimeStamp;
import dev.jorik.timestamp.model.handlers.DbHandler;

public class DbIteractor implements Model{

    @Override
    public long createItem(TimeStamp newTimeStamp) {
        return App.getDbHandler().createItem(newTimeStamp);
    }

    @Override
    public boolean refreshItem(TimeStamp timeStamp) {
        return App.getDbHandler().refreshItem(timeStamp);
    }

    @Override
    public List<TimeStamp> readAllItems() {
        return App.getDbHandler().readAllItems();
    }

    @Override
    public int getRowsCount() {
        return App.getDbHandler().getRowsCount();
    }

    @Override
    public int deleteAllItems() {
        return App.getDbHandler().deleteAllItems();
    }
}
