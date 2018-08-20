package dev.jorik.timestamp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import dev.jorik.timestamp.model.TimeStamp;
import dev.jorik.timestamp.model.handlers.DbHandler;
import dev.jorik.timestamp.presenter.Main;

public class MainActivity extends AppCompatActivity implements Contract.View{

    public static class Const{
        public static final int EDIT_TIMESTAMP = 1;
        public static final int CUSTOM_TIMESTAMP = 2;
    }

    private Button timeStamp;
    private RecyclerView listTimestamp;
    private TimeStampAdapterRV adapterRV;
    private Contract.Model model;
    private Contract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = new DbHandler(this);
        presenter = new Main(this, model);

        timeStamp = findViewById(R.id.btn_mainA_timestamp);
        timeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.timeStampButtonClick();
            }
        });
        timeStamp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                presenter.timeStampButtonHold();
                return true;
            }
        });

        adapterRV = new TimeStampAdapterRV(this);
        listTimestamp = findViewById(R.id.rv_mainA_listTimestamp);
        listTimestamp.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listTimestamp.setLayoutManager(layoutManager);
        listTimestamp.setAdapter(adapterRV);
        adapterRV.setOnItemClickListener(new TimeStampAdapterRV.ClickListener() {
            @Override
            public void onClick(int position) {
                presenter.onItemClick(adapterRV.getElements().get(position));
            }
        });
        adapterRV.setData(model.readAllItems());//рефактор: перенести в Presenter
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.item_mainMenu_export:
                exportData();
                break;
            case R.id.item_mainMenu_deleteAll:
                confirmDelete();
                break;
        }
        return false;
    }

    @Override
    public void setData(List<TimeStamp> data) {
        adapterRV.setData(data);
    }

    @Override
    public void addItem(TimeStamp timeStamp) {
        adapterRV.add(timeStamp);
    }

    @Override
    public void insertItem(TimeStamp timeStamp) {
        adapterRV.insertItem(timeStamp);
    }

//    @Override
//    public void showEditDialog(int id_dialog){
//        switch(id_dialog){
//            case Const.CUSTOM_TIMESTAMP:
//                showCustomDialog();
//                break;
//            case Const.EDIT_TIMESTAMP:
//                break;
//        }
//    }

    @Override
    public void showCustomDialog(int[] time){
        View dialogView = initCustomView();
        final NumberPicker npHours = dialogView.findViewById(R.id.numPic_customD_hours);
        final NumberPicker npMinutes = dialogView.findViewById(R.id.numPic_customD_minutes);
        final EditText etDescription = dialogView.findViewById(R.id.et_dialogCustom_decs);
        npHours.setValue(time[0]);
        npMinutes.setValue(time[1]);

        new AlertDialog.Builder(this)
                .setTitle(R.string.str_customD_title)
                .setView(dialogView)
                .setPositiveButton(R.string.str_customD_positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hours = npHours.getValue();
                        int minutes = npMinutes.getValue();
                        String description = etDescription.getText().toString();
                        presenter.createCustomItem(new int[]{hours, minutes}, description);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    @Override
    public void showEditDialog(final TimeStamp timeStamp){
        View dialogView = getLayoutInflater().inflate(R.layout.view_editdialog, null, false);
        final EditText etDescription = dialogView.findViewById(R.id.et_dialogEdit_decs);
        ((TextView) dialogView.findViewById(R.id.tv_dialogEdit_time)).setText(TimeStamp.DATE_FORMAT.format(timeStamp.getTime()));
        etDescription.setText(timeStamp.getName());

        new AlertDialog.Builder(this)
                .setTitle(R.string.str_editD_title)
                .setView(dialogView)
                .setPositiveButton(R.string.str_all_edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //рефактор: Передать в Presenter
                        timeStamp.setName(etDescription.getText().toString());
//                        model.updateItem(timeStamp.getId(), timeStamp);
                        model.refreshItem(timeStamp);
                        adapterRV.notifyDataSetChanged();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        showToast(android.R.string.cancel);
                    }
                })
                .show();
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int resId){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    private void exportData(){//рефактор: изменить сигнатуру. Передавать данные для экспорта
        Intent exportIntent = new Intent(Intent.ACTION_SEND);
        exportIntent.putExtra(Intent.EXTRA_TEXT, getTextSheldere());
        String exportTitle = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        exportIntent.putExtra(Intent.EXTRA_TITLE, exportTitle);
        exportIntent.setType("text/plain");
        startActivity(exportIntent);
    }

    private String getTextSheldere() {
        List<TimeStamp> listTimeStamp = model.readAllItems();//рефактор: передать в Presenter
        StringBuilder builder = new StringBuilder();
        for (TimeStamp ts : listTimeStamp) {
            builder.append(TimeStamp.DATE_FORMAT.format(ts.getTime())).append(" - ").append(ts.getName()).append("\n");
        }
        return builder.toString();
    }

    private void confirmDelete(){
        final int countRows = model.getRowsCount();//рефактор: передать в Presenter
        final View dialogView = getLayoutInflater().inflate(R.layout.view_dialogconfirm, null, false);
        ((TextView) dialogView.findViewById(R.id.tv_confirmD_countRows)).setText(String.valueOf(countRows));
        new AlertDialog.Builder(this)
                .setTitle(R.string.str_confirmD_confirm)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    //рефактор: передать в Presenter
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText inputCount = dialogView.findViewById(R.id.et_confirmD_inputRows);
                        if (Integer.parseInt(inputCount.getText().toString()) == countRows)//рефактор: передать в Presenter
                            deleteData();
                        else
                            showToast(getString(R.string.str_confirmD_notEqual));
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void deleteData(){
        model.deleteAllItems();//рефактор: передать в Presenter
        adapterRV.setData(model.readAllItems());
        adapterRV.notifyDataSetChanged();
    }


//    @Override
//    public void createCustomItem() {
//        View dialogView = initCustomView();
//        ((NumberPicker) dialogView.findViewById(R.id.numPic_customD_minutes)).setValue();
//        ((NumberPicker) dialogView.findViewById(R.id.numPic_customD_hours)).setValue();
//    }

    private View initCustomView(){
        View rView;
        rView = getLayoutInflater().inflate(R.layout.view_customdialog, null, false);
        final NumberPicker npHours = rView.findViewById(R.id.numPic_customD_hours);
        final NumberPicker npMinutes = rView.findViewById(R.id.numPic_customD_minutes);
//        final EditText etDescription = rView.findViewById(R.id.et_dialogCustom_decs);
        npHours.setMinValue(0);
        npHours.setMaxValue(23);
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);
        npMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
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
        return rView;
    }

    //рефактор
//    private void createCustomTimestamp(Date date){
//        View view = getLayoutInflater().inflate(R.layout.view_customdialog, null, false);
//        final NumberPicker npHours = view.findViewById(R.id.numPic_customD_hours);
//        final NumberPicker npMinutes = view.findViewById(R.id.numPic_customD_minutes);
//        final EditText etDescription = view.findViewById(R.id.et_dialogCustom_decs);
//        npHours.setMinValue(0);
//        npHours.setMaxValue(23);
//        npHours.setValue(getDateValue(date, Calendar.HOUR_OF_DAY));
//        npMinutes.setMinValue(0);
//        npMinutes.setMaxValue(59);
//        npMinutes.setValue(getDateValue(date, Calendar.MINUTE));
//        npMinutes.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                if (oldVal == 59 && newVal == 0) {
//                    npHours.setValue(npHours.getValue()+1);
//                }
//                if (oldVal == 0 && newVal == 59) {
//                    npHours.setValue(npHours.getValue()-1);
//                }
//            }
//        });
//        new AlertDialog.Builder(this)
//                .setTitle(R.string.str_customD_title)
//                .setView(view)
//                .setPositiveButton(R.string.str_customD_positiveButton, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        int hours = npHours.getValue();
//                        int minutes = npMinutes.getValue();
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.set(Calendar.HOUR_OF_DAY, hours);
//                        calendar.set(Calendar.MINUTE, minutes);
//                        calendar.set(Calendar.SECOND, 0);
//                        TimeStamp customTimeStamp = new TimeStamp(calendar.getTime());
//                        String description = etDescription.getText().toString();
//                        if (description.length() > 0){
//                            customTimeStamp.setName(description);
//                        }
//                        timeStamp.setId((int) model.createItem(customTimeStamp));
//                        adapterRV.insertItem(customTimeStamp);
//                    }
//                })
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })
//                .show();
//
//    }
}
