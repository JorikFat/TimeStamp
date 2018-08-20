package dev.jorik.timestamp.presenter;

import java.util.Calendar;
import java.util.Date;

import dev.jorik.timestamp.Contract;
import dev.jorik.timestamp.model.TimeStamp;

public class Main implements Contract.Presenter{
    private Contract.Model model;
    private Contract.View view;


    public Main(Contract.View view, Contract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void timeStampButtonClick() {
        TimeStamp addTimestamp = new TimeStamp(Calendar.getInstance().getTime());
        addTimestamp.setId(model.createItem(addTimestamp));//рефактор: сделать асинхронно
        view.addItem(addTimestamp);
    }

    @Override
    public void timeStampButtonHold() {
        Date date = Calendar.getInstance().getTime();
        int[] time = new int[2];
        time[0] = getDateValue(date, Calendar.HOUR_OF_DAY);
        time[1] = getDateValue(date, Calendar.MINUTE);
        view.showCustomDialog(time);
//        view.showEditDialog(MainActivity.Const.CUSTOM_TIMESTAMP);
    }

    @Override
    public void createCustomItem(int[] time, String description) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, time[0]);
        calendar.set(Calendar.MINUTE, time[1]);
        calendar.set(Calendar.SECOND, 0);
        TimeStamp customTimeStamp = new TimeStamp(calendar.getTime());
        if (description.trim().length() > 0) customTimeStamp.setName(description);
        customTimeStamp.setId(model.createItem(customTimeStamp));//рефактор: сделать асинхронно
        view.insertItem(customTimeStamp);
    }

    @Override
    public void onItemClick(TimeStamp timeStamp) {
        view.showEditDialog(timeStamp);
    }

    private int getDateValue(Date date, int valueConst){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(valueConst);
    }
}
