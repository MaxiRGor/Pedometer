package eduard.doron.pedometer;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import eduard.doron.pedometer.data.Preferences;
import eduard.doron.pedometer.models.AppDatabase;
import eduard.doron.pedometer.models.DayResult;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        checkUserAuthorization();
    }

    private void checkUserAuthorization() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            createSignInIntent();
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        //finish current LoginActivity
        finish();
    }

    private void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.AppTheme_Day)
                        .build(),
                RC_SIGN_IN);
    }


    // [START auth_fui_result]
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (response != null) {
                if (resultCode == RESULT_OK) {
                    // Successfully signed in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        checkIfUserDocumentExistsOnFirestore(user.getUid());
                    }
                    // ...
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    // ...
                    if (response.getError() != null && response.getError().getLocalizedMessage() != null)
                        // I made a loop
                        createSignInIntent();
                }
            }
        }
    }


    private void syncSparedPreferences(String uid, boolean isDayMode, long stepCount, long stepLength, long stepLimit) {
        Preferences.setUserDocumentId(LoginActivity.this, uid);
        Preferences.setDayMode(LoginActivity.this, isDayMode);
        Preferences.setStepCount(LoginActivity.this, (int) stepCount);
        Preferences.setStepLength(LoginActivity.this, (int) stepLength);
        Preferences.setStepLimit(LoginActivity.this, (int) stepLimit);

        startMainActivity();
    }

    private void checkIfUserDocumentExistsOnFirestore(String uid) {
        FirebaseFirestore.getInstance()
                .collection(this.getString(R.string.firestore_collection_user_settings))
                .document(uid)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().get(this.getString(R.string.firestore_field_is_day_mode)) != null) {
                DocumentSnapshot snapshot = task.getResult();
                syncSparedPreferences(uid
                        , (boolean) snapshot.get(this.getString(R.string.firestore_field_is_day_mode))
                        , (long) snapshot.get(this.getString(R.string.firestore_field_step_count))
                        , (long) snapshot.get(this.getString(R.string.firestore_field_step_length))
                        , (long) snapshot.get(this.getString(R.string.firestore_field_step_limit))
                );
                syncLocalDatabase(uid);
            } else {
                syncSparedPreferences(uid, true, 0, 60, 6000);
                generateRandomStatistics();
            }
        });
    }

    private void syncLocalDatabase(String uid) {
        FirebaseFirestore.getInstance()
                .collection(getString(R.string.firestore_collection_user_results))
                .whereEqualTo(getString(R.string.firestore_field_user_uid), uid)
                .get().addOnCompleteListener(task -> {
            if (task.getResult() != null && task.getResult().size() != 0) {
                for (DocumentSnapshot snapshot : task.getResult()) {
                    AppDatabase.getDatabase(LoginActivity.this).getDayResultDao().insertResult(snapshot.toObject(DayResult.class));
                }
            }
        });
    }

    private void generateRandomStatistics() {
        long time = new Date().getTime();
        long oneDayInMs = 1000 * 60 * 60 * 24;
        int generatingDayCount = 31;
        for (; generatingDayCount > 0; generatingDayCount--) {
            DayResult result = new DayResult(time - (oneDayInMs * generatingDayCount)
                    , 60
                    , (new Random().nextInt(5) + 5) * 1000
                    , new Random().nextInt(6000) + 1000
                    , false);
            AppDatabase.getDatabase(this).getDayResultDao()
                    .insertResult(result);
        }

    }

}
