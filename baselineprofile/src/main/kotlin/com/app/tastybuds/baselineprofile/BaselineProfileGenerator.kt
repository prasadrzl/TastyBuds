package com.app.tastybuds.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates a Baseline Profile for TastyBuds.
 *
 * Run with a Gradle Managed Device:
 *   ./gradlew :app:generateBaselineProfile
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(
        packageName = "com.app.tastybuds",
        includeInStartupProfile = true,
    ) {
        pressHome()
        startActivityAndWait()
        // Let the home content compose/settle so its classes are captured too.
        device.waitForIdle()
    }
}
