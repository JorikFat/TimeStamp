package dev.jorik.timestamp.presenter;

import android.content.DialogInterface;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.DbInteract;
import dev.jorik.timestamp.MainView;
import dev.jorik.timestamp.Model;
import dev.jorik.timestamp.R;
import dev.jorik.timestamp.Utils.CalendarUtils;
import dev.jorik.timestamp.Utils.DateTimeUtils;
import dev.jorik.timestamp.model.entities.TimeStamp;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private TimeStamp dialogTimestamp;
    private Model model = new DbInteract();

    public void mainButtonClick(){
        //todo заменить на insert
        getViewState().addTimeStamp(model.createItem(CalendarUtils.now(), ""));
    }

    public void mainButtonHold(){
        getViewState().createCustomTimestamp(Calendar.getInstance().getTime());
    }

    public void clickItemList(TimeStamp timeStamp){
        dialogTimestamp = timeStamp;
        getViewState().showEditDialog(timeStamp);
    }

    public void editDialogConfirm(String newName) {
        dialogTimestamp.setName(newName);
        model.refreshItem(dialogTimestamp);
        //todo обновить только 1 элемент, а не все
        getViewState().showData(model.readAllItems());
        dialogTimestamp = null;
    }

    public void editDialogCancel(){
        dialogTimestamp = null;
        getViewState().showToast(android.R.string.cancel);
    }

    public void viewCreated(){
        getViewState().showData(model.readAllItems());
    }

    public boolean selectOptionsMenu(int id) {
        switch (id){
            case R.id.item_mainMenu_export:
                getViewState().exportData(getExpTitle(), getExpData());
                return true;
            case R.id.item_mainMenu_deleteAll:
                getViewState().confirmDelete(model.getRowsCount());
                return true;
            default:
                return false;
        }
    }

    public void confirmDialogConfirm(int inputRows){
        int currentRows = model.getRowsCount();
        if (inputRows == currentRows){
            model.deleteAllItems();
            //todo сделать метод showEmptyList()
            getViewState().showData(new ArrayList<TimeStamp>());
        } else {
            getViewState().showToast(R.string.str_confirmD_notEqual);
        }
    }

    public void dialogCancel(DialogInterface dialog){
        dialog.cancel();
    }

    private String getExpData(){
        List<TimeStamp> listTimeStamp = model.readAllItems();
        StringBuilder builder = new StringBuilder();
        for (TimeStamp ts : listTimeStamp) {
            builder.append(DateTimeUtils.TIME.format(ts.getTime()))
                    .append(" - ")
                    .append(ts.getName())
                    .append("\n");
        }
        return builder.toString();
    }

    private String getExpTitle(){
        //todo исправить заголовок
        /*
        * сейчас дата заголовка берется из момента, когда выполняется export
        * т.е. если экспорт делается на следущий день, после создания меток
        * заголовок не будет вчерашним.
        * ВАРИАНТ:
        * брать заголовок из первой записи timestamp
        * */
        return DateTimeUtils.DATE.format(Calendar.getInstance().getTime());
    }

    public void customDialogConfirm(Date time, String name) {
        model.createItem(time, name);
        //todo добавлять не все записи, а только ту, что создали
        getViewState().showData(model.readAllItems());
    }
}
