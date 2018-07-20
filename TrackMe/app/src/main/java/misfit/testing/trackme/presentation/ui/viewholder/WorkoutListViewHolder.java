package misfit.testing.trackme.presentation.ui.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import misfit.testing.trackme.R;
import misfit.testing.trackme.TrackMeApplication;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.helper.GoogleMapHelper;
import misfit.testing.trackme.presentation.ui.viewholder.base.AbsRecyclerViewHolder;
import misfit.testing.trackme.util.AppUtils;

/**
 * Created by Thu Nguyen on 6/15/2018.
 */

public class WorkoutListViewHolder extends AbsRecyclerViewHolder<WorkoutItemData> implements OnMapReadyCallback{
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvDuration)
    TextView tvDuration;
    @BindView(R.id.tvAverageSpeed)
    TextView tvAverageSpeed;
    @BindView(R.id.mapView)
    MapView mapView;

    GoogleMapHelper googleMapHelper;
    WorkoutItemData workoutItemData;
    GoogleMap googleMap;

    public WorkoutListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        googleMapHelper = new GoogleMapHelper();
        if(mapView != null){
            mapView.getLayoutParams().height = 3 * AppUtils.getWidthDevice((Activity)(itemView.getContext())) / 4;
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void bind(WorkoutItemData itemdata, int pos) {
        super.bind(itemdata, pos);
        this.workoutItemData = itemdata;

        tvDistance.setText(itemdata.getDisplayDistance());
        tvDuration.setText(itemdata.getDisplayDuration());
        tvAverageSpeed.setText(itemdata.getDisplayAverageSpeed());

        drawWorkoutOnMap();
    }
    private void drawWorkoutOnMap(){
        if(googleMap == null || workoutItemData == null){
            return;
        }
        googleMapHelper.drawWorkout(workoutItemData);

        // Set the map type back to normal.
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

    }
    public void freeMap(){
        if(googleMap != null){
            googleMap.clear();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(TrackMeApplication.getInstance());
        this.googleMap = googleMap;
        googleMapHelper.setGoogleMap(googleMap);
        drawWorkoutOnMap();
    }
}
