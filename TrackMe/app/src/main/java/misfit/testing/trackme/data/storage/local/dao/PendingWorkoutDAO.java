package misfit.testing.trackme.data.storage.local.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import misfit.testing.trackme.data.ui.PendingWorkoutItemData;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static misfit.testing.trackme.data.storage.local.AppDB.PendingWorkout.deleteAll;
import static misfit.testing.trackme.data.storage.local.AppDB.PendingWorkout.queryAll;

/**
 * Created by Thu Nguyen on 9/28/2017.
 */

@Dao
public interface PendingWorkoutDAO {
    @Insert(onConflict = REPLACE)
    void upsertPendingWorkout(PendingWorkoutItemData pendingWorkoutItemData);

    @Query(queryAll)
    List<PendingWorkoutItemData> loadPendingWorkoutItem();

    @Query(deleteAll)
    void deleteAll();
}
