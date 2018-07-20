package misfit.testing.trackme.helper;


import android.location.Location;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLng;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.presentation.presenter.IPendingWorkoutPresenter;
import misfit.testing.trackme.presentation.presenter.IWorkoutPresenter;
import misfit.testing.trackme.presentation.presenter.impl.PendingWorkoutPresenterImpl;
import misfit.testing.trackme.presentation.presenter.impl.WorkoutPresenterImpl;
import misfit.testing.trackme.util.AppConstant;

/**
 * Created by Thu Nguyen on 7/19/2018.
 */

public class TrackingWorkoutHelper implements IWorkoutPresenter.View, IPendingWorkoutPresenter.View{
    public interface ITrackingListener{
        void updateLatestDuration(Integer duration);
        void updateLatestLocation(PendingWorkoutItemData pendingWorkoutItemData, boolean isDrawAll);
        void onSaveWorkoutDone(WorkoutItemData workoutItemData);
    }
    private static final int TIME_TICK = 1000; // 1s

    Handler handler;
    IWorkoutPresenter.Presenter presenter;
    IPendingWorkoutPresenter.Presenter pendingPresenter;

    ITrackingListener trackingListener;

    PendingWorkoutItemData pendingWorkoutItemData;
    boolean isNotifyToUI;

    public TrackingWorkoutHelper(){
        init();
    }
    /*************** Init/deinit tracking S*******************************/
    private void init(){
        handler = new Handler();
        presenter = new WorkoutPresenterImpl(this);
        pendingPresenter = new PendingWorkoutPresenterImpl(this);
        pendingWorkoutItemData = new PendingWorkoutItemData();
        EventBus.getDefault().register(this);
    }
    public void setTrackingListener(ITrackingListener trackingListener){
        this.trackingListener = trackingListener;
    }

    public void setPendingWorkoutItemData(PendingWorkoutItemData pendingWorkoutItemData) {
        this.pendingWorkoutItemData = pendingWorkoutItemData;
    }

    public void setNotifyToUI(boolean notifyToUI) {
        isNotifyToUI = notifyToUI;
    }

    public boolean isNotifyToUI() {
        return isNotifyToUI;
    }
    public boolean hasData(){
        return pendingWorkoutItemData != null
                && pendingWorkoutItemData.trackingLocationList != null && pendingWorkoutItemData.trackingLocationList.size() > 0;
    }
    public PendingWorkoutItemData getPendingWorkoutItemData() {
        return pendingWorkoutItemData;
    }

    public void destroy(){
        EventBus.getDefault().unregister(this);
        if(presenter != null){
            presenter.destroy();
        }
        if(pendingPresenter != null){
            pendingPresenter.destroy();
        }
    }
    /*************** Init/deinit tracking E*******************************/



    /*************** Process tracking S*******************************/
    public void beginTracking(){
        pendingWorkoutItemData.setNewTrackingTime(System.currentTimeMillis());
        handler.postDelayed(timeTickRunnable, TIME_TICK);
    }
    public void pauseTracking(){
        handler.removeCallbacks(timeTickRunnable);
        pendingWorkoutItemData.state = AppConstant.WORKOUT_STATE.PAUSE;
        pendingWorkoutItemData.updateLogicForPause();
    }
    public void stopTracking(){
        handler.removeCallbacks(timeTickRunnable);

        // Delete pending workout
        pendingPresenter.deletePendingWorkoutItem();

        // Save workout item
        WorkoutItemData workoutItemData = new WorkoutItemData(pendingWorkoutItemData);
        presenter.addWorkoutItem(workoutItemData);

        // Save done
        if(trackingListener != null){
            trackingListener.onSaveWorkoutDone(workoutItemData);
            destroy();
        }

    }
    public void refreshTracking(){
        pendingWorkoutItemData.setNewTrackingTime(System.currentTimeMillis());
        handler.postDelayed(timeTickRunnable, TIME_TICK);
    }
    private void updateInfoAfterTick(){
        isNotifyToUI = false;
        pendingWorkoutItemData.updateDurationFromCertainTime(System.currentTimeMillis());

        // Also post to UI
        if(trackingListener != null){
            trackingListener.updateLatestDuration(pendingWorkoutItemData.duration);
        }
    }
    /*************** Process tracking E*******************************/


    /*************** Process receive data from event bus S*******************************/
    @Subscribe
    public void receiveNewChangedLocation(Location location){
        isNotifyToUI = false;

        pendingWorkoutItemData.currentSpeed = location.getSpeed();
        pendingWorkoutItemData.addNewLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        if(pendingWorkoutItemData.getDuration() == 0){ // First time of tracking
            beginTracking();
        }else if(pendingWorkoutItemData.state == AppConstant.WORKOUT_STATE.PAUSE){
            refreshTracking();
        }
        pendingWorkoutItemData.state = AppConstant.WORKOUT_STATE.ACTIVE;
        // Also post to UI to notify
        if(trackingListener != null){
            trackingListener.updateLatestLocation(pendingWorkoutItemData, false);
        }
    }
    @Subscribe
    public void receiveClearAppFromRecent(Boolean forceStop){
        if(pendingWorkoutItemData != null &&
                pendingWorkoutItemData.state != AppConstant.WORKOUT_STATE.NONE && pendingWorkoutItemData.trackingLocationList.size() > 0){
            // Store to db
            pendingPresenter.addPendingWorkoutItem(pendingWorkoutItemData);
        }
    }
    /*************** Process receive data from event bus E*******************************/

    //////////////// Override methods from View-Presenter S//////////////////////
    @Override
    public void getWorkoutListResponse(List<WorkoutItemData> workoutItemDataList, String error) {

    }

    @Override
    public void loadWorkoutListMoreResponse(List<WorkoutItemData> workoutItemDataList, String error) {

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

    }
    //////////////// Override methods from View-Presenter E//////////////////////



    Runnable timeTickRunnable = new Runnable() {
        @Override
        public void run() {
            handler.removeCallbacks(timeTickRunnable);
            updateInfoAfterTick();
            handler.postDelayed(timeTickRunnable, TIME_TICK);
        }
    };
}
