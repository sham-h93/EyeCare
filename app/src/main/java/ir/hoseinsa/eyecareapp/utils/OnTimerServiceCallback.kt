package ir.hoseinsa.eyecareapp.utils

interface OnTimerServiceCallback {

    fun onBreakTimer(isBreak: Boolean)

    fun onTick(tick: Long)

}