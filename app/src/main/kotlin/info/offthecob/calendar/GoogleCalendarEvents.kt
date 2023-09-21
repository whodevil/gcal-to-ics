package info.offthecob.calendar

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.DateTime
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Events
import java.io.File
import java.io.InputStreamReader

private fun fetchCredentials(configuration: Configuration, jsonFactory: GsonFactory, transport: NetHttpTransport): Credential {
    val credentialsFile = File(configuration.credentialsPath)
    val tokensPath = File(configuration.tokensPath)
    val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(credentialsFile.inputStream()))
    val scopes = listOf(CalendarScopes.CALENDAR_READONLY)
    val flow = GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, scopes).setDataStoreFactory(FileDataStoreFactory(tokensPath)).setAccessType("offline").build()
    val receiver = LocalServerReceiver.Builder().setPort(8888).build()
    return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
}

fun fetchEvents(configuration: Configuration): Events {
    val jsonFactory = GsonFactory.getDefaultInstance()
    val transport = GoogleNetHttpTransport.newTrustedTransport()
    val credentials = fetchCredentials(configuration, jsonFactory, transport)
    val service = Calendar.Builder(transport, jsonFactory, credentials).setApplicationName("calendar reader").build()
    val now = DateTime(System.currentTimeMillis())
    return service.events().list(configuration.calendarId).setMaxResults(100).setTimeMin(now).setOrderBy("startTime").setSingleEvents(true).execute()
}
