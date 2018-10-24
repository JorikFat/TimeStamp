package dev.jorik.timestamp;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.model.entities.TimeStamp;

@StateStrategyType(SkipStrategy.class)
public interface MainView extends MvpView{
    void showToast(String text);
    void showToast(int resText);
    void addTimeStamp(TimeStamp nowTimestamp);
    void updateData();
    void createCustomTimestamp(Date date);
    void showEditDialog(TimeStamp timeStamp);
    void showData(List<TimeStamp> data);
    void exportData(String exportTitle, String exportData);
    void confirmDelete(int countRows);
}
