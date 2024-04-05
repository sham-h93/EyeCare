package ir.hoseinsa.eyecareapp.utils

fun Long.toTimeFormat(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val formattedMinutes = if (minutes.toString().length == 1) "0$minutes" else minutes
    return "$formattedMinutes:$seconds"
}