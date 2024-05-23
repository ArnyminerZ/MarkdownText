import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlin)

    `maven-publish`
    signing
}

fun readProperties(fileName: String): Properties {
    val propsFile = project.rootProject.file(fileName)
    if (!propsFile.exists()) {
        throw GradleException("$fileName doesn't exist")
    }
    if (!propsFile.canRead()) {
        throw GradleException("Cannot read $fileName")
    }
    return Properties().apply {
        propsFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "com.arnyminerz.markdowntext"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        // versionName = "1.0.0"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    // Android dependencies
    implementation(libs.androidx.core)

    // Jetpack Compose core
    implementation(libs.androidx.activityCompose)
    implementation(libs.compose.material3.core)
    implementation(libs.compose.ui.base)
    implementation(libs.compose.ui.toolingPreview)

    // Jetpack Compose Code Highlighter
    implementation(libs.compose.codeEditor)

    // Coil image loader
    implementation(libs.coil.base)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.svg)

    // Markdown parsing
    implementation(libs.jetbrains.markdown)

    // Kotlin libraries
    implementation(libs.kotlin.reflect)

    testImplementation(kotlin("test"))

    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
}

val androidSourcesJar = tasks.create("androidSourcesJar", Jar::class.java) {
    archiveClassifier = "sources"
    if (project.plugins.findPlugin("com.android.library") != null) {
        from(android.sourceSets.getByName("main").java.srcDirs)
    } else {
        from(sourceSets.getByName("main").java.srcDirs)
    }
}

artifacts {
    archives(androidSourcesJar)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "com.arnyminerz.markdowntext"
            artifactId = "markdowntext"
            version = "2.0.0-dev02"

            if (project.plugins.findPlugin("com.android.library") != null) {
                from(components.findByName("release"))
            } else {
                artifact("${layout.buildDirectory}/libs/${project.name}-${version}.jar")
            }

            artifact(androidSourcesJar)

            pom {
                name = "Markdown Text"
                description =
                    "A Jetpack Compose component used for displaying Markdown-formatted text."
                url = "https://github.com/ArnyminerZ/MarkdownText"

                scm {
                    connection = "scm:git:github.com/ArnyminerZ/MarkdownText.git"
                    developerConnection = "scm:git:ssh://github.com/ArnyminerZ/MarkdownText.git"
                    url = "https://github.com/ArnyminerZ/MarkdownText/tree/master"
                }

                developers {
                    developer {
                        id = "ArnyminerZ"
                        name = "Arnau Mora"
                        email = "arnyminerz@proton.me"
                    }
                }

                licenses {
                    license {
                        name = "Apache License"
                        url = "https://github.com/ArnyminerZ/MarkdownText/blob/master/LICENSE"
                    }
                }
            }
        }
    }
    repositories {
        mavenLocal()
        maven {
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
        }
    }
}

signing {
    sign(publishing.publications)
}
