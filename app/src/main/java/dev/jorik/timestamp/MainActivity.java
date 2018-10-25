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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.Utils.CalendarUtils;
import dev.jorik.timestamp.Utils.DateTimeUtils;
import dev.jorik.timestamp.model.entities.TimeStamp;
import dev.jorik.timestamp.presenter.MainPresenter;

import static java.util.Calendar.HOUR_OF_DAY;

public class MainActivity extends MvpAppCompatActivity implements MainView {

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
        Button timeStamp = findViewById(R.id.btn_mainA_timestamp);
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
        RecyclerView recyclerView = findViewById(R.id.rv_mainA_listTimestamp);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterRV);
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
        return presenter.selectOptionsMenu(item.getItemId());
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
        ((TextView) dialogView.findViewById(R.id.tv_dialogEdit_time)).setText(DateTimeUtils.TIME.format(timeStamp.getTime()));
        etDescription.setText(timeStamp.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.str_editD_title)
                .setView(dialogView)
                .setPositiveButton(R.string.str_all_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.editDialogConfirm(etDescription.getText().toString());
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        presenter.editDialogCancel();
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
                        presenter.dialogCancel(dialog);
                    }
                })
                .show();
    }

    //todo вынести в отдельный Presenter
    @Override
    public void createCustomTimestamp(Date date){
        View view = getLayoutInflater().inflate(R.layout.view_customdialog, null, false);
        final NumberPicker npHours = view.findViewById(R.id.numPic_customD_hours);
        final NumberPicker npMinutes = view.findViewById(R.id.numPic_customD_minutes);
        final EditText etDescription = view.findViewById(R.id.et_dialogCustom_decs);
        npHours.setMinValue(CalendarUtils.getValue(HOUR_OF_DAY, CalendarUtils.Border.MIN));
        npHours.setMaxValue(CalendarUtils.getValue(HOUR_OF_DAY, CalendarUtils.Border.MAX));
        npHours.setValue(CalendarUtils.getValue(HOUR_OF_DAY, null));
        npMinutes.setMinValue(CalendarUtils.getValue(Calendar.MINUTE, CalendarUtils.Border.MIN));
        npMinutes.setMaxValue(CalendarUtils.getValue(Calendar.MINUTE, CalendarUtils.Border.MAX));
        npMinutes.setValue(CalendarUtils.getValue(HOUR_OF_DAY, null));
        npMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            //todo Вынести в Presenter диалога
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
                        Calendar calendar = CalendarUtils.getCalendar(hours, minutes, 0);
                        String name = etDescription.getText().toString();
                        presenter.customDialogConfirm(calendar.getTime(), name);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.dialogCancel(dialog);
                    }
                })
                .show();
    }
}
