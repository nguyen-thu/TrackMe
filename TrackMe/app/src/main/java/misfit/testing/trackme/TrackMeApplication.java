package misfit.testing.trackme;

import android.app.Application;

import misfit.testing.trackme.data.storage.local.AppDB;

/**
 * Created by Thu Nguyen on 7/16/2018.
 */

public class TrackMeApplication extends Application {
    private static TrackMeApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        AppDB.init(this);

        // Add workoutlist here

//        Thread thread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    List<WorkoutItemData> workoutItemDataList = WorkoutItemData.fakeData();//AppDB.getInstance().workoutDAO().loadWorkoutList(offset);
//                    AppDB.getInstance().workoutDAO().upsertWorkoutList(workoutItemDataList);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };

//        thread.start();
    }
    public static TrackMeApplication getInstance(){
        return instance;
    }
}
