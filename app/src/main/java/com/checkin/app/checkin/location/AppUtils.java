package com.checkin.app.checkin.location;

import android.content.Context;
import android.provider.Settings;


public class AppUtils {
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        try {
            locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF;
    }

    public class LocationConstants {
        public static final int SUCCESS_RESULT = 0;

        public static final int FAILURE_RESULT = 1;

        public static final String RECEIVER = "RECEIVER";

        public static final String RESULT_DATA_KEY = "RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = "LOCATION_DATA_EXTRA";
        public static final String LOCATION_DATA_AREA = "LOCATION_DATA_AREA";
        public static final String LOCATION_DATA_CITY = "LOCATION_DATA_CITY";
        public static final String LOCATION_DATA_STREET = "LOCATION_DATA_STREET";
        public static final String LOCATION_DATA_ADDRESS_BUNDLE = "LOCATION_DATA_ADDRESS_BUNDLE";
        public static final String LOCATION_DATA_LOCATION_BUNDLE = "LOCATION_DATA_LOCATION_BUNDLE";
    }
}