package ch.ralena.nasapp

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostcardTest {

	@get:Rule
	val activityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

	@Test
	fun roversAreLoaded() {
		onView(withText("Curiosity")).check(matches(withText("Curiosity")))
	}

	@Test
	fun clickingRoverChangesCameras() {
		onView(withText("Curiosity")).perform(click())

		onView(withText("mast")).check(matches(withText("mast")))
	}
}
