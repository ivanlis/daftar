package bilbao.ivanlis.daftar.constants

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class LessonDetailsMode {
    EDIT,
    TRAIN
}

@Parcelize
enum class WordScreenMode: Parcelable {
    EDIT,
    ANSWER,
    EVALUATE
}