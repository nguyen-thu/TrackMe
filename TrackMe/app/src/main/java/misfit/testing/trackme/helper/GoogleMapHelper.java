package misfit.testing.trackme.helper;

import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import misfit.testing.trackme.R;
import misfit.testing.trackme.TrackMeApplication;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.util.AppConstant;
import misfit.testing.trackme.util.AppUtils;

/**
 * Created by Thu Nguyen on 7/17/2018.
 */

public class GoogleMapHelper {
    private static final int COLOR_RED_ARGB = 0xffff0000;
    private static int POLYLINE_STROKE_WIDTH_PX = 6;

    GoogleMap googleMap;
    public GoogleMapHelper(){
        // 2dp
        POLYLINE_STROKE_WIDTH_PX = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, TrackMeApplication.getInstance().getResources().getDisplayMetrics());
    }
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }
    public void moveToLocationWithZoomInit(double latitude, double longitude, int meters) {
//        int mapHeightInDP = 200;
//        Resources r = TrackMeApplication.getInstance().getResources();
//        int mapSideInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mapHeightInDP, r.getDisplayMetrics());
//        LatLng point = new LatLng(latitude, longitude);
//        LatLngBounds latLngBounds = SphericalUtils.calculateBounds(point, meters);
//        if (latLngBounds != null) {
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(latLngBounds, mapSideInPixels, mapSideInPixels, 0);
//            if (googleMap != null) {
//                googleMap.moveCamera(cameraUpdate);
//            }
//        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), getZoomLevel(meters));
        if (googleMap != null) {
            googleMap.moveCamera(cameraUpdate);
        }
    }
    private Marker addMarker(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.ic_start_point_marker));
        //.anchor(V_ANCHOR, V1_ANCHOR);
        Marker marker = googleMap.addMarker(markerOption);
        return marker;
    }
    public void drawWorkout(WorkoutItemData workoutItemData){
        googleMap.clear();

        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .addAll(workoutItemData.trackingLocationList));
        stylePolyline(polyline, true, true);

        LatLng center = workoutItemData.getAnchorPoint();
        if(center != null){
            moveToLocationWithZoomInit(center.latitude, center.longitude, workoutItemData.getRadius());
        }
    }
    public void drawPendingWorkout(WorkoutItemData workoutItemData){
        googleMap.clear();

        Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                .addAll(workoutItemData.trackingLocationList));
        stylePolyline(polyline, true, false);
    }
    public void drawRealtimeWorkout(PendingWorkoutItemData pendingWorkoutItemData){
        if(pendingWorkoutItemData.isInit()){
            addMarker(pendingWorkoutItemData.getCurrentLocation());
            moveToLocationWithZoomInit(pendingWorkoutItemData.getCurrentLocation().latitude, pendingWorkoutItemData.getCurrentLocation().longitude,
                    AppConstant.DEFAULT_ZOOM_IN_METERS);
        }else{
            Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                    .add(pendingWorkoutItemData.getLastLocation(), pendingWorkoutItemData.getCurrentLocation()));
            stylePolyline(polyline, false, false);
        }
    }
    private void stylePolyline(Polyline polyline, boolean hasStartPoint, boolean hasEndPoint){
        if(hasStartPoint) {
            polyline.setStartCap(new CustomCap(
                    BitmapDescriptorFactory.fromResource(R.mipmap.ic_start_point_marker)));
        }
        if(hasEndPoint){
            polyline.setEndCap(new CustomCap(
                    BitmapDescriptorFactory.fromResource(R.mipmap.ic_end_point_marker)));
        }
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_RED_ARGB);
        polyline.setJointType(JointType.ROUND);
    }

    /**
     * Get zoom level from radius in meters
     * @param radius
     * @return
     */
    public float getZoomLevel(int radius) {
        double radiusInMiles = AppUtils.convertMetersToMiles(radius);
        return Math.round(14-Math.log(radiusInMiles)/Math.log(2));
    }
}
