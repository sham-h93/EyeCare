package example.hotaku.timer.service

interface ServiceTimerCallback {

    fun onBreakTimer(isBreak: Boolean)

    fun onTick(tick: Long)

    fun onStop(isKilled: Boolean)

}