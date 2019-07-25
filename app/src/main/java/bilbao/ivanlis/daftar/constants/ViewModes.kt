package bilbao.ivanlis.daftar.constants

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class LessonDetailsMode: Parcelable {
    EDIT,
    TRAIN
}

@Parcelize
enum class WordScreenMode: Parcelable {
    EDIT,
    ANSWER,
    EVALUATE
}