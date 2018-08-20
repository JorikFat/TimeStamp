package dev.jorik.timestamp.presenter;

import dev.jorik.timestamp.SettingContract;

public class SettingPresenter implements SettingContract.Presenter{

    private SettingContract.Model model;
    private SettingContract.View view;

    public SettingPresenter(SettingContract.View view, SettingContract.Model model) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void viewIsReady() {
        view.setSwitchValue(model.readSetting());
    }

    @Override
    public void checkedChange(boolean newValue) {
        model.writeSetting(newValue);
    }
}
