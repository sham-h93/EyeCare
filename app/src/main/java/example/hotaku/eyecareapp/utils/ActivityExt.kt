package example.hotaku.eyecareapp.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

fun Context.activity(): Activity? {
    return this as? Activity ?: (this as? ContextWrapper)?.baseContext?.activity()
}