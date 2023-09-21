package info.offthecob.calendar


fun main() {
    val configuration = configuration()
    val events = fetchEvents(configuration)
    val items = events.items
    if (items.isEmpty()) {
        return
    } else {
        for (event in items) {
            if (event.description == null) continue
            orgEvent(configuration, event)
        }
    }
}
