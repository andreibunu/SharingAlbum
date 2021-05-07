package andreibunu.projects.ui.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import andreibunu.projects.R;
import andreibunu.projects.databinding.FragmentRegisterBinding;
import andreibunu.projects.domain.User;
import andreibunu.projects.ui.base.BaseFragment;
import andreibunu.projects.ui.login.LoginFragment;

public class RegisterFragment extends BaseFragment {

    private FragmentRegisterBinding binding;

    private FirebaseAuth firebaseAuth;

    private DatabaseReference myRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users").child(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLoginListener(view);
        setRegisterListener();

    }

    private void setRegisterListener() {
        binding.registerRegisterBtn.setOnClickListener(v -> {
            if (Objects.requireNonNull(binding.registerPasswordEt.getText()).toString().equals(
                    Objects.requireNonNull(binding.registerPasswordConfirmEt.getText()).toString()
            )) {
                User user = createUserFromUi();
                createUserAccount(user);
            }
        });
    }

    private void updateDatabase(User user) {
        myRef.child("username").setValue(user.getUsername());
        myRef.child("email").setValue(user.getEmail());
        myRef.child("theme").setValue("default");
    }

    private void createUserAccount(User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    updateDatabase(user);
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    UserProfileChangeRequest updatedUser = new UserProfileChangeRequest.Builder()
                            .setDisplayName(user.getName()).build();
                    if (firebaseUser != null) {
                        firebaseUser.updateProfile(updatedUser);
                        firebaseUser.sendEmailVerification();
                        Toast.makeText(getContext(), "Check your email for the verification email",
                                Toast.LENGTH_SHORT).show();
                        goToLogin();
                    }
                });
    }

    private User createUserFromUi() {
        return new User(Objects.requireNonNull(binding.registerNameEt.getText()).toString(),
                Objects.requireNonNull(binding.registerUsernameEt.getText()).toString(),
                Objects.requireNonNull(binding.registerEmailEt.getText()).toString(),
                Objects.requireNonNull(binding.registerPasswordEt.getText()).toString());
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
            goToLogin();
        });
    }

    private void goToLogin() {
        FragmentTransaction ft = Objects.requireNonNull(getFragmentManager()).beginTransaction()
                .setCustomAnimations(R.anim.slide_back_in, R.anim.slide_back_out);
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.fragment, fragment);
        ft.commit();
    }

}