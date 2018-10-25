package dev.jorik.timestamp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.Utils.DateTime;
import dev.jorik.timestamp.model.entities.TimeStamp;
import dev.jorik.timestamp.model.handlers.DbHandler;
import dev.jorik.timestamp.presenter.MainPresenter;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    private Button timeStamp;
    private RecyclerView listTimestamp;
    private TimeStampAdapterRV adapterRV;
    @InjectPresenter MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.createDbHandler(this);

        adapterRV = new TimeStampAdapterRV();
        adapterRV.setOnItemClickListener(new TimeStampAdapterRV.ClickListener() {
            @Override
            public void onClick(int position) {
                presenter.clickItemList(adapterRV.tempGetElements().get(position));
            }
        });
        initViews();
    }

    private void initViews(){
        timeStamp = findViewById(R.id.btn_mainA_timestamp);
        timeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.mainButtonClick();
            }
        });
        timeStamp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                presenter.mainButtonHold();
                return true;
            }
        });
        listTimestamp = findViewById(R.id.rv_mainA_listTimestamp);
        listTimestamp.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listTimestamp.setLayoutManager(layoutManager);
        listTimestamp.setAdapter(adapterRV);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.viewCreated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.selectOptionsMenu(item);
    }

    @Override
    public void showData(List<TimeStamp> data) {
        adapterRV.setData(data);
    }

    @Override
    public void addTimeStamp(TimeStamp timeStamp) {
        adapterRV.add(timeStamp);
    }

    @Override
    public void updateData() {
        adapterRV.notifyDataSetChanged();
    }

    @Override
    //todo вынести в отдельный Presenter
    public void showEditDialog(final TimeStamp timeStamp){
        View dialogView = getLayoutInflater().inflate(R.layout.view_editdialog, null, false);
        final EditText etDescription = dialogView.findViewById(R.id.et_dialogEdit_decs);
        ((TextView) dialogView.findViewById(R.id.tv_dialogEdit_time)).setText(DateTime.TIME.format(timeStamp.getTime()));
        etDescription.setText(timeStamp.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.str_editD_title)
                .setView(dialogView)
                .setPositiveButton(R.string.str_all_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        timeStamp.setName(etDescription.getText().toString());
//                        dbHandler.updateItem(timeStamp.getId(), timeStamp);
//                        dbHandler.refreshItem(timeStamp);
//                        adapterRV.notifyDataSetChanged();

                        presenter.editDialogSuccess(etDescription.getText().toString());
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast(android.R.string.cancel);
                        presenter.editDialogFail();
                    }
                })
                .show();
    }

    @Override
    public void showToast(String text){
        //Todo расширить метод закрытием открытого Toast
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showToast(int resId){
        //Todo расширить метод закрытием открытого Toast
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void exportData(String exportTitle, String exportData){
        Intent exportIntent = new Intent(Intent.ACTION_SEND);
        exportIntent.putExtra(Intent.EXTRA_TEXT, exportData);
        exportIntent.putExtra(Intent.EXTRA_TITLE, exportTitle);
        exportIntent.setType("text/plain");
        startActivity(exportIntent);
    }

    @Override
    //todo вынести в отдельный Presenter
    public void confirmDelete(final int countRows){
        final View dialogView = getLayoutInflater().inflate(R.layout.view_dialogconfirm, null, false);
        ((TextView) dialogView.findViewById(R.id.tv_confirmD_countRows)).setText(String.valueOf(countRows));

        new AlertDialog.Builder(this)
                .setTitle(R.string.str_confirmD_confirm)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = ((EditText) dialogView.findViewById(R.id.et_confirmD_inputRows))
                                .getText().toString();
                        int inputRows = Integer.parseInt(text);
                        presenter.confirmDialogConfirm(inputRows);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.confirmDialogCancel();
                        dialog.cancel();
                    }
                })
                .show();
    }

    //todo вынести в отдельный Presenter
    @Override
    public void createCustomTimestamp(Date date){
        Calendar tCalendar = Calendar.getInstance();
        View view = getLayoutInflater().inflate(R.layout.view_customdialog, null, false);
        final NumberPicker npHours = view.findViewById(R.id.numPic_customD_hours);
        final NumberPicker npMinutes = view.findViewById(R.id.numPic_customD_minutes);
        final EditText etDescription = view.findViewById(R.id.et_dialogCustom_decs);
//        npHours.setMinValue(0);
//        npHours.setMaxValue(23);
        //todo вынести в CalendarUtils
        npHours.setMinValue(tCalendar.getActualMinimum(Calendar.HOUR_OF_DAY));
        npHours.setMaxValue(tCalendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        npHours.setValue(getDateValue(date, Calendar.HOUR_OF_DAY));
//        npMinutes.setMinValue(0);
//        npMinutes.setMaxValue(59);
        //todo вынести в CalendarUtils
        npMinutes.setMinValue(tCalendar.getActualMinimum(Calendar.MINUTE));
        npMinutes.setMaxValue(tCalendar.getActualMaximum(Calendar.MINUTE));
        npMinutes.setValue(getDateValue(date, Calendar.MINUTE));
        npMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //todo Вынести в Presenter????
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (oldVal == 59 && newVal == 0) {
                    npHours.setValue(npHours.getValue()+1);
                }
                if (oldVal == 0 && newVal == 59) {
                    npHours.setValue(npHours.getValue()-1);
                }
            }
        });
        new AlertDialog.Builder(this)
                .setTitle(R.string.str_customD_title)
                .setView(view)
                .setPositiveButton(R.string.str_customD_positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hours = npHours.getValue();
                        int minutes = npMinutes.getValue();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, hours);
                        calendar.set(Calendar.MINUTE, minutes);
                        calendar.set(Calendar.SECOND, 0);

//                        TimeStamp customTimeStamp = new TimeStamp(calendar.getTime());
                        String description = etDescription.getText().toString();

                        presenter.customDialogConfirm(calendar.getTime(), description);

//                        if (description.length() > 0){
//                            customTimeStamp.setName(description);
//                        }
//                        timeStamp.setId((int) dbHandler.createItem(customTimeStamp));
//                        adapterRV.insertItem(customTimeStamp);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.customDialogCancel();
                        dialog.cancel();
                    }
                })
                .show();

    }

    //todo вынести в CalendarUtils
    private int getDateValue(Date date, int valueConst){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(valueConst);
    }
}
