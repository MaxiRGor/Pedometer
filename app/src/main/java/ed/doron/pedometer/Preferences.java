package ed.doron.pedometer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Preferences {
    // Identify Shared Preference Store
    final static String PREFS_NAME = "pedometerPreferences";

    //private final static String IS_SERVICE_RUNNING = "isServiceRunning";
    private final static String USER_DOCUMENT_ID = "userDocumentId";
    private final static String STEP_COUNT = "stepCount";
    private final static String STEP_LENGTH = "stepLength";
    private final static String STEP_LIMIT = "stepLimit";
    private final static String IS_DAY_MODE = "isDayMode";

/*
    // Should the Step Counting Service be running?
    public static boolean getServiceRun(Context context) {
        return getBooleanValue(context, IS_SERVICE_RUNNING);
    }

    // Should the Step Counting Service be running?
    public static void setServiceRun(Context context, boolean running) {
        setBooleanValue(context, IS_SERVICE_RUNNING, running);
    }
*/

    static String getUserDocumentId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(USER_DOCUMENT_ID, "");
    }

    static void setUserDocumentId(Context context, String userDocumentId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Preferences.USER_DOCUMENT_ID, userDocumentId);
        prefsEditor.apply();
    }

    public static int getStepCount(Context context) {
        return getIntValue(context, STEP_COUNT);
    }

    static void setStepCount(Context context, Integer steps) {
        setIntValue(context, STEP_COUNT, steps);
        Log.d("myLogs", "saving steps " + String.valueOf(steps));
    }

    public static int getStepLength(Context context) {
        return getIntValue(context, STEP_LENGTH);
    }

    static void setStepLength(Context context, Integer length) {
        setIntValue(context, STEP_LENGTH, length);
    }

    public static int getStepLimit(Context context) {
        return getIntValue(context, STEP_LIMIT);
    }

    static void setStepLimit(Context context, Integer limit) {
        setIntValue(context, STEP_LIMIT, limit);
    }

    public static boolean getDayMode(Context context) {
        return getBooleanValue(context);
    }

    static void setDayMode(Context context, boolean isDayMode) {
        setBooleanValue(context, isDayMode);
    }

    private static boolean getBooleanValue(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getBoolean(Preferences.IS_DAY_MODE, true);
    }

    private static void setBooleanValue(Context context, Boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(Preferences.IS_DAY_MODE, value);
        prefsEditor.apply();
    }

    private static int getIntValue(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getInt(key, 222);
    }

    private static void setIntValue(Context context, String key, Integer value) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }

    public static void updateInfoOnFirestore(Context context) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            HashMap<String, Object> data = new HashMap<>();
            data.put(context.getString(R.string.firestore_field_is_day_mode), getDayMode(context));
            data.put(context.getString(R.string.firestore_field_step_count), getStepCount(context));
            data.put(context.getString(R.string.firestore_field_step_length), getStepLength(context));
            data.put(context.getString(R.string.firestore_field_step_limit), getStepLimit(context));
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection(context.getString(R.string.firestore_collection_user_settings))
                    .document(getUserDocumentId(context));
            documentReference.set(data);
        }
    }
}
