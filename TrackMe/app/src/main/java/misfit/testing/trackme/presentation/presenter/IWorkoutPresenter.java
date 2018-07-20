package misfit.testing.trackme.presentation.presenter;

import java.util.List;

import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.presentation.presenter.base.IVP;

/**
 * Created by Thu Nguyen on 5/24/2018.
 */

public interface IWorkoutPresenter {
    interface View extends IVP.View {
        void getWorkoutListResponse(List<WorkoutItemData> workoutItemDataList, String error);
        void loadWorkoutListMoreResponse(List<WorkoutItemData> workoutItemDataList, String error);
        void addWorkoutResponse(String error);
    }
    interface Presenter extends IVP.Presenter{
        void getWorkoutList(int limit);
        void loadWorkoutListMore(int offset);
        void addWorkoutItem(WorkoutItemData workoutItemData);
    }
}
