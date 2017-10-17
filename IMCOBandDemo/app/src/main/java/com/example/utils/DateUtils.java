package com.example.utils;

import com.App;
import com.example.imco.model.R;

/**
 * Created by mai on 17-6-26.
 */

public class DateUtils {
    // Day Flags
    public final static byte REPETITION_NULL = 0x00;
    public final static byte REPETITION_ALL = 0x7f;
    public final static byte REPETITION_MON = 0x01;
    public final static byte REPETITION_TUES = 0x02;
    public final static byte REPETITION_WED = 0x04;
    public final static byte REPETITION_THU = 0x08;
    public final static byte REPETITION_FRI = 0x10;
    public final static byte REPETITION_SAT = 0x20;
    public final static byte REPETITION_SUN = 0x40;

    public static String getDayFlagString(byte flag) {
        String flagString = "";

        if((flag & 0xff) == REPETITION_NULL
                && flagString.equals("")) {
            flagString = App.getAppContext().getString(R.string.only_once);
        }
        if((flag & 0xff) == REPETITION_ALL
                && flagString.equals("")) {
            flagString = App.getAppContext().getString(R.string.every_day);
        }
        if(flagString.equals("")) {
            if ((flag & REPETITION_MON) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.monday_week);
            }
            if ((flag & REPETITION_TUES) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.tuesday_week);
            }
            if ((flag & REPETITION_WED) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.wednesday_week);
            }
            if ((flag & REPETITION_THU) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.thursday_week);
            }
            if ((flag & REPETITION_FRI) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.friday_week);
            }
            if ((flag & REPETITION_SAT) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.saturday_week);
            }
            if ((flag & REPETITION_SUN) != 0) {
                flagString = flagString + (flagString.equals("") ? "" : ", ") + App.getAppContext().getString(R.string.sunday_week);
            }
        }
        return flagString;
    }
}
