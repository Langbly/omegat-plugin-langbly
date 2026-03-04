plugins {
    java
    signing
    distribution
    id("org.omegat.gradle") version "1.5.11"
}

version = "1.0.0"

omegat {
    version = "6.0.0"
    pluginClass = "com.langbly.omegat.LangblyTranslate"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

distributions {
    main {
        contents {
            from(tasks.jar)
            from("README.md")
            from("COPYING")
        }
    }
}

signing {
    if (project.hasProperty("signingKey")) {
        val signingKey: String by project
        val signingPassword: String by project
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(tasks.jar.get())
    }
}
