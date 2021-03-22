package com.kevinwei.vote

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter


val DisplayFormat = SimpleDateFormat("EEEE MM-dd-yyyy")

/**
 * Convert dates to the user LOCAL time to be displayed on the UI
 * timestamp is in ISO 8601 standard
 * '2011-12-03T10:15:30Z' <- ISO_INSTANT
 * '2021-03-01T00:00:00.000Z' <- ISO 8601
 * yyyy-MM-dd T hh:mm:ss.mmm Z
 */
fun convertDateToLocalFormatted(timestring: String): String {
//    val date = DisplayFormat.parse(timestring)
    val formatter = DateTimeFormatter.ofPattern("EEEE MM-dd-yyyy")
    val date = LocalDate.parse(timestring,formatter)
    return date.toString()
}


