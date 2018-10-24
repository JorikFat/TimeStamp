package dev.jorik.timestamp.presenter;

import android.content.Context;
import android.view.MenuItem;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Calendar;
import java.util.List;

import dev.jorik.timestamp.MainView;
import dev.jorik.timestamp.R;
import dev.jorik.timestamp.Utils.DateTime;
import dev.jorik.timestamp.model.entities.TimeStamp;
import dev.jorik.timestamp.model.handlers.DbHandler;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    DbHandler dbHandler;
    TimeStamp dialogTimestamp;

//    public MainPresenter(){}

    public MainPresenter(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public void mainButtonClick(){
        TimeStamp nowTimeStamp = new TimeStamp(Calendar.getInstance().getTime());
        nowTimeStamp.setId(dbHandler.createItem(nowTimeStamp));
        getViewState().addTimeStamp(nowTimeStamp);
    }

    public void mainButtonHold(){
        getViewState().createCustomTimestamp(Calendar.getInstance().getTime());
    }

    public void clickItemList(TimeStamp timeStamp){
        dialogTimestamp = timeStamp;
        getViewState().showEditDialog(timeStamp);
    }

    public void viewCreated(){
        getViewState().showData(dbHandler.readAllItems());
    }

    public boolean selectOptionsMenu(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_mainMenu_export:
                getViewState().exportData(getExpTitle(), getExpData());
                return true;
            case R.id.item_mainMenu_deleteAll:
                getViewState().confirmDelete(dbHandler.getRowsCount());
                return true;
            default:
                return false;
        }
    }

    public void editDialogSuccess(String newName) {
        dialogTimestamp.setName(newName);
        dbHandler.refreshItem(dialogTimestamp);
        getViewState().showData(dbHandler.readAllItems());
        dialogTimestamp = null;
    }

    public void editDialogFail(){
        dialogTimestamp = null;
        getViewState().showToast(android.R.string.cancel);
    }

    public void confirmDialogSuccess(int inputRows){
        int currentRows = dbHandler.getRowsCount();
        if (inputRows == currentRows){
            dbHandler.deleteAllItems();
            getViewState().showData(dbHandler.readAllItems());
        } else {
            getViewState().showToast(R.string.str_confirmD_notEqual);
        }
    }

    public void confirmDialogFail(){
        //nothing
    }

    private String getExpData(){
        List<TimeStamp> listTimeStamp = dbHandler.readAllItems();
        StringBuilder builder = new StringBuilder();
        for (TimeStamp ts : listTimeStamp) {
            builder.append(DateTime.TIME.format(ts.getTime())).append(" - ").append(ts.getName()).append("\n");
        }
        return builder.toString();
    }

    private String getExpTitle(){
        return DateTime.DATE.format(Calendar.getInstance().getTime());
    }
}
