package andreibunu.projects.ui.base;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import andreibunu.projects.R;
public abstract class BaseFragment<BaseMvpPresenter> extends Fragment {

    public final String TAG = getClass().getCanonicalName();

    protected BaseMvpPresenter presenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    protected abstract void attach();
    protected abstract int getLayoutResourceId();
    protected abstract void injectDependencies();
}