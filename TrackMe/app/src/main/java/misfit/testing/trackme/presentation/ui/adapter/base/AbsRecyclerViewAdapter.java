package misfit.testing.trackme.presentation.ui.adapter.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import misfit.testing.trackme.data.ui.base.AbsItemData;
import misfit.testing.trackme.presentation.ui.viewholder.base.AbsRecyclerViewHolder;

/**
 * Created by ThuNguyen on 6/15/2018.
 */

public abstract class AbsRecyclerViewAdapter <T extends AbsItemData, U extends AbsRecyclerViewHolder> extends RecyclerView.Adapter<U>{
    protected Context context;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_PROG = 0;
    public static final int VIEW_HEADER = 2;
    public static final int VIEW_FOOTER = 3;
    List<T> dataList;

    public T getItem(int position) {
        return dataList.get(position);
    }

    public void setDataList(List<T> datalist){
        this.dataList = datalist;
    }

    public List<T> getDataList(){
        return dataList;
    }

    /**
     * Add the items to the current list on last
     * @param datalist
     */
    public void addItemsOnLast(List<T> datalist){
        if(datalist != null && datalist.size() > 0){
            this.dataList.addAll(datalist);
        }
    }

    /**
     * Add the items to the current list on first
     * @param datalist
     */
    public void addItemsOnFirst(List<T> datalist){
        if(datalist != null && datalist.size() > 0){
            this.dataList.addAll(0, datalist);
        }
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }
}
