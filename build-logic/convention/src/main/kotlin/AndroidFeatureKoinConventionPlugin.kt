import com.lolo.io.onelist.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureKoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("koin-android").get())
                add("implementation", libs.findLibrary("koin-androidx-navigation").get())
                add("implementation", libs.findLibrary("koin-androidx-compose").get())
            }
        }
    }
}