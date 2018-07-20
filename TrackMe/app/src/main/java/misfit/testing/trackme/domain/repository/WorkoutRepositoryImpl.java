package misfit.testing.trackme.domain.repository;

import java.util.List;

import io.reactivex.Observable;
import misfit.testing.trackme.data.storage.local.AppDB;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.domain.repository.base.AbsRepository;
import misfit.testing.trackme.domain.repository.base.IWorkoutRepository;

/**
 * Created by Thu Nguyen on 8/23/2017.
 */

public class WorkoutRepositoryImpl extends AbsRepository implements IWorkoutRepository {

    @Override
    public Observable<List<WorkoutItemData>> getWorkoutList(int limit, int offset) {
        return Observable.defer(() -> {
            try {
                List<WorkoutItemData> workoutItemDataList = AppDB.getInstance().workoutDAO().loadWorkoutList(limit, offset);
                return Observable.just(workoutItemDataList);
            } catch (Exception ex) {
                return Observable.error(ex);
            }
        });
    }

    @Override
    public Observable<Void> addWorkout(WorkoutItemData workoutItemData) {
        return Observable.defer(() -> {
            try {
                AppDB.getInstance().workoutDAO().upsertWorkout(workoutItemData);
                return Observable.just(null);
            } catch (Exception ex) {
                return Observable.error(ex);
            }
        });
    }

    @Override
    public Observable<Void> addPendingWorkout(PendingWorkoutItemData pendingWorkoutItemData) {
        return Observable.defer(() -> {
            try {
                AppDB.getInstance().pendingWorkoutDAO().upsertPendingWorkout(pendingWorkoutItemData);
                return Observable.just(null);
            } catch (Exception ex) {
                return Observable.error(ex);
            }
        });
    }

    @Override
    public Observable<Void> deletePendingWorkout() {
        return Observable.defer(() -> {
            try {
                AppDB.getInstance().pendingWorkoutDAO().deleteAll();
                return Observable.just(null);
            } catch (Exception ex) {
                return Observable.error(ex);
            }
        });
    }

    @Override
    public Observable<PendingWorkoutItemData> getPendingWorkout() {
        return Observable.defer(() -> {
            try {
                List<PendingWorkoutItemData> pendingWorkoutItemDataList = AppDB.getInstance().pendingWorkoutDAO().loadPendingWorkoutItem();
                if(pendingWorkoutItemDataList != null && pendingWorkoutItemDataList.size() > 0) {
                    return Observable.just(pendingWorkoutItemDataList.get(0));
                }
                return Observable.error(new Throwable("pending workout not found"));
            } catch (Exception ex) {
                return Observable.error(ex);
            }
        });
    }
}
