package andreibunu.projects;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import andreibunu.projects.ui.choice.ChoiceFragment;
import andreibunu.projects.ui.login.LoginFragment;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getCanonicalName();

    public static final String SHARED_PREF = "SHARED_PREF";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        String username = sharedPreferences.getString(USERNAME, "-1");
        String password = sharedPreferences.getString(PASSWORD, "-1");

        if (username.equals("-1")) {
            goToLogin();

        } else {
            loginAndGoToChoice(username, password);
        }
    }

    private void goToLogin() {
        LoginFragment loginFragment = new LoginFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment, loginFragment);
        ft.commit();
    }

    private void loginAndGoToChoice(String username, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(
                username, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ChoiceFragment fragment = new ChoiceFragment();
                ft.replace(R.id.fragment, fragment);
                ft.commit();
            } else {
                goToLogin();
            }
        });

    }


}