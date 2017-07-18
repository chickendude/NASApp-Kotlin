package ch.ralena.nasapp

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.Intents.*
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class IntroScreenTest {

	@get:Rule
	public val activityRule = ActivityTestRule<LoadingActivity>(LoadingActivity::class.java)

	@Test
	fun skipIntroScreen() {
		init()
		onView(withId(R.id.skipButton)).perform(click())
		intended(hasComponent(MainActivity::class.java.name))
		release()
	}

}
