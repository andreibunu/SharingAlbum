package andreibunu.projects.ui.login;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.R;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.database.DatabaseHandler;
import andreibunu.projects.databinding.FragmentLoginBinding;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.choice.ChoiceFragment;
import andreibunu.projects.ui.register.RegisterFragment;

import static android.content.Context.MODE_PRIVATE;

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
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoginListener(view);
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

    private void setLoginListener(View view) {
        Button login = view.findViewById(R.id.login_login_btn);
        login.setOnClickListener(v -> {
            ConstraintLayout loading = view.findViewById(R.id.login_progress_bar);
            loading.setVisibility(View.VISIBLE);
            loading.bringToFront();
            if (binding.loginUsernameEt.getText() != null && !(binding.loginUsernameEt.getText().toString().isEmpty())
                    && binding.loginPasswordEt.getText() != null && !(binding.loginPasswordEt.getText().toString().isEmpty())) {
                firebaseAuth.signInWithEmailAndPassword(
                        Objects.requireNonNull(binding.loginUsernameEt.getText()).toString(),
                        Objects.requireNonNull(binding.loginPasswordEt.getText()).toString()
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                            loading.setVisibility(View.GONE);
                            rememberUser(Objects.requireNonNull(binding.loginUsernameEt.getText()).toString(),
                                    Objects.requireNonNull(binding.loginPasswordEt.getText()).toString());
                            goToFilter();
                        }
                        else{
                            Toast.makeText(getContext(), "Please verify email to continue to application", Toast.LENGTH_LONG).show();
                            loading.setVisibility(View.GONE);
                        }
                    } else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Credentials are incorrect", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                loading.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Credentials are incorrect", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void rememberUser(String username, String password) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USERNAME", username);
        editor.putString("PASSWORD", password);

        editor.apply();
    }

    private void goToFilter() {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        ChoiceFragment fragment = new ChoiceFragment();
        ft.replace(R.id.fragment, fragment);
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