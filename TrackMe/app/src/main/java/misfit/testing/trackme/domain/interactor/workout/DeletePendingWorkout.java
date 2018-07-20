package misfit.testing.trackme.domain.interactor.workout;

import io.reactivex.Observable;
import misfit.testing.trackme.domain.interactor.base.AbsUseCase;
import misfit.testing.trackme.domain.repository.WorkoutRepositoryImpl;
import misfit.testing.trackme.domain.repository.base.IWorkoutRepository;

/**
 * Created by Thu Nguyen on 8/23/2017.
 */

public class DeletePendingWorkout extends AbsUseCase<Void, Void, Void, Void> {
    IWorkoutRepository repository;

    public DeletePendingWorkout() {
        repository = new WorkoutRepositoryImpl();
    }

    @Override
    protected Observable<Void> buildUseCaseSingle() {
        return null;
    }

    public Observable<Void> deletePendingWorkoutItem(){
        return repository.deletePendingWorkout();
    }

}
