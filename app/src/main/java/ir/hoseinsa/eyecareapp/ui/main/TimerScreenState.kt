package ir.hoseinsa.eyecareapp.ui.main

data class TimerScreenState(
    val isStarted: Boolean = false,
    val isBreak: Boolean = false,
    val time: String = "00:00"
)
