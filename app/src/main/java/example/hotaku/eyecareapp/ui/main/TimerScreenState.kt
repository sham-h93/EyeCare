package example.hotaku.eyecareapp.ui.main

data class TimerScreenState(
    val isServiceStarted: Boolean = false,
    val isTimerStarted: Boolean = false,
    val isBreak: Boolean = false,
    val progress: Float = 0.0f,
    val time: String = "00:00"
)
