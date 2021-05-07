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

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.R;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.databinding.FragmentLoginBinding;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.filter.FilterFragment;
import andreibunu.projects.ui.register.RegisterFragment;

public class LoginFragment extends BaseFragment {

    @Inject
    FaceRecognitionHandler handler;

    @Inject
    DatabaseHandler databaseHandler;

    FirebaseAuth firebaseAuth;

    FragmentLoginBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
        login.setOnClickListener(v -> firebaseAuth.signInWithEmailAndPassword(
                Objects.requireNonNull(binding.loginUsernameEt.getText()).toString(),
                Objects.requireNonNull(binding.loginPasswordEt.getText()).toString()
        ).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                goToFilter();
            }
        }));
    }

    private void goToFilter() {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        FilterFragment fragment = new FilterFragment();
        ft.replace(R.id.fragment, fragment).addToBackStack(TAG);
        ft.commit();
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