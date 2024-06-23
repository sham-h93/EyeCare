package example.hotaku.eyecareapp.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri

fun Context.activity(): Activity? {
    return this as? Activity ?: (this as? ContextWrapper)?.baseContext?.activity()
}

fun Context.openGithubPage() {
    val uri = Uri.parse("https://github.com/HoseinSadonasl/EyeCare")
    Intent(Intent.ACTION_VIEW, uri).also { intent ->
        startActivity(intent)
    }
}