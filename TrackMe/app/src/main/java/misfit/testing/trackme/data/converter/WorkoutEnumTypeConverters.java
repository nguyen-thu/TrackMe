package misfit.testing.trackme.data.converter;

import android.arch.persistence.room.TypeConverter;

import misfit.testing.trackme.util.AppConstant;

/**
 * Created by Thu Nguyen on 7/16/2018.
 */

public class WorkoutEnumTypeConverters {

    @TypeConverter
    public static Integer getIntegerFromEnum(AppConstant.WORKOUT_STATE data) {
        return data.getValue();
    }

    @TypeConverter
    public static AppConstant.WORKOUT_STATE getEnumFromInteger(Integer value) {
        return AppConstant.WORKOUT_STATE.getEnum(value);
    }
}
