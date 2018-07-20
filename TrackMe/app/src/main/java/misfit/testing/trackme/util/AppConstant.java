package misfit.testing.trackme.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thu Nguyen on 7/17/2018.
 */

public class AppConstant {
    public static final int DEFAULT_ZOOM_IN_METERS = 1000; // 1km
    public static final int ITEMS_PER_PAGE = 10;
    public static final int LOCATION_INTERVAL = 1000;
    public static final int LOCATION_FASTEST_INTERVAL = 1000;
    // Define the state of workout session
    public enum WORKOUT_STATE{
        NONE(0),
        ACTIVE(1),
        PAUSE(2),
        STOP(3);
        private static final Map<Integer, WORKOUT_STATE> enumsByValue = new HashMap<Integer, WORKOUT_STATE>();
        static {
            for (WORKOUT_STATE type : WORKOUT_STATE.values()) {
                enumsByValue.put(type.value, type);
            }
        }

        public static WORKOUT_STATE getEnum(int value) {
            return enumsByValue.get(value);
        }
        private final int value;
        private WORKOUT_STATE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static final String WORKOUT_ITEM = "workout_item";
    public static final String PENDING_WORKOUT_ITEM = "pending_workout_item";
}
