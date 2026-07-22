import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Applied only by :core:common. Plain Kotlin JVM module, no Android SDK dependency at all —
 * the architecture guide requires the domain layer to import nothing but pure Kotlin.
 */
class TastyBudsJvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("kotlin-kapt")
            }

            // Forces Kotlin compilation onto a JDK 17 toolchain (auto-downloaded via the
            // Foojay resolver in settings.gradle.kts) instead of whatever JDK is running the
            // Gradle daemon — needed because Kotlin 1.9.22 can't parse newer JDK version strings.
            extensions.configure<KotlinJvmProjectExtension> {
                jvmToolchain(17)
            }

            tasks.withType<KotlinCompile>().configureEach {
                kotlinOptions {
                    jvmTarget = "17"
                }
            }

            tasks.withType<Test>().configureEach {
                useJUnitPlatform()
            }
        }
    }
}
