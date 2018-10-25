package dev.jorik.timestamp;

import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.model.entities.TimeStamp;

public interface Model {
    TimeStamp createItem(Date time, String name);
    boolean refreshItem(TimeStamp timeStamp);
    List<TimeStamp> readAllItems();
    int getRowsCount();
    int deleteAllItems();
}
