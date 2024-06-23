package example.hotaku.eyecareapp.presentation.main

data class TimerScreenState(
    val isTimerStarted: Boolean = false,
    val isBreak: Boolean = false,
    val progress: Float = 0.0f,
    val time: String = "00:00"
)
