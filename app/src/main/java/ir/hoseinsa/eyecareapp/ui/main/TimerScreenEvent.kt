package ir.hoseinsa.eyecareapp.ui.main

import android.content.Context

sealed class TimerScreenEvent() {

    data class startTimer(val context: Context): TimerScreenEvent()

    data class stopTimer(val context: Context): TimerScreenEvent()

}
