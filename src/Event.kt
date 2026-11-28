package org.jetbrains.amper.ktor

import java.time.LocalDate
import java.time.LocalTime

data class Event(
    val id: Int,
    val staff_member: String,
    val module_code: String,
    val type: String,
    val date: LocalDate,
    val start_time: LocalTime,
    val finish_time: LocalTime,
    val location: String,
    val previous_location: String?
)