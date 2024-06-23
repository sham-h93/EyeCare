package example.hotaku.eyecareapp.presentation.utils

import example.hotaku.timer.utils.TimerUtils

fun millisToProgressValue(timerValue: Pair<Long?, Boolean>) = timerValue.first!!.toFloat() / if (timerValue.second) TimerUtils.BREAK_TIMER_MILLISECODS else TimerUtils.CONTINUE_TIMER_MILLISECONDS
