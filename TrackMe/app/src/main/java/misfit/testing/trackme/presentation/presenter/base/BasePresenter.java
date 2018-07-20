package misfit.testing.trackme.presentation.presenter.base;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Thu Nguyen on 8/22/2017.
 */

public abstract class BasePresenter<V extends IVP.View> implements IVP.Presenter{
    protected V view;
    protected CompositeDisposable compositeDisposable;

    public BasePresenter(V view){
        this.view = view;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
    }
}
