package ir.hoseinsa.eyecareapp.utils

interface OnTimerServiceCallback {

    fun onBreakTimer(isBreak: Boolean): Boolean

    fun onTick(tick: Long): Long

}