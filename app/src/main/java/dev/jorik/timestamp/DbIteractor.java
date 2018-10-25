package dev.jorik.timestamp;

import java.util.List;

import dev.jorik.timestamp.model.entities.TimeStamp;
import dev.jorik.timestamp.model.handlers.DbHandler;

public class DbIteractor implements Model{

    @Override
    public long createItem(TimeStamp newTimeStamp) {
        //todo в обработчик передавать только данные для записи
        return App.getDbHandler().createItem(newTimeStamp);
    }

    @Override
    public boolean refreshItem(TimeStamp timeStamp) {
        //todo в обработчик передавать только данные для обновления
        return App.getDbHandler().refreshItem(timeStamp);
    }

    @Override
    public List<TimeStamp> readAllItems() {
        //todo обработчик возвращает только данные из базы, не объекты
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
