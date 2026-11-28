package org.jetbrains.amper.ktor

import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class TimetableStore(private val path: String) {

    fun all(): List<Event> {
        val lines = File(path).readLines().drop(1).filter { it.isNotBlank() }

        return lines.map { line ->
            val fields = line.split(",")

            Event(
                id = fields[0].toInt(),
                staff_member = fields[1],
                module_code = fields[2],
                type = fields[3],
                date = LocalDate.parse(fields[4]),
                start_time = LocalTime.parse(fields[5]),
                finish_time = LocalTime.parse(fields[6]),
                location = fields[7],
                previous_location = fields[8].ifEmpty { null }
            )
        }
    }

    fun find(id: Int): Event? {
        return all().firstOrNull { it.id == id }
    }

    fun changeLocation(id: Int, newLocation: String): Boolean {
        if (newLocation.contains(",")) return false

        val event = find(id) ?: return false

        val updatedEvent = event.copy(
            previous_location = event.location,
            location = newLocation
        )

        val events = all().map {
            if (it.id == id) updatedEvent else it
        }

        val header = "id,staff_member,module_code,type,date,start_time,finish_time,location,previous_location"

        val lines = events.map {
            "${it.id},${it.staff_member},${it.module_code},${it.type},${it.date},${it.start_time},${it.finish_time},${it.location},${it.previous_location ?: ""}"
        }

        File(path).writeText((listOf(header) + lines).joinToString("\n"))

        return true
    }
}