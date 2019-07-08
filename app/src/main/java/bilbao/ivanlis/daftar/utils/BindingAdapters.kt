package bilbao.ivanlis.daftar.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter
import bilbao.ivanlis.daftar.database.LessonItemForList
import bilbao.ivanlis.daftar.database.WordInitialFormTranslation
import java.text.SimpleDateFormat

@BindingAdapter("lessonNameFormatted")
fun TextView.lessonNameFormatted(item: LessonItemForList?) {
    item?.let {
        text = item.name
    }
}

@BindingAdapter("creationDateTimeFormatted")
fun TextView.creationDateTimeFormatted(item: LessonItemForList?) {
    item?.let {
        text = SimpleDateFormat("yyyy-MM-dd").format(item.creationDateTime)
    }
}

@BindingAdapter("wordCountFormatted")
fun TextView.wordCountFormatted(item: LessonItemForList?) {
    item?.let {
        text = item.wordCount.toString()
    }
}


@BindingAdapter("initialFormSpellingFormatted")
fun TextView.initialFormSpellingFormatted(item: WordInitialFormTranslation?) {
    item?.let {
        text = item.spelling
    }
}

@BindingAdapter("translationFormatted")
fun TextView.translationFormatted(item: WordInitialFormTranslation?) {
    item?.let {
        text = item.translation
    }
}
