package bilbao.ivanlis.daftar.utils

import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import bilbao.ivanlis.daftar.database.Lesson
import java.lang.StringBuilder
import java.text.SimpleDateFormat

fun formatLessons(lessons: List<Lesson>, resources: Resources): Spanned {
    val sb = StringBuilder()

    sb.apply {
        append("<h3>Your lessons</h3>")
        lessons.forEach{
            append("<br>")
            append("Id:\t${it.id}<br>")
            append("Name:\t${it.name}<br>")
            append("Created: \t${SimpleDateFormat("EEEE MMM-dd-yyyy' Time: 'HH:mm")
                .format(it.creationDateTime).toString()}")
            append("<br>")
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        return HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}