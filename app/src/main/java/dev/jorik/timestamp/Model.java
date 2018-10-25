package dev.jorik.timestamp;

import java.util.List;

import dev.jorik.timestamp.model.entities.TimeStamp;

public interface Model {
    long createItem(TimeStamp newTimeStamp);
    boolean refreshItem(TimeStamp timeStamp);
    List<TimeStamp> readAllItems();
    int getRowsCount();
    int deleteAllItems();
}
