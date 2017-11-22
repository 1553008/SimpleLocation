package com.example.hoangdung.simplelocation;

import android.support.test.espresso.FailureHandler;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.hoangdung.simplelocation.Activity.MapsActivity;
import com.example.hoangdung.simplelocation.Adapter.PlaceAutoCompleteAdapter;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import  android.support.test.espresso.Espresso.*;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.concurrent.RecursiveAction;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by hoangdung on 11/18/17.
 */

@RunWith(AndroidJUnit4.class)
public class DirectionsTest {
    @Rule
    public ActivityTestRule<MapsActivity> mapsActivityActivityTestRule = new ActivityTestRule<MapsActivity>(MapsActivity.class);

    @Test
    public void test() {
        //Click the toolbar button
        onView(withId(R.id.toolbar_search))
                .perform(click());

        //Type Hau Giang

        onView(withId(R.id.search_editText))
                .perform(typeText("Hau Giang"));

        onView(isRoot())
                .perform(waitFor(3000));

        //Choose Search Places
        onView(withId(R.id.recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(2, click()))
                .withFailureHandler(new FailureHandler() {
                    @Override
                    public void handle(Throwable error, Matcher<View> viewMatcher) {
                        Log.d("MapsActivity", "Something wrong with ViewHolder");
                    }
                });

        onView(isRoot())
                .perform(waitFor(3000));

        //Find directions
        onView(withId(R.id.find_direction))
                .perform(click());

        onView(isRoot())
                .perform(waitFor(30000));

    }


    public static ViewAction waitFor(final long millis){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "wait for" + millis + "milliseconds";
            }

            @Override
            public void perform(UiController uiController, View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
    public static Matcher<RecyclerView.ViewHolder> withHolder(String text){
        return new BoundedMatcher<RecyclerView.ViewHolder, PlaceAutoCompleteAdapter.PlaceViewHolder>(PlaceAutoCompleteAdapter.PlaceViewHolder.class) {
            @Override
            public void describeTo(Description description) {
                return;
            }

            @Override
            protected boolean matchesSafely(PlaceAutoCompleteAdapter.PlaceViewHolder item) {

                return item.primaryText.getText().toString().contains("Hau Giang");
            }
        };
    }
    public static ViewAction throughLastPosition(final Matcher<View> matcher){
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return new BoundedMatcher<View,RecyclerView>(RecyclerView.class) {

                    @Override
                    public void describeTo(Description description) {

                    }

                    @Override
                    protected boolean matchesSafely(RecyclerView item) {
                        return true;
                    }
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
            }
        };

    }
}
