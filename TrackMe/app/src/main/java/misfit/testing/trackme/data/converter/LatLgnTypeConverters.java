package misfit.testing.trackme.data.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Thu Nguyen on 7/16/2018.
 */

public class LatLgnTypeConverters {
    static String separator = ";";
    @TypeConverter
    public static String getStringFromLatLng(LatLng data) {
        return String.valueOf(data.latitude) + separator + String.valueOf(data.longitude);
    }

    @TypeConverter
    public static LatLng getLatLngFromString(String data) {
        String[] latLngStr = data.split(separator);
        double latitude = Double.parseDouble(latLngStr[0]);
        double longitude = Double.parseDouble(latLngStr[1]);
        return new LatLng(latitude, longitude);
    }
}
