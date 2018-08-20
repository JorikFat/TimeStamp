package dev.jorik.timestamp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import dev.jorik.timestamp.model.handlers.PreferencesHandler;
import dev.jorik.timestamp.presenter.SettingPresenter;

public class SettingsActivity extends AppCompatActivity implements SettingContract.View{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SettingPresenter settingPresenter = new SettingPresenter();
        PreferencesHandler preferencesHandler = new PreferencesHandler();
    }
}
