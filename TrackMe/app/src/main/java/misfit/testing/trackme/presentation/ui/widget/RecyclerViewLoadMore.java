package misfit.testing.trackme.presentation.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by ThuNguyen on 7/16/2018.
 */

public class RecyclerViewLoadMore extends RecyclerView {
    public interface IOnLoadMoreListener{
        void onLoadMore();
    }
    private boolean isLoadingMore = false;
    private boolean isReadEnd = false;

    LinearLayoutManager layoutManager;
    private IOnLoadMoreListener onLoadMoreListener;

    public RecyclerViewLoadMore(Context context) {
        super(context);
        init(context);
    }

    public RecyclerViewLoadMore(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerViewLoadMore(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setOnLoadMoreListener(IOnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setReadEnd(boolean readEnd) {
        isReadEnd = readEnd;
    }

    public void onLoadMoreComplete(){
        isLoadingMore = false;
    }
    private void init(Context context){
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        setLayoutManager(layoutManager);

        addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (onLoadMoreListener != null) {
                    int totalItemCount = layoutManager.getItemCount();
                    int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (dy > 0 && !isLoadingMore && !isReadEnd
                            && totalItemCount <= (lastVisibleItem + 1) && totalItemCount > 0) {
                        // End has been reached
                        // Do something
                        isLoadingMore = true;
                        onLoadMoreListener.onLoadMore();
                    }
                }
            }
        });
    }
}
