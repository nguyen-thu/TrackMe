package misfit.testing.trackme.domain.interactor.workout;

import java.util.List;

import io.reactivex.Observable;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.domain.interactor.base.AbsUseCase;
import misfit.testing.trackme.domain.repository.WorkoutRepositoryImpl;
import misfit.testing.trackme.domain.repository.base.IWorkoutRepository;

/**
 * Created by Thu Nguyen on 8/23/2017.
 */

public class GetWorkoutList extends AbsUseCase<List<WorkoutItemData>, Integer, Void, Void> {
    IWorkoutRepository repository;
    int limit = 10;
    public GetWorkoutList() {
        repository = new WorkoutRepositoryImpl();
        queryReq = 0;
    }
    public void init(int limit){
        this.limit = limit;
    }
    public void loadMore(int offset){
        queryReq = offset;
    }
    @Override
    public Observable<List<WorkoutItemData>> buildUseCaseSingle() {
        return repository.getWorkoutList(limit, queryReq);
    }
}
