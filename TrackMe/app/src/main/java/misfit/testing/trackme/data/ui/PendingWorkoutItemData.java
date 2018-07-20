package misfit.testing.trackme.data.ui;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.ArrayList;

import misfit.testing.trackme.data.converter.LatLgnTypeConverters;
import misfit.testing.trackme.data.converter.WorkoutEnumTypeConverters;
import misfit.testing.trackme.data.storage.local.AppDB;
import misfit.testing.trackme.util.AppConstant;
import misfit.testing.trackme.util.AppUtils;

/**
 * Created by Thu Nguyen on 7/19/2018.
 */
@Entity(tableName = AppDB.PendingWorkout.TABLE_NAME)
public class PendingWorkoutItemData extends WorkoutItemData implements Parcelable{
    @TypeConverters(WorkoutEnumTypeConverters.class)
    @SerializedName("state")
    @ColumnInfo(name = "state")
    @Expose
    public AppConstant.WORKOUT_STATE state;

    @SerializedName("new_tracking_time")
    @ColumnInfo(name = "new_tracking_time")
    @Expose
    public long newTrackingTime;

    @SerializedName("current_speed")
    @ColumnInfo(name = "current_speed")
    @Expose
    public double currentSpeed; // m/s

    @ColumnInfo(name = "current_distance")
    public double currentDistance;

    @TypeConverters(LatLgnTypeConverters.class)
    @ColumnInfo(name = "current_location")
    public LatLng currentLocation;

    @Ignore
    LatLng lastLocation;

    protected PendingWorkoutItemData(Parcel in) {
        createdTime = in.readLong();
        trackingLocationList = in.createTypedArrayList(LatLng.CREATOR);
        interruptPositionList = new ArrayList<>();
        in.readList(interruptPositionList, null);
        duration = in.readInt();
        state = AppConstant.WORKOUT_STATE.getEnum(in.readInt());
        newTrackingTime = in.readLong();
        currentSpeed = in.readDouble();
        currentDistance = in.readDouble();
        currentLocation = in.readParcelable(LatLng.class.getClassLoader());

    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(createdTime);
        parcel.writeTypedList(trackingLocationList);
        parcel.writeList(interruptPositionList);
        parcel.writeInt(duration);
        parcel.writeInt(state.getValue());
        parcel.writeLong(newTrackingTime);
        parcel.writeDouble(currentSpeed);
        parcel.writeDouble(currentDistance);
        parcel.writeParcelable(currentLocation, 0);
    }
    public static final Creator<PendingWorkoutItemData> CREATOR = new Creator<PendingWorkoutItemData>() {
        @Override
        public PendingWorkoutItemData createFromParcel(Parcel in) {
            return new PendingWorkoutItemData(in);
        }

        @Override
        public PendingWorkoutItemData[] newArray(int size) {
            return new PendingWorkoutItemData[size];
        }
    };

    public PendingWorkoutItemData(){
        createdTime = System.currentTimeMillis();
        state = AppConstant.WORKOUT_STATE.NONE;
        duration = 0;
        trackingLocationList = new ArrayList<>();
        interruptPositionList = new ArrayList<>();
    }
    public boolean isInit(){
        return currentLocation == lastLocation;
    }
    public boolean isInitFromResume(){
        return isInit() || (currentLocation.equals(lastLocation));
    }
    public LatLng getLastLocation() {
        return lastLocation;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }
    public void reinitLocationInfo(){
        if(trackingLocationList != null && trackingLocationList.size() > 0){
            lastLocation = trackingLocationList.get(trackingLocationList.size() - 1);
            currentLocation = new LatLng(lastLocation.latitude, lastLocation.longitude);
        }
    }
    @Override
    public void addNewLocation(LatLng latLng) {
        super.addNewLocation(latLng);
        if(currentLocation == null){
            currentLocation = latLng;
            lastLocation = latLng;
        }else{
            float[] results = new float[2];
            Location.distanceBetween(latLng.latitude, latLng.longitude, currentLocation.latitude, currentLocation.longitude, results);
            currentDistance += results[0];
            lastLocation = currentLocation;
            currentLocation = latLng;
        }
    }

    @Override
    public String getDisplayAverageSpeed() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String averageSpeedFormat = "%s km/h";
        displayAverageSpeed = String.format(averageSpeedFormat, decimalFormat.format(AppUtils.convertToKmhFromMs(currentSpeed)));
        return displayAverageSpeed;
    }
    @Override
    public String getDisplayDistance() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String distanceFormat = "%s km";
        return String.format(distanceFormat, decimalFormat.format(currentDistance/1000));
    }
    public void updateSpeed(double currentSpeed){
        this.currentSpeed = currentSpeed;
    }
    public void updateTrackingTime(long startTrackingTime){
        this.newTrackingTime = startTrackingTime;
    }
    public void setNewTrackingTime(long trackingTime){
        newTrackingTime = trackingTime;
    }
    public void updateDurationFromCertainTime(long certainTime){
        duration += (certainTime - newTrackingTime) / 1000;
        newTrackingTime = certainTime;
    }
    public void updateLogicForPause(){
        if(trackingLocationList != null && trackingLocationList.size() > 0){
            interruptPositionList.add(trackingLocationList.size() - 1);
        }
    }
}
