package com.example.androidtank;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {


    @Test
    public void isActivity() {

        ActivityScenario a = ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.MainActivity)).check(matches(isDisplayed()));


    }

    @Test
    public void launchManualActivity() throws Exception{

        ActivityScenario a = ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.button_play)).perform(click());

        onView(withId(R.id.ManualActivity)).check(matches(isDisplayed()));


    }


    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}