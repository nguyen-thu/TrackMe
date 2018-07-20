package misfit.testing.trackme.presentation.presenter;

import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.presentation.presenter.base.IVP;

/**
 * Created by Thu Nguyen on 5/24/2018.
 */

public interface IPendingWorkoutPresenter {
    interface View extends IVP.View {
        void deletePendingWorkoutResponse(String error);
        void addPendingWorkoutResponse(String error);
        void getPendingWorkoutItemResponse(PendingWorkoutItemData pendingWorkoutItemData, String error);
    }
    interface Presenter extends IVP.Presenter{
        void getPendingWorkoutItem();
        void deletePendingWorkoutItem();
        void addPendingWorkoutItem(PendingWorkoutItemData pendingWorkoutItemData);
    }
}
