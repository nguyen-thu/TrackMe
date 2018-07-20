package misfit.testing.trackme.presentation.presenter.impl;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.domain.interactor.workout.AddPendingWorkout;
import misfit.testing.trackme.domain.interactor.workout.DeletePendingWorkout;
import misfit.testing.trackme.domain.interactor.workout.GetPendingWorkout;
import misfit.testing.trackme.presentation.presenter.IPendingWorkoutPresenter;
import misfit.testing.trackme.presentation.presenter.base.BasePresenter;

/**
 * Created by Thu Nguyen on 5/24/2018.
 */

public class PendingWorkoutPresenterImpl extends BasePresenter<IPendingWorkoutPresenter.View> implements IPendingWorkoutPresenter.Presenter {
    AddPendingWorkout addPendingWorkout;
    DeletePendingWorkout deletePendingWorkout;
    GetPendingWorkout getPendingWorkout;

    public PendingWorkoutPresenterImpl(IPendingWorkoutPresenter.View view) {
        super(view);
        addPendingWorkout = new AddPendingWorkout();
        deletePendingWorkout = new DeletePendingWorkout();
        getPendingWorkout = new GetPendingWorkout();
    }

    @Override
    public void getPendingWorkoutItem() {
        Disposable disposable = getPendingWorkout.getPendingWorkoutItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pendingWorkoutItemData -> view.getPendingWorkoutItemResponse(pendingWorkoutItemData, null),
                        throwable -> view.getPendingWorkoutItemResponse(null, throwable.getMessage())
                );
        compositeDisposable.add(disposable);
    }

    @Override
    public void deletePendingWorkoutItem() {
        Disposable disposable = deletePendingWorkout.deletePendingWorkoutItem()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workoutItemDataList -> view.deletePendingWorkoutResponse(null),
                        throwable -> view.deletePendingWorkoutResponse(throwable.getMessage())
                );
        compositeDisposable.add(disposable);
    }

    @Override
    public void addPendingWorkoutItem(PendingWorkoutItemData pendingWorkoutItemData) {
        Disposable disposable = addPendingWorkout.addPendingWorkoutItem(pendingWorkoutItemData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workoutItemDataList -> view.addPendingWorkoutResponse(null),
                        throwable -> view.addPendingWorkoutResponse(throwable.getMessage())
                );
        compositeDisposable.add(disposable);
    }
}
