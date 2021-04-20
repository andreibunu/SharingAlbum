package andreibunu.projects.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.App;
import andreibunu.projects.R;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.database.DatabaseImage;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.gallery.GalleryFragment;
import andreibunu.projects.ui.register.RegisterFragment;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginFragment extends BaseFragment {

    @Inject
    FaceRecognitionHandler handler;

    @Inject
    DatabaseHandler databaseHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setLoginListsner(view);
        setRegisterListener(view);

        App.getInstance().getAppComponent().inject(this);
        databaseHandler.getImages().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<List<DatabaseImage>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<DatabaseImage> databaseImages) {
                        databaseImages.forEach(el -> Log.d("DATA_IMAGES", el.toString()));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    @Override
    protected void attach() {
    }

    @Override
    protected int getLayoutResourceId() {
        return 0;
    }

    @Override
    protected void injectDependencies() {
    }

    private void setLoginListsner(View view) {
        Button login = view.findViewById(R.id.login_login_btn);
        login.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
            GalleryFragment fragment = new GalleryFragment();
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });
    }

    private void setRegisterListener(View view) {
        TextView register = view.findViewById(R.id.login_register);
        register.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .setCustomAnimations(R.anim.slide_in, R.anim.slide2_out);
            RegisterFragment fragment = new RegisterFragment();
            ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
            ft.commit();
        });
    }

}