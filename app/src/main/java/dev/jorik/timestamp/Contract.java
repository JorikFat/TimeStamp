package dev.jorik.timestamp;

import java.util.List;

import dev.jorik.timestamp.model.TimeStamp;

public interface Contract {
    interface View{
        void setData(List<TimeStamp> data);
        void addItem(TimeStamp timeStamp);
        void insertItem(TimeStamp timeStamp);
//        void createCustomItem();
//        void showEditDialog(int id_dialog);
        void showCustomDialog(int[] time);
        void showEditDialog(TimeStamp timeStamp);
    }

    interface Presenter{
        void timeStampButtonClick();
        void timeStampButtonHold();
        void createCustomItem(int[] time, String description);
        void onItemClick(TimeStamp timeStamp);
    }

    interface Model {
        long createItem(TimeStamp timeStamp);
        int updateItem(long id, TimeStamp timeStamp);
        boolean refreshItem(TimeStamp timeStamp);
        int deleteAllItems();
        int getRowsCount();
        List<TimeStamp> readAllItems();
    }
}
