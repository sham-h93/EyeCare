package example.hotaku.eyecareapp.ui.main

data class TimerScreenState(
    val isStarted: Boolean = false,
    val isBreak: Boolean = false,
    val time: String = "00:00"
)
