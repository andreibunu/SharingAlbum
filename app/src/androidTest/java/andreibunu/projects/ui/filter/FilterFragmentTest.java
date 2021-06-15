package andreibunu.projects.ui.filter;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import andreibunu.projects.MainActivity;

public class FilterFragmentTest {

    @Rule
    ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        activityScenarioRule.getScenario().onActivity(activity -> {
            mActivity = activity;
        });
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @Test
    public void test() {

    }
}