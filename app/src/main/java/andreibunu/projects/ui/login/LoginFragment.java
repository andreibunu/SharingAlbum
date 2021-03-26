package andreibunu.projects.ui.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import andreibunu.projects.R;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.gallery.GalleryFragment;
import andreibunu.projects.ui.register.RegisterFragment;

public class LoginFragment extends BaseFragment {
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
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
                GalleryFragment fragment = new GalleryFragment();
                ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
                ft.commit();
            }
        });
    }

    private void setRegisterListener(View view) {
        TextView register = view.findViewById(R.id.login_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.slide2_out);
                RegisterFragment fragment = new RegisterFragment();
                ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
                ft.commit();
            }
        });
    }

}