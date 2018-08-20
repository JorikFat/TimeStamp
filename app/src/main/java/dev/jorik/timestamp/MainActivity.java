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
import java.util.Date;
import java.util.List;

import dev.jorik.timestamp.model.handlers.DbHandler;

public class MainActivity extends AppCompatActivity {

    private Button timeStamp;
    private RecyclerView listTimestamp;
    private TimeStampAdapterRV adapterRV;
    private DbHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DbHandler(this);//передать в presenter

        timeStamp = findViewById(R.id.btn_mainA_timestamp);
        timeStamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//передать в presenter
                addTimeStamp(Calendar.getInstance().getTime());
            }
        });
        timeStamp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {//передать в presenter
                createCustomTimestamp(Calendar.getInstance().getTime());
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
            public void onClick(int position) {//передать в presenter
                showEditDialog(adapterRV.tempGetElements().get(position));
            }
        });
        adapterRV.setData(dbHandler.readAllItems());
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
            case R.id.item_mainMenu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return false;
    }

    private void addTimeStamp(Date date) {
        TimeStamp addTimestamp = new TimeStamp(date);
        addTimestamp.setId(dbHandler.createItem(addTimestamp));
        adapterRV.add(addTimestamp);
    }

    private void showEditDialog(final TimeStamp timeStamp){//передать в presenter
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
                        timeStamp.setName(etDescription.getText().toString());
//                        dbHandler.updateItem(timeStamp.getId(), timeStamp);
                        dbHandler.refreshItem(timeStamp);
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

    private void showDialog(AlertDialog dialog) {
        dialog.show();
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int resId){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    private void exportData(){
        Intent exportIntent = new Intent(Intent.ACTION_SEND);
        exportIntent.putExtra(Intent.EXTRA_TEXT, getTextSheldere());
        String exportTitle = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
        exportIntent.putExtra(Intent.EXTRA_TITLE, exportTitle);
        exportIntent.setType("text/plain");
        startActivity(exportIntent);
    }

    private String getTextSheldere() {
        List<TimeStamp> listTimeStamp = dbHandler.readAllItems();
        StringBuilder builder = new StringBuilder();
        for (TimeStamp ts : listTimeStamp) {
            builder.append(TimeStamp.DATE_FORMAT.format(ts.getTime())).append(" - ").append(ts.getName()).append("\n");
        }
        return builder.toString();
    }

    private void confirmDelete(){
        final int countRows = dbHandler.getRowsCount();
        final View dialogView = getLayoutInflater().inflate(R.layout.view_dialogconfirm, null, false);
        ((TextView) dialogView.findViewById(R.id.tv_confirmD_countRows)).setText(String.valueOf(countRows));
        new AlertDialog.Builder(this)
                .setTitle(R.string.str_confirmD_confirm)
                .setView(dialogView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText inputCount = dialogView.findViewById(R.id.et_confirmD_inputRows);
                        if (Integer.parseInt(inputCount.getText().toString()) == countRows)
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
        dbHandler.deleteAllItems();
        adapterRV.setData(dbHandler.readAllItems());
        adapterRV.notifyDataSetChanged();
    }

    //рефактор
    private void createCustomTimestamp(Date date){
        View view = getLayoutInflater().inflate(R.layout.view_customdialog, null, false);
        final NumberPicker npHours = view.findViewById(R.id.numPic_customD_hours);
        final NumberPicker npMinutes = view.findViewById(R.id.numPic_customD_minutes);
        final EditText etDescription = view.findViewById(R.id.et_dialogCustom_decs);
        npHours.setMinValue(0);
        npHours.setMaxValue(23);
        npHours.setValue(getDateValue(date, Calendar.HOUR_OF_DAY));
        npMinutes.setMinValue(0);
        npMinutes.setMaxValue(59);
        npMinutes.setValue(getDateValue(date, Calendar.MINUTE));
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
                        TimeStamp customTimeStamp = new TimeStamp(calendar.getTime());
                        String description = etDescription.getText().toString();
                        if (description.length() > 0){
                            customTimeStamp.setName(description);
                        }
                        timeStamp.setId((int) dbHandler.createItem(customTimeStamp));
                        adapterRV.insertItem(customTimeStamp);
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

    private int getDateValue(Date date, int valueConst){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(valueConst);
    }
}
