package example.hotaku.eyecareapp.utils

interface OnTimerServiceCallback {

    fun onBreakTimer(isBreak: Boolean)

    fun onTick(tick: Long)

}