package misfit.testing.trackme.presentation.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import misfit.testing.trackme.R;
import misfit.testing.trackme.data.ui.WorkoutItemData;
import misfit.testing.trackme.presentation.ui.adapter.base.AbsRecyclerViewAdapter;
import misfit.testing.trackme.presentation.ui.viewholder.WorkoutListViewHolder;

/**
 * Created by Thu Nguyen on 6/15/2018.
 */

public class WorkoutListAdapter extends AbsRecyclerViewAdapter<WorkoutItemData, WorkoutListViewHolder>{
    @Override
    public WorkoutListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WorkoutListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ctrl_workout_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(WorkoutListViewHolder holder, int position) {
        holder.bind(getItem(position), position);
    }

    @Override
    public void onViewAttachedToWindow(WorkoutListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //holder.getMapFragmentAndCallback();
    }

    @Override
    public void onViewDetachedFromWindow(WorkoutListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        //holder.removeMapFragment();
    }

    @Override
    public void onViewRecycled(WorkoutListViewHolder holder) {
        super.onViewRecycled(holder);
        holder.freeMap();
    }
}
