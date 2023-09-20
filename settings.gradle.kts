plugins {
    id("info.offthecob.Settings") version "1.0.11"
}

rootProject.name = "gcal-to-org"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            from("info.offthecob.platform:catalog:1.0.11")
            library("offthecob-platform", "info.offthecob.platform:bom:1.0.11")
        }
    }
}
