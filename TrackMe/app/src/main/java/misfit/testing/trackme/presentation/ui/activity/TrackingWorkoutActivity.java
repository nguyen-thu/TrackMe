package misfit.testing.trackme.presentation.ui.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import misfit.testing.trackme.R;
import misfit.testing.trackme.TrackMeApplication;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.helper.GoogleMapHelper;
import misfit.testing.trackme.helper.TrackingWorkoutHelper;
import misfit.testing.trackme.helper.service.LocationMonitoringService;
import misfit.testing.trackme.helper.service.OnClearFromRecentService;
import misfit.testing.trackme.presentation.ui.activity.base.BaseActivity;
import misfit.testing.trackme.util.AppConstant;
import misfit.testing.trackme.util.AppUtils;
import misfit.testing.trackme.util.DialogUtil;
import misfit.testing.trackme.util.PermissionUtils;

/**
 * Created by Thu Nguyen on 7/17/2018.
 */

public class TrackingWorkoutActivity extends BaseActivity
        implements TrackingWorkoutHelper.ITrackingListener, OnMapReadyCallback{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 228;
    private static final int LOCATION_ENABLE_REQUEST_CODE = 229;

    @BindView(R.id.ibPause)
    ImageButton ibPause;
    @BindView(R.id.llStopRefresh)
    LinearLayout llStopRefresh;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.tvAverageSpeed)
    TextView tvAverageSpeed;
    @BindView(R.id.tvSpeedText)
    TextView tvSpeedText;

    SupportMapFragment mapFragment;
    GoogleMap googleMap;

    GoogleMapHelper googleMapHelper;
    TrackingWorkoutHelper trackingWorkoutHelper;
    PendingWorkoutItemData pendingWorkoutItemData;

    boolean isVisibleToUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_workout);
        ButterKnife.bind(this, this);

        // Adjust some text
        tvSpeedText.setText(R.string.speed_text);

        // Init helper object
        trackingWorkoutHelper = new TrackingWorkoutHelper();
        pendingWorkoutItemData = getIntent().getParcelableExtra(AppConstant.PENDING_WORKOUT_ITEM);
        if(pendingWorkoutItemData != null){
            trackingWorkoutHelper.setPendingWorkoutItemData(pendingWorkoutItemData);
        }
        trackingWorkoutHelper.setTrackingListener(this);
        googleMapHelper = new GoogleMapHelper();

        // Init map view
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        EventBus.getDefault().register(this);
        // Start the service to know when the app stops from recent
        startService(new Intent(this, OnClearFromRecentService.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisibleToUser = true;

        if(pendingWorkoutItemData == null) {
            // Update UI if has changed on background
            if (trackingWorkoutHelper != null && trackingWorkoutHelper.hasData() && !trackingWorkoutHelper.isNotifyToUI()) {
                updateLatestDuration(trackingWorkoutHelper.getPendingWorkoutItemData().duration);
                updateLatestLocation(trackingWorkoutHelper.getPendingWorkoutItemData(), true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisibleToUser = false;

        // Store pending workout if any
        EventBus.getDefault().post(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        // Stop recent stop service
        stopService(new Intent(this, OnClearFromRecentService.class));
    }

    @Override
    public void onBackPressed() {
        DialogUtil.showConfirmDialog(this, getString(R.string.workout_session_out_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                processStop();
            }
        }, null);
    }

    //////////////// Implement click listener S//////////////////////
    @OnClick({R.id.ibPause, R.id.ibRefresh, R.id.ibStop})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.ibPause:
                processPause();
                break;
            case R.id.ibRefresh:
                processRefresh();
                break;
            case R.id.ibStop:
                processStop();
                break;
        }
    }
    //////////////// Implement click listener E//////////////////////

    //////////////// Some methods to process workout S//////////////////////
    private void processPause(){
        // Update UI
        llStopRefresh.setVisibility(View.VISIBLE);
        ibPause.setVisibility(View.GONE);

        // Stop google location service
        stopService(new Intent(this, LocationMonitoringService.class));

        // Also process pause from handler
        trackingWorkoutHelper.pauseTracking();
    }
    private void processRefresh(){
        // Update UI
        llStopRefresh.setVisibility(View.GONE);
        ibPause.setVisibility(View.VISIBLE);

        // Request location permission if any
        // Check location permission before
        if(PermissionUtils.checkOrRequestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)){
            beginTrackingNow();
        }
    }
    private void processStop(){
        // Stop the service
        stopService(new Intent(this, LocationMonitoringService.class));

        // Also process stop from handler
        trackingWorkoutHelper.stopTracking();
    }

    @SuppressLint("MissingPermission")
    private void beginTrackingNow(){
        googleMap.setMyLocationEnabled(true);
        if(pendingWorkoutItemData == null) {
            startService(new Intent(this, LocationMonitoringService.class));
        }else{
            pendingWorkoutItemData.reinitLocationInfo();

            // Update UI for the last pending item
            updateLatestLocation(pendingWorkoutItemData, true);
            updateLatestDuration(pendingWorkoutItemData.duration);

            if(AppConstant.WORKOUT_STATE.PAUSE == pendingWorkoutItemData.state){
                processPause();
            }else{ // Otherwise playing
                startService(new Intent(this, LocationMonitoringService.class));
            }
            // Release pending start for the first time
            pendingWorkoutItemData = null;
        }
    }
    //////////////// Some methods to process workout E//////////////////////

    @Override
    public void updateLatestDuration(Integer duration) {
        if(isVisibleToUser) {
            tvDuration.setText(AppUtils.getDisplayDuration(duration));
            trackingWorkoutHelper.setNotifyToUI(true);
        }
    }

    @Override
    public void updateLatestLocation(PendingWorkoutItemData pendingWorkoutItemData, boolean isDrawAll) {
        if(isVisibleToUser) {
            // Update workout info
            tvDistance.setText(pendingWorkoutItemData.getDisplayDistance());
            tvAverageSpeed.setText(pendingWorkoutItemData.getDisplayAverageSpeed());

            // Update on Map
            if(isDrawAll) {
                googleMapHelper.drawPendingWorkout(pendingWorkoutItemData);
            }else{
                googleMapHelper.drawRealtimeWorkout(pendingWorkoutItemData);
            }
            trackingWorkoutHelper.setNotifyToUI(true);
        }
    }

    @Override
    public void onSaveWorkoutDone(WorkoutItemData workoutItemData) {
        Intent intent = new Intent();
        intent.putExtra(AppConstant.WORKOUT_ITEM, workoutItemData);
        setResult(RESULT_OK, intent);
        finish();
    }
    //////////////// Granted permission result S//////////////////////
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults != null && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            beginTrackingNow();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == LOCATION_ENABLE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                startService(new Intent(this, LocationMonitoringService.class));
            }
        }
    }
    //////////////// Granted permission result E//////////////////////

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(TrackMeApplication.getInstance());
        this.googleMap = googleMap;
        googleMapHelper.setGoogleMap(googleMap);

        // Check location permission before
        if(PermissionUtils.checkOrRequestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)){
            beginTrackingNow();
        }
    }
    @Subscribe
    public void requestResolutionLocation(Status resolvableApiException){
        try {
            resolvableApiException.startResolutionForResult(this, LOCATION_ENABLE_REQUEST_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }
}
