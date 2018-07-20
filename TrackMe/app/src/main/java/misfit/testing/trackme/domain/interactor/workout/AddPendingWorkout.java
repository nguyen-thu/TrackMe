package misfit.testing.trackme.domain.interactor.workout;

import io.reactivex.Observable;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.domain.interactor.base.AbsUseCase;
import misfit.testing.trackme.domain.repository.WorkoutRepositoryImpl;
import misfit.testing.trackme.domain.repository.base.IWorkoutRepository;

/**
 * Created by Thu Nguyen on 8/23/2017.
 */

public class AddPendingWorkout extends AbsUseCase<Void, Void, Void, Void> {
    IWorkoutRepository repository;

    public AddPendingWorkout() {
        repository = new WorkoutRepositoryImpl();
    }

    @Override
    protected Observable<Void> buildUseCaseSingle() {
        return null;
    }

    public Observable<Void> addPendingWorkoutItem(PendingWorkoutItemData pendingWorkoutItemData){
        return repository.addPendingWorkout(pendingWorkoutItemData);
    }

}
