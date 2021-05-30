package com.example.androidtank;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.getIntents;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

@RunWith(AndroidJUnit4ClassRunner.class)
public class ManualActivityTest {

    @Rule
    public ActivityTestRule<ManualActivity> activityTestRule =
            new ActivityTestRule<ManualActivity>(ManualActivity.class);
    @Test
    public void isActivity() {


        onView(withId(R.id.ManualActivity)).check(matches(isDisplayed()));

    }

    @Test
    public void successfulConnectionAtActivityLaunch() {

        ManualActivity activity = activityTestRule.getActivity();

        // Checks if Toast message is outputting successful connection to TANK
        onView(withText(R.string.toast_text)).
                inRoot(withDecorView(not(is(activity.getWindow().getDecorView())))).
                check(matches(isDisplayed()));

    }

    @Test
    public void breakButton() {
        ManualActivity m = activityTestRule.getActivity();
        onView(withId(R.id.break_button)).perform(click());
        Assert.assertTrue(m.getVehicleBrake());


    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}