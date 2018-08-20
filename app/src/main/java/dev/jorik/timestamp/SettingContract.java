package dev.jorik.timestamp;

public interface SettingContract {

    public interface Model{
//        void writeSetting(String key, boolean value);
//        boolean readSetting(String key);
        void writeSetting(boolean value);
        boolean readSetting();
    }

    public interface View{
        void setSwitchValue(boolean value);
    }

    public interface Presenter{
        void viewIsReady();
        void checkedChange(boolean newValue);
    }
}
