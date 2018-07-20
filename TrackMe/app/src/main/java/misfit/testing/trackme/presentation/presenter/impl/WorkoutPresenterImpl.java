package misfit.testing.trackme.presentation.presenter.impl;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.domain.interactor.workout.AddPendingWorkout;
import misfit.testing.trackme.domain.interactor.workout.AddWorkout;
import misfit.testing.trackme.domain.interactor.workout.GetWorkoutList;
import misfit.testing.trackme.presentation.presenter.IWorkoutPresenter;
import misfit.testing.trackme.presentation.presenter.base.BasePresenter;

/**
 * Created by Thu Nguyen on 5/24/2018.
 */

public class WorkoutPresenterImpl extends BasePresenter<IWorkoutPresenter.View> implements IWorkoutPresenter.Presenter {
    GetWorkoutList getWorkoutList;
    AddWorkout addWorkout;
    AddPendingWorkout addPendingWorkout;

    public WorkoutPresenterImpl(IWorkoutPresenter.View view) {
        super(view);
        addWorkout = new AddWorkout();
        addPendingWorkout = new AddPendingWorkout();
        getWorkoutList = new GetWorkoutList();
    }

    @Override
    public void getWorkoutList(int limit) {
        getWorkoutList.init(limit);
        Disposable disposable = getWorkoutList.buildUseCaseSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workoutItemDataList -> view.getWorkoutListResponse(workoutItemDataList, null),
                        throwable -> view.getWorkoutListResponse(null, throwable.getMessage())
                );
        compositeDisposable.add(disposable);
    }

    @Override
    public void loadWorkoutListMore(int offset) {
        getWorkoutList.loadMore(offset);
        Disposable disposable = getWorkoutList.buildUseCaseSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workoutItemDataList -> view.loadWorkoutListMoreResponse(workoutItemDataList, null),
                        throwable -> view.loadWorkoutListMoreResponse(null, throwable.getMessage())
                );
        compositeDisposable.add(disposable);
    }

    @Override
    public void addWorkoutItem(WorkoutItemData workoutItemData) {
        Disposable disposable = addWorkout.addWorkoutItem(workoutItemData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workoutItemDataList -> view.addWorkoutResponse(null),
                        throwable -> view.addWorkoutResponse(throwable.getMessage())
                );
        compositeDisposable.add(disposable);
    }
}
