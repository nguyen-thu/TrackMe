package misfit.testing.trackme.data.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by Thu Nguyen on 7/16/2018.
 */

public class IntegerListTypeConverters {

    @TypeConverter
    public static List<Integer> stringToSomeObjectList(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Integer>>() {}.getType();

        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String someObjectListToString(List<Integer> someObjects) {
        return new Gson().toJson(someObjects);
    }
}
