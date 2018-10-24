package dev.jorik.timestamp.presenter;

import android.content.Context;
import android.view.MenuItem;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Calendar;

import dev.jorik.timestamp.MainView;
import dev.jorik.timestamp.R;
import dev.jorik.timestamp.model.entities.TimeStamp;
import dev.jorik.timestamp.model.handlers.DbHandler;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    DbHandler dbHandler;

//    public MainPresenter(){}

    public MainPresenter(DbHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public void mainButtonClick(){
        getViewState().addTimeStamp(Calendar.getInstance().getTime());
    }

    public void mainButtonHold(){
        getViewState().createCustomTimestamp(Calendar.getInstance().getTime());
    }

    public void clickItemList(TimeStamp timeStamp){
        getViewState().showEditDialog(timeStamp);
    }

    public void viewCreated(){
        getViewState().showData();
    }

    public boolean selectOptionsMenu(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_mainMenu_export:
                getViewState().exportData();
                return true;
            case R.id.item_mainMenu_deleteAll:
                getViewState().confirmDelete();
                return true;
            default:
                return false;
        }
    }

}
