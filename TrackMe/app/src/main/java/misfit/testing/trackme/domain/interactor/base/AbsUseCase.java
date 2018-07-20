package misfit.testing.trackme.domain.interactor.base;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Abstract class for a Use Case.
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 * <p>
 * By convention each UseCase implementation will return the result using a {@link DisposableObserver}
 * that will execute its job in a background thread and will post the result in the UI thread.
 * Generic T: data return (Collection, model)
 * Generic Params: parameter, Void if it's unnecessary.
 */
public abstract class AbsUseCase<T, Q,
        P, B> {

    protected Q queryReq;
    protected P[] pathReqArr;
    protected B bodyReq;
    protected Observable<T> observable;

    public AbsUseCase() {
    }

    /**
     * Builds an {@link Observable} which will be used when executing the current {@link AbsUseCase}.
     */
    protected abstract Observable<T> buildUseCaseSingle();

    /**
     * @param q Parameters (Optional) used to build/execute this use case.
     * @return UseCase itself
     */
    public AbsUseCase query(Q q) {
        queryReq = q;
        return this;
    }
    public AbsUseCase path(P... p){
        pathReqArr = p;
        return this;
    }
    public AbsUseCase body(B b){
        bodyReq = b;
        return this;
    }

    /**
     * The abstract method is for the child to process particularly
     * @return
     */
    protected Disposable execute(){return null;}

    public Disposable execute(Scheduler executorScheduler, Scheduler postScheduler,
                              DisposableObserver<T> observer) {
        //Precondition.checkNotNull(observer);
        observable = buildUseCaseSingle();
        observable = observable.subscribeOn(executorScheduler).observeOn(postScheduler);
        return observable.subscribeWith(observer);
    }
    public Disposable execute(DisposableObserver<T> observer) {
        return execute(Schedulers.io(), AndroidSchedulers.mainThread(), observer);
    }
}
