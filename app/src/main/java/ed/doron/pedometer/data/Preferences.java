package ed.doron.pedometer.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import ed.doron.pedometer.R;

public class Preferences {
    // Identify Shared Preference Store
    private final static String PREFS_NAME = "pedometerPreferences";

    private final static String USER_DOCUMENT_ID = "userDocumentId";
    private final static String STEP_COUNT = "stepCount";
    private final static String STEP_LENGTH = "stepLength";
    private final static String STEP_LIMIT = "stepLimit";
    private final static String IS_DAY_MODE = "isDayMode";

    private static String getUserDocumentId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(USER_DOCUMENT_ID, "");
    }

    public static void setUserDocumentId(Context context, String userDocumentId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Preferences.USER_DOCUMENT_ID, userDocumentId);
        prefsEditor.apply();
    }

    public static int getStepCount(Context context) {
        return getIntValue(context, STEP_COUNT);
    }

    public static void setStepCount(Context context, Integer steps) {
        setIntValue(context, STEP_COUNT, steps);
    }

    public static int getStepLength(Context context) {
        return getIntValue(context, STEP_LENGTH);
    }

    public static void setStepLength(Context context, Integer length) {
        setIntValue(context, STEP_LENGTH, length);
    }

    public static int getStepLimit(Context context) {
        return getIntValue(context, STEP_LIMIT);
    }

    public static void setStepLimit(Context context, Integer limit) {
        setIntValue(context, STEP_LIMIT, limit);
    }

    public static boolean getDayMode(Context context) {
        return getBooleanValue(context);
    }

    public static void setDayMode(Context context, boolean isDayMode) {
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

    public static void updateUserSettingsOnFirestore(Context context) {
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
