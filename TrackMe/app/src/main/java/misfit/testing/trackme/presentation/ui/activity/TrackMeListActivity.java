package misfit.testing.trackme.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import misfit.testing.trackme.R;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.presentation.presenter.IPendingWorkoutPresenter;
import misfit.testing.trackme.presentation.presenter.IWorkoutPresenter;
import misfit.testing.trackme.presentation.presenter.impl.PendingWorkoutPresenterImpl;
import misfit.testing.trackme.presentation.presenter.impl.WorkoutPresenterImpl;
import misfit.testing.trackme.presentation.ui.activity.base.BaseActivity;
import misfit.testing.trackme.presentation.ui.adapter.WorkoutListAdapter;
import misfit.testing.trackme.presentation.ui.widget.RecyclerViewLoadMore;
import misfit.testing.trackme.util.AppConstant;
import misfit.testing.trackme.util.DialogUtil;
import misfit.testing.trackme.util.PermissionUtils;

public class TrackMeListActivity extends BaseActivity
        implements IWorkoutPresenter.View, IPendingWorkoutPresenter.View{
    private static final int TRACK_WORKOUT_REQUEST_CODE = 228;

    @BindView(R.id.ibRecord)
    ImageButton ibRecord;
    @BindView(R.id.rvWorkout)
    RecyclerViewLoadMore rvWorkout;
    @BindView(R.id.tvEmptyWorkoutList)
    TextView tvEmptyWorkoutList;

    IWorkoutPresenter.Presenter presenter;
    IPendingWorkoutPresenter.Presenter pendingPresenter;

    WorkoutListAdapter adapter;
    List<WorkoutItemData> workoutItemDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_me_list);
        ButterKnife.bind(this, this);

        // Init data and adapter to attach to recyclerview
        workoutItemDataList = new ArrayList<>();
        adapter = new WorkoutListAdapter();
        adapter.setDataList(workoutItemDataList);
        rvWorkout.setAdapter(adapter);

        // Init presenter object and load data
        presenter = new WorkoutPresenterImpl(this);
        presenter.getWorkoutList(AppConstant.ITEMS_PER_PAGE);

        pendingPresenter = new PendingWorkoutPresenterImpl(this);
        pendingPresenter.getPendingWorkoutItem();
        // Implement loadmore listener
        rvWorkout.setOnLoadMoreListener(() -> presenter.loadWorkoutListMore(workoutItemDataList.size()));

        // Request location permission if not have
        PermissionUtils.checkOrRequestLocationPermission(this, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
        pendingPresenter.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TRACK_WORKOUT_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                // Get the newest workout item
                WorkoutItemData workoutItemData = data.getParcelableExtra(AppConstant.WORKOUT_ITEM);

                // Add it as the first item and then notify UI
                workoutItemDataList.add(0, workoutItemData);
                adapter.notifyDataSetChanged();
                invalidateUI(true);
            }
        }
    }
    private void invalidateUI(boolean hasData){
        rvWorkout.setVisibility(hasData ? View.VISIBLE : View.GONE);
        tvEmptyWorkoutList.setVisibility(hasData ? View.GONE : View.VISIBLE);
    }
    //////////////// Implement click listener S//////////////////////
    @OnClick({R.id.ibRecord})
    public void onClick(){
        if(isGooglePlayServicesAvailable()) { // Check google play service installed on device
            // Go to tracking workout screen
            Intent intent = new Intent(this, TrackingWorkoutActivity.class);
            startActivityForResult(intent, TRACK_WORKOUT_REQUEST_CODE);
        }else{
            DialogUtil.showInformDialog(this, getString(R.string.play_service_not_found_error), null);
        }
    }
    //////////////// Implement click listener E//////////////////////

    //////////////// Override methods from View-Presenter S//////////////////////
    @Override
    public void getWorkoutListResponse(List<WorkoutItemData> workoutItemDataList, String error) {
        if(workoutItemDataList != null && workoutItemDataList.size() > 0){ // Any workout session found
            // Populate the item in list at the first time
            this.workoutItemDataList.clear();
            this.workoutItemDataList.addAll(workoutItemDataList);
            adapter.notifyDataSetChanged();
        }
        // Invalidate UI
        invalidateUI(workoutItemDataList != null && workoutItemDataList.size() > 0);

        // Check list read to end
        rvWorkout.setReadEnd(workoutItemDataList != null && workoutItemDataList.size() < AppConstant.ITEMS_PER_PAGE);
    }

    @Override
    public void loadWorkoutListMoreResponse(List<WorkoutItemData> workoutItemDataList, String error) {
        // Reset load-more state
        rvWorkout.onLoadMoreComplete();
        // Check list read to end
        rvWorkout.setReadEnd(workoutItemDataList != null && workoutItemDataList.size() < AppConstant.ITEMS_PER_PAGE);

        if(workoutItemDataList != null && workoutItemDataList.size() > 0) {
            // Add data list to original list
            this.workoutItemDataList.addAll(workoutItemDataList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addWorkoutResponse(String error) {

    }

    @Override
    public void deletePendingWorkoutResponse(String error) {

    }

    @Override
    public void addPendingWorkoutResponse(String error) {

    }

    @Override
    public void getPendingWorkoutItemResponse(PendingWorkoutItemData pendingWorkoutItemData, String error) {
        if(pendingWorkoutItemData != null){
            // Go to tracking workout
            Intent intent = new Intent(this, TrackingWorkoutActivity.class);
            intent.putExtra(AppConstant.PENDING_WORKOUT_ITEM, pendingWorkoutItemData);
            startActivityForResult(intent, TRACK_WORKOUT_REQUEST_CODE);
        }
    }

    //////////////// Override methods from View-Presenter E//////////////////////
}
