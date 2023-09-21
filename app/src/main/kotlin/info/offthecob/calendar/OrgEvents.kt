package info.offthecob.calendar

import com.google.api.services.calendar.model.Event
import java.io.File
import java.time.format.DateTimeFormatter

fun orgEvent(configuration: Configuration, event: Event) {
    val stringBuilder = StringBuilder()
    fileHeaders(stringBuilder, configuration, event.summary)
    orgEventBody(stringBuilder, event)
    File("${configuration.outputDir}/${fileName(event)}").writeText(stringBuilder.toString())
}

private fun fileName(event: Event): String {
    val date = if (event.start.dateTime != null) {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
        val startTime = formatter.format(DateTimeFormatter.ISO_DATE_TIME.parse(event.start.dateTime.toStringRfc3339()))
        startTime
    } else {
        event.start.date.toStringRfc3339().replace("-","")
    }
    return "$date-${event.iCalUID.replace("@google.com","")}.org"
}
private fun fileHeaders(stringBuilder: StringBuilder, configuration: Configuration, summary: String) {
    stringBuilder.append("""
        #+TITLE: $summary
        #+AUTHOR: ${configuration.author}
        #+EMAIL: ${configuration.email}
        #+CATEGORY: ${configuration.category}
        #+STARTUP: hidestars
        #+STARTUP: overview
        #+FILETAGS: ${configuration.fileTags}
    """.trimIndent())
    stringBuilder.append("\n")
}

private fun orgEventBody(stringBuilder: StringBuilder, event: Event) {
    //            """:
//                  :LOGBOOK:
//                  CLOCK: [2018-12-29 Sat 16:30]--[2018-12-29 Sat 17:30] =>  0:00
//                  :END
//                <2016-08-08 Mon +1y>
//                """
    stringBuilder.append("\n")
    orgEventProperties(stringBuilder, event)
    timeline(event, stringBuilder)
    attendees(event, stringBuilder)
    description(event, stringBuilder)
    hangout(event, stringBuilder)
}

private fun orgEventProperties(stringBuilder: StringBuilder, event: Event) {
    stringBuilder.append("""
                * ${event.summary}
                  :PROPERTIES:
                  :ID:        ${event.iCalUID}
                  :ATTENDING: ATTENDING
                  :END:
                  """.trimIndent())
}

private fun timeline(event: Event, stringBuilder: StringBuilder) {
    stringBuilder.append("\n${timeline(event)}")
}

private fun timeline(event: Event): String {
    return if (event.start.dateTime != null) {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd EEE HH:mm")
        val startTime = formatter.format(DateTimeFormatter.ISO_DATE_TIME.parse(event.start.dateTime.toStringRfc3339()))
        val endTime = formatter.format(DateTimeFormatter.ISO_DATE_TIME.parse(event.end.dateTime.toStringRfc3339()))
        "<$startTime>--<$endTime>"
    } else {
        "<${event.start.date}>"
    }
}

private fun attendees(event: Event, stringBuilder: StringBuilder) {
    if (event.attendees != null) {
        val attendees = event.attendees.map {
            val attending = if (it.responseStatus == "accepted") "X" else " "
            " - [$attending] ${it.email}"
        }.joinToString(separator = "\n")
        stringBuilder.append("\n** Attendees\n")
        stringBuilder.append(attendees)
    }
}

private fun description(event: Event, stringBuilder: StringBuilder) {
    if (event.description != null) {
        stringBuilder.append("\n** Description\n")
        stringBuilder.append(event.description.trim().replace("<br>", "\n").replace("<p>", "").replace("</p>", "").replace("<span>", "").replace("</span>", "").replace("<strong>", "").replace("</strong>", ""))
    }
}

private fun hangout(event: Event, stringBuilder: StringBuilder) {
    if (event.hangoutLink != null) {
        stringBuilder.append("\n** Google Hangout")
        stringBuilder.append("\n${event.hangoutLink}")
    }
}
