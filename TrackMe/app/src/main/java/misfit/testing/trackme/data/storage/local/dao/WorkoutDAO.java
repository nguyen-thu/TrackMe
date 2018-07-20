package misfit.testing.trackme.data.storage.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import misfit.testing.trackme.data.ui.WorkoutItemData;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static misfit.testing.trackme.data.storage.local.AppDB.Workout.queryBlockByPage;

/**
 * Created by Thu Nguyen on 9/28/2017.
 */

@Dao
public interface WorkoutDAO {
    @Insert(onConflict = REPLACE)
    void upsertWorkout(WorkoutItemData workoutItemData);

    @Insert(onConflict = REPLACE)
    void upsertWorkoutList(List<WorkoutItemData> workoutItemDataList);

    @Query(queryBlockByPage)
    List<WorkoutItemData> loadWorkoutList(int limit, int offset);

}
