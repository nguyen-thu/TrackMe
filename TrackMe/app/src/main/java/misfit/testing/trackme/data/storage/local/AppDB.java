package misfit.testing.trackme.data.storage.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import misfit.testing.trackme.data.storage.local.dao.PendingWorkoutDAO;
import misfit.testing.trackme.data.storage.local.dao.WorkoutDAO;
import misfit.testing.trackme.data.ui.PendingWorkoutItemData;
import misfit.testing.trackme.data.ui.WorkoutItemData;

/**
 * Created by Thu Nguyen on 9/28/2017.
 */

@Database(version = 1, entities = {WorkoutItemData.class, PendingWorkoutItemData.class})
public abstract class AppDB extends RoomDatabase{
    private static final String DB_NAME = "track_me";

    private static AppDB instance;
    private static Context _context;

    public static AppDB getInstance(){
        if(instance == null){
            instance = Room.databaseBuilder(_context.getApplicationContext(),
                    AppDB.class, DB_NAME).build();
        }
        return instance;
    }
    public static void init(Context context) {
        _context = context;
    }
    /************************** Define some DAO methods ************************************/
    public abstract WorkoutDAO workoutDAO();
    public abstract PendingWorkoutDAO pendingWorkoutDAO();

    /************************** Define some class as table ***********************************/
    public class Workout{
        public static final String TABLE_NAME = "work_out";
        public static final String queryAll = "select * from " + TABLE_NAME;
        public static final String queryBlockByPage = "select * from " + TABLE_NAME + " order by created_time desc limit :limit offset :offset";
    }
    public class PendingWorkout{
        public static final String TABLE_NAME = "pending_work_out";
        public static final String queryAll = "select * from " + TABLE_NAME;
        public static final String deleteAll = "delete from " + TABLE_NAME;
    }
}
