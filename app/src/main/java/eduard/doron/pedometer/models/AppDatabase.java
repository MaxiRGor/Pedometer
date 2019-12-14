package eduard.doron.pedometer.models;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import eduard.doron.pedometer.interfaces.DayResultDao;

@Database(entities = {DayResult.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, AppDatabase.class, "database").allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract DayResultDao getDayResultDao();

}
