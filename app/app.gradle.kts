plugins {
    id("info.offthecob.Base")
    application
}

dependencies {
    implementation("com.google.api-client:google-api-client:2.0.0")
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-calendar:v3-rev20220715-2.0.0")
    implementation(libs.bundles.guice)
}

application {
    // Define the main class for the application.
    mainClass.set("gcal.to.ics.app.AppKt")
}
