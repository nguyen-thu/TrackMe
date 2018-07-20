package misfit.testing.trackme.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Thu Nguyen on 7/17/2018.
 */

public class AppUtils {

    public static int getHeightDevice(Activity c) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        c.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getWidthDevice(Activity c) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        c.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Return the display hhmmss from ss
     * @param seconds
     * @return the display HH:mm:ss
     */
    public static final String getDisplayDuration(int seconds){
        int hour = seconds / 3600;
        int minute = (seconds - (hour*3600)) / 60;
        int second = seconds % 60;

        String result = "";
        if(hour >= 1){
            if(hour < 10){
                result += "0" + hour + ":";
            }else{
                result += hour + ":";
            }
        }else{
            result += "00:";
        }
        if(minute < 10){
            result += "0" + minute + ":";
        }else{
            result += minute + ":";
        }
        if(second < 10){
            result += "0" + second;
        }else{
            result += second + "";
        }
        return  result;
    }

    /**
     *
     * @param meters
     * @param second
     * @return
     */
    public static double convertToKmhFromMs(double meters, int second){
        return (meters*3600) / (second * 1000);
    }

    /**
     *
     * @param speed
     * @return value with km/h
     */
    public static double convertToKmhFromMs(double speed){
        return (speed * 3600) / 1000;
    }
    public static double convertMetersToMiles(int meters){
        return ((double)meters / 1609.344);
    }
    public static LatLng getAveragePoint(List<LatLng> latLngList){
        double latitude = 0;
        double longitude = 0;
        if(latLngList != null && latLngList.size() > 0){
            for(LatLng latLng : latLngList){
                latitude += latLng.latitude;
                longitude += latLng.longitude;
            }
            latitude = latitude / latLngList.size();
            longitude = longitude / latLngList.size();
        }
        if(latitude != 0 && longitude != 0) {
            return new LatLng(latitude, longitude);
        }
        return null;
    }

}
