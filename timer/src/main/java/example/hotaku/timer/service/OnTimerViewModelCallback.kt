package example.hotaku.timer.service

interface OnTimerViewModelCallback {

    fun onBreakTimer(isBreak: Boolean)

    fun onTick(tick: Long)

}