package andreibunu.projects.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import java.util.Objects;

import andreibunu.projects.R;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.login.LoginFragment;

public class RegisterFragment extends BaseFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoginListener(view);

    }

    @Override
    protected void attach() {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_register;
    }

    @Override
    protected void injectDependencies() {
    }

    private void setLoginListener(View view) {
        TextView login = view.findViewById(R.id.register_login);
        login.setOnClickListener(v -> {
            FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                    .setCustomAnimations(R.anim.slide_back_in, R.anim.slide_back_out);
            LoginFragment fragment = new LoginFragment();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        });
    }

}