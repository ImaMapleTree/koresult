plugins {
    `maven-publish`
    id("kotlin-conventions")
    id("publish-conventions")
}

kotlin {
    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.coroutines.core)
                api(project(":koresult"))
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}
