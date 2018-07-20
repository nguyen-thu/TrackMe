package misfit.testing.trackme.data.ui;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import misfit.testing.trackme.data.converter.IntegerListTypeConverters;
import misfit.testing.trackme.data.converter.LatLngListTypeConverters;
import misfit.testing.trackme.data.storage.local.AppDB;
import misfit.testing.trackme.data.ui.base.AbsItemData;
import misfit.testing.trackme.util.AppConstant;
import misfit.testing.trackme.util.AppUtils;

/**
 * Created by Thu Nguyen on 7/16/2018.
 */

@Entity(tableName = AppDB.Workout.TABLE_NAME)
public class WorkoutItemData extends AbsItemData implements Parcelable{
    @SerializedName("created_time")
    @ColumnInfo(name = "created_time")
    @Expose
    @PrimaryKey
    @NonNull
    public long createdTime;

    @SerializedName("tracking_location_list")
    @ColumnInfo(name = "tracking_location_list")
    @Expose
    @TypeConverters(LatLngListTypeConverters.class)
    public List<LatLng> trackingLocationList;

    @SerializedName("interrupt_position_list")
    @ColumnInfo(name = "interrupt_position_list")
    @TypeConverters(IntegerListTypeConverters.class)
    @Expose
    public List<Integer> interruptPositionList; // Manage the interrupt position at tracking location list

    @Expose
    @NonNull
    @ColumnInfo(name = "duration")
    public int duration; // In second

    @Ignore
    String displayDuration;
    @Ignore
    String displayDistance;
    @Ignore
    String displayAverageSpeed;
    @Ignore
    double actualDistance; // In meters
    @Ignore
    LatLng anchorPoint; // The point which the mapview navigates to
    @Ignore
    int radius; // The radius that the map radius to

    protected WorkoutItemData(Parcel in) {
        createdTime = in.readLong();
        trackingLocationList = in.createTypedArrayList(LatLng.CREATOR);
        interruptPositionList = new ArrayList<>();
        in.readList(interruptPositionList, null);
        duration = in.readInt();
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
    }
    public static final Creator<WorkoutItemData> CREATOR = new Creator<WorkoutItemData>() {
        @Override
        public WorkoutItemData createFromParcel(Parcel in) {
            return new WorkoutItemData(in);
        }

        @Override
        public WorkoutItemData[] newArray(int size) {
            return new WorkoutItemData[size];
        }
    };

    public long getCreatedTime() {
        return createdTime;
    }
    public int getDuration() {
        return duration;
    }

    public WorkoutItemData(){}

    public WorkoutItemData(PendingWorkoutItemData pendingWorkoutItemData){
        createdTime = System.currentTimeMillis();
        trackingLocationList = pendingWorkoutItemData.trackingLocationList;
        interruptPositionList = pendingWorkoutItemData.interruptPositionList;
        duration = pendingWorkoutItemData.duration;
    }
    public void addNewLocation(LatLng latLng){
        if(trackingLocationList == null){
            trackingLocationList = new ArrayList<>();
        }
        trackingLocationList.add(latLng);
    }
    public double getActualDistance() {
        if(actualDistance <= 0){
            actualDistance = 0;
            float[] result = new float[2];
            if(trackingLocationList != null && trackingLocationList.size() > 1){
                for(int index = 0; index < trackingLocationList.size() - 1; index ++){
                    if(interruptPositionList == null ||
                            (interruptPositionList != null && !interruptPositionList.contains(index))){

                        LatLng firstLatLng = trackingLocationList.get(index);
                        LatLng secondLatLng = trackingLocationList.get(index + 1);
                        Location.distanceBetween(firstLatLng.latitude, firstLatLng.longitude, secondLatLng.latitude, secondLatLng.longitude, result);
                        actualDistance += result[0];
                    }
                }
            }
        }
        return actualDistance;
    }

    public String getDisplayDuration() {
        if(displayDuration == null){
            displayDuration = AppUtils.getDisplayDuration(duration);
        }
        return displayDuration;
    }

    public String getDisplayAverageSpeed() {
        if(displayAverageSpeed == null){
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            double distance = getActualDistance();
            String averageSpeedFormat = "%s km/h";
            displayAverageSpeed = String.format(averageSpeedFormat, decimalFormat.format(AppUtils.convertToKmhFromMs(distance, duration)));
        }
        return displayAverageSpeed;
    }

    public String getDisplayDistance() {
        if(displayDistance == null) {
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String distanceFormat = "%s km";
            double distance = getActualDistance();
            displayDistance = String.format(distanceFormat, decimalFormat.format(distance/1000));
        }
        return displayDistance;
    }
    public LatLng getAnchorPoint(){
        if(anchorPoint == null){
            anchorPoint = AppUtils.getAveragePoint(trackingLocationList);
            if(anchorPoint != null) {
                if(trackingLocationList != null && trackingLocationList.size() > 1) {
                    float[] result = new float[2];
                    Location.distanceBetween(anchorPoint.latitude, anchorPoint.longitude, trackingLocationList.get(0).latitude, trackingLocationList.get(0).longitude, result);
                    float radius1 = result[0];
                    Location.distanceBetween(anchorPoint.latitude, anchorPoint.longitude, trackingLocationList.get(trackingLocationList.size() - 1).latitude, trackingLocationList.get(trackingLocationList.size() - 1).longitude, result);
                    float radius2 = result[0];

                    radius = (int) (Math.max(radius1, radius2) * 1.2);
                }else{
                    radius = AppConstant.DEFAULT_ZOOM_IN_METERS; // 1km
                }
            }
        }
        return anchorPoint;
    }
    public int getRadius(){
        return radius;
    }


    public static List<WorkoutItemData> fakeData(){
        List<WorkoutItemData> workoutItemDataList = new ArrayList<>();
        WorkoutItemData workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis();
        workoutItemData.duration = 600;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.797353, 106.657888));
        workoutItemData.trackingLocationList.add(new LatLng(10.798934, 106.659390));
        workoutItemData.trackingLocationList.add(new LatLng(10.799619, 106.660195));
        workoutItemData.trackingLocationList.add(new LatLng(10.799377, 106.661417));
        workoutItemData.trackingLocationList.add(new LatLng(11.480732, 106.887388));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 10000;
        workoutItemData.duration = 700;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 20000;
        workoutItemData.duration = 800;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.797353, 106.657888));
        workoutItemData.trackingLocationList.add(new LatLng(10.798934, 106.659390));
        workoutItemData.trackingLocationList.add(new LatLng(10.799619, 106.660195));
        workoutItemData.trackingLocationList.add(new LatLng(10.799377, 106.661417));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 30000;
        workoutItemData.duration = 900;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 40000;
        workoutItemData.duration = 1000;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.797353, 106.657888));
        workoutItemData.trackingLocationList.add(new LatLng(10.798934, 106.659390));
        workoutItemData.trackingLocationList.add(new LatLng(10.799619, 106.660195));
        workoutItemData.trackingLocationList.add(new LatLng(10.799377, 106.661417));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 50000;
        workoutItemData.duration = 1100;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 60000;
        workoutItemData.duration = 1200;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.797353, 106.657888));
        workoutItemData.trackingLocationList.add(new LatLng(10.798934, 106.659390));
        workoutItemData.trackingLocationList.add(new LatLng(10.799619, 106.660195));
        workoutItemData.trackingLocationList.add(new LatLng(10.799377, 106.661417));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 70000;
        workoutItemData.duration = 1300;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 80000;
        workoutItemData.duration = 1400;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.797353, 106.657888));
        workoutItemData.trackingLocationList.add(new LatLng(10.798934, 106.659390));
        workoutItemData.trackingLocationList.add(new LatLng(10.799619, 106.660195));
        workoutItemData.trackingLocationList.add(new LatLng(10.799377, 106.661417));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 90000;
        workoutItemData.duration = 1500;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 100000;
        workoutItemData.duration = 1600;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 110000;
        workoutItemData.duration = 1700;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 120000;
        workoutItemData.duration = 1800;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 130000;
        workoutItemData.duration = 1900;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        workoutItemData = new WorkoutItemData();
        workoutItemData.createdTime = System.currentTimeMillis() - 140000;
        workoutItemData.duration = 2000;
        workoutItemData.trackingLocationList = new ArrayList<>();
        workoutItemData.trackingLocationList.add(new LatLng(10.789157, 106.652826));
        workoutItemData.trackingLocationList.add(new LatLng(10.792235, 106.653030));
        workoutItemData.trackingLocationList.add(new LatLng(10.791855, 106.655186));
        workoutItemData.trackingLocationList.add(new LatLng(10.791170, 106.656538));
        workoutItemDataList.add(workoutItemData);

        return workoutItemDataList;
    }


}
