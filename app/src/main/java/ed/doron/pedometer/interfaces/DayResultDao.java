package ed.doron.pedometer.interfaces;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import ed.doron.pedometer.models.DayResult;


@Dao
public interface DayResultDao {
    @Insert
    void insertResult(DayResult result);

    @Query("SELECT * FROM dayresult")
    List<DayResult> getAllResults();
}
