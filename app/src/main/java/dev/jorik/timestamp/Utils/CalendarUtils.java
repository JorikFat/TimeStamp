package dev.jorik.timestamp.Utils;

import java.util.Calendar;
import java.util.Date;

import static dev.jorik.timestamp.Utils.CalendarUtils.Border.*;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

public class CalendarUtils {
    public enum Border{
        MAX, MIN
    }

    public static Calendar getCalendar(int hours, int minutes, int seconds) {
        Calendar rCalendar = Calendar.getInstance();
        rCalendar.set(HOUR_OF_DAY, hours);
        rCalendar.set(MINUTE, minutes);
        rCalendar.set(SECOND, seconds);
        return rCalendar;
    }

    public static int getValue(int field, Border border) {
        Calendar calendar = Calendar.getInstance();
        if (border == MAX) return calendar.getActualMaximum(field);
        else if (border == MIN) return calendar.getActualMinimum(field);
        else return calendar.get(field);
    }

    public static Date now(){
        return Calendar.getInstance().getTime();
    }
}
