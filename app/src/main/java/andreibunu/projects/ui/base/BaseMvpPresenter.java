package andreibunu.projects.ui.base;


public class BaseMvpPresenter<BaseMvpView> {
    public final String TAG = getClass().getCanonicalName();
    protected BaseMvpView view;
    public void attach(BaseMvpView view){
        this.view = view;
    }
}
