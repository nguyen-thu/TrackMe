package misfit.testing.trackme.presentation.ui.viewholder.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;
import misfit.testing.trackme.data.ui.base.AbsItemData;

/**
 * Created by ThuNguyen on 11/15/15.
 */
public abstract class AbsRecyclerViewHolder<T extends AbsItemData> extends RecyclerView.ViewHolder {
    protected AbsRecyclerViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(T itemdata,int pos){

    }

    public T getDataItem() {
        return null;
    }

    public void setDataItem(T dataItem){
    }
}
