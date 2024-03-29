package eduard.doron.pedometer.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import eduard.doron.pedometer.models.DayResult;

@Dao
public interface DayResultDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertResult(DayResult result);

    @Query("SELECT * FROM dayresult")
    List<DayResult> getAllResults();

    @Query("SELECT * FROM dayresult WHERE synced='0'")
    List<DayResult> getNotSyncedResults();

    @Update()
    void syncResult(DayResult result);

    @Query("DELETE FROM dayresult")
    void deleteTable();

}
