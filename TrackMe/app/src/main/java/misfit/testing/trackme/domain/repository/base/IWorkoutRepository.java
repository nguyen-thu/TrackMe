package misfit.testing.trackme.domain.repository.base;

import java.util.List;

import io.reactivex.Observable;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;

/**
 * Created by Thu Nguyen on 8/23/2017.
 */

public interface IWorkoutRepository extends IRepository {
    Observable<List<WorkoutItemData>> getWorkoutList(int limit, int offset);
    Observable<Void> addWorkout(WorkoutItemData workoutItemData);
    Observable<Void> addPendingWorkout(PendingWorkoutItemData pendingWorkoutItemData);
    Observable<Void> deletePendingWorkout();
    Observable<PendingWorkoutItemData> getPendingWorkout();
}
