package com.aki.go4lunch.UI;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.aki.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainUITests {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @After
    public void tearDown() {
        AuthUI.getInstance().signOut(AuthUI.getApplicationContext());
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void loginTest() throws InterruptedException {
        Thread.sleep(500);

        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email")));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.email)));
        textInputEditText.perform(scrollTo(), click());

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.email_layout),
                                        0),
                                0)));
        textInputEditText2.perform(scrollTo(), replaceText("test@gmail.com"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction textInputEditText99 = onView(
                allOf(withId(R.id.password)));
        textInputEditText99.perform(scrollTo(), click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.password)));
        textInputEditText3.perform(scrollTo(), replaceText("azer1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction imageView = onView(
                allOf(withId(R.id.profile_pic),
                        withParent(allOf(withId(R.id.drawer_header),
                                withParent(withId(R.id.navigation_header_container)))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));
    }

    @Test
    public void bottomNavTest() throws InterruptedException {
        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email")));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.email)));
        textInputEditText.perform(scrollTo(), replaceText("test@gmail.com"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.password)));
        textInputEditText2.perform(scrollTo(), click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.password)));
        textInputEditText3.perform(scrollTo(), replaceText("azer1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction view = onView(
                allOf(withContentDescription("Google Map"),
                        withParent(withParent(withId(R.id.map_frag))),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.listFragment), withContentDescription("List View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView = onView(
                allOf(withId(R.id.restaurantName), withText("Chlew"),
                        withParent(withParent(withId(R.id.restaurantsRecyclerView))),
                        isDisplayed()));
        textView.check(matches(withText("Chlew")));

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.workmatesFragment), withContentDescription("WorkMates"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        Thread.sleep(1000);

        ViewInteraction viewGroup = onView(
                allOf((allOf(withId(R.id.workmatesRecyclerView))),
                        isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
    }

    @Test
    public void detailPageTest() throws InterruptedException {

        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email")));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.email)));
        textInputEditText.perform(scrollTo(), replaceText("test@gmail.com"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.password)));
        textInputEditText2.perform(scrollTo(), click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.password)));
        textInputEditText3.perform(scrollTo(), replaceText("azer1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.listFragment), withContentDescription("List View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView = onView(
                allOf(withId(R.id.restaurantName), withText("Chlew"),
                        withParent(withParent(withId(R.id.restaurantsRecyclerView))),
                        isDisplayed()));
        textView.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.restaurantDetailName), withText("Chlew"), isDisplayed()));
        textView2.check(matches(withText("Chlew")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.restaurantDetailAddress), isDisplayed())
        );
        textView3.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.restaurantDetailPic), isDisplayed())
        );
        imageView.check(matches(isDisplayed()));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.restaurantDetailWorkmateText), withText("Keanu is eating here !"),
                        isDisplayed()));
        textView5.check(matches(withText("Keanu is eating here !")));

        Thread.sleep(800);

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.detail_fab), withContentDescription("Make this place your lunch!"),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.restaurantDetailWorkmateText), withText("Tester TESTING is eating here !"),
                        isDisplayed()));
        textView4.check(matches(withText("Tester TESTING is eating here !")));

        Thread.sleep(800);

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.detail_fab), withContentDescription("Make this place your lunch!"),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        Thread.sleep(800);

        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Yes !"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.mapFragment), withContentDescription("Map View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

    }

    @Test
    public void sidePanelTest1() throws InterruptedException {

        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email")));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.email)));
        textInputEditText.perform(scrollTo(), replaceText("test@gmail.com"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.password)));
        textInputEditText2.perform(scrollTo(), click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.password)));
        textInputEditText3.perform(scrollTo(), replaceText("azer1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.listFragment), withContentDescription("List View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView = onView(
                allOf(withId(R.id.restaurantName), withText("Chlew"),
                        isDisplayed()));
        textView.perform(click());

        Thread.sleep(1000);

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.detail_fab), withContentDescription("Make this place your lunch!"),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.listFragment), withContentDescription("List View"),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        Thread.sleep(1000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction yourLunch = onView(
                allOf(withId(R.id.your_lunch), isDisplayed())
        );
        yourLunch.perform(click());

        Thread.sleep(1000);

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.restaurantDetailName), withText("Chlew"), isDisplayed()));
        textView2.check(matches(withText("Chlew")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.restaurantDetailAddress), isDisplayed())
        );
        textView3.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.restaurantDetailPic), isDisplayed())
        );
        imageView.check(matches(isDisplayed()));

        Thread.sleep(800);

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.detail_fab), withContentDescription("Make this place your lunch!"),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        Thread.sleep(800);

        ViewInteraction materialButton5 = onView(
                allOf(withId(android.R.id.button1), withText("Yes !"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        materialButton5.perform(scrollTo(), click());

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(withId(R.id.mapFragment), withContentDescription("Map View"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());
    }

    @Test
    public void sidePanelTest2() throws InterruptedException {

        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email")));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.email)));
        textInputEditText.perform(scrollTo(), replaceText("test@gmail.com"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.password)));
        textInputEditText2.perform(scrollTo(), click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.password)));
        textInputEditText3.perform(scrollTo(), replaceText("azer1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction settings = onView(
                allOf(withId(R.id.settings), isDisplayed())
        );
        settings.perform(click());

        ViewInteraction notificationSwitch = onView(
                allOf(withId(R.id.notification_switch), isDisplayed())
        );
        notificationSwitch.perform(click());
        notificationSwitch.perform(click());

        ViewInteraction confirmSettings = onView(
                allOf(withText("Confirm settings"), isDisplayed())
        );
        confirmSettings.perform(click());
    }

    @Test
    public void workmatesListTest() throws InterruptedException {

        ViewInteraction supportVectorDrawablesButton = onView(
                allOf(withId(R.id.email_button), withText("Sign in with email")));
        supportVectorDrawablesButton.perform(scrollTo(), click());

        ViewInteraction textInputEditText = onView(
                allOf(withId(R.id.email)));
        textInputEditText.perform(scrollTo(), replaceText("test@gmail.com"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(R.id.button_next), withText("Next"),
                        childAtPosition(
                                allOf(withId(R.id.email_top_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.ScrollView")),
                                                0)),
                                2)));
        materialButton.perform(scrollTo(), click());

        Thread.sleep(1000);

        ViewInteraction textInputEditText2 = onView(
                allOf(withId(R.id.password)));
        textInputEditText2.perform(scrollTo(), click());

        ViewInteraction textInputEditText3 = onView(
                allOf(withId(R.id.password)));
        textInputEditText3.perform(scrollTo(), replaceText("azer1234"), closeSoftKeyboard());

        ViewInteraction materialButton2 = onView(
                allOf(withId(R.id.button_done), withText("Sign in"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        materialButton2.perform(scrollTo(), click());

        Thread.sleep(2000);

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.workmatesFragment),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottom_nav_view),
                                        0),
                                2),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        Thread.sleep(1000);

        ViewInteraction workmate = onView(
                allOf(withText("Keanu is eating at Chlew"), isDisplayed())
        );
        workmate.perform(click());

        Thread.sleep(1500);

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.restaurantDetailName), withText("Chlew"), isDisplayed()));
        textView2.check(matches(withText("Chlew")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.restaurantDetailAddress), isDisplayed())
        );
        textView3.check(matches(isDisplayed()));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.restaurantDetailPic), isDisplayed())
        );
        imageView.check(matches(isDisplayed()));
    }
}
