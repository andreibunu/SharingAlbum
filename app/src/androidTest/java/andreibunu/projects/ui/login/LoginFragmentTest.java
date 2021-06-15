package andreibunu.projects.ui.login;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.constraintlayout.solver.state.State;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.material.textfield.TextInputEditText;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Objects;

import andreibunu.projects.MainActivity;
import andreibunu.projects.R;

import static org.junit.Assert.*;

public class LoginFragmentTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            mActivity = activity;
        });
    }

    @After
    public void tearDown() {
        mActivity = null;
    }

    @Test
    public void test_checkIfLoginAppeared() {
        LoginFragment loginFragment = getFragment();
        Button btn = Objects.requireNonNull(loginFragment.getView()).findViewById(R.id.login_login_btn);
        assertEquals(btn.getText().toString(), mActivity.getResources().getString(R.string.sign_in));
        assertNotNull(btn);
    }

    public LoginFragment getFragment(){
        FrameLayout frameLayout = mActivity .findViewById(R.id.fragment);
        assertNotNull(frameLayout);
        LoginFragment loginFragment = new LoginFragment();
        mActivity.getSupportFragmentManager().beginTransaction()
                .replace(frameLayout.getId(), loginFragment).commitAllowingStateLoss();
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        return loginFragment;
    }

    @Test
    public void test_checkIfLoginSuccessful() {
        LoginFragment loginFragment = getFragment();

        loginFragment.getView().findViewById(R.id.login_login_btn).performClick();
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        assert(loginFragment.isVisible());
    }
}