package dev.jorik.timestamp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import dev.jorik.timestamp.model.handlers.PreferencesHandler;
import dev.jorik.timestamp.presenter.SettingPresenter;

public class SettingsActivity extends AppCompatActivity implements SettingContract.View{

    private SwitchCompat secondsSwitch;
    private SettingContract.Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        presenter = new SettingPresenter(this, new PreferencesHandler(prefs));

        bindView();
    }

    private void bindView(){
        secondsSwitch = findViewById(R.id.swt_settingA_useSeconds);
        secondsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.checkedChange(isChecked);
            }
        });
    }

    @Override
    protected void onResume() {
        presenter.viewIsReady();
        super.onResume();
    }


    @Override
    public void setSwitchValue(boolean value) {
        secondsSwitch.setChecked(value);
    }
}
