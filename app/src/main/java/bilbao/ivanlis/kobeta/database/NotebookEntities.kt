package bilbao.ivanlis.kobeta.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "language")
data class Language(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "native_name")
    val nativeName: String,
    @ColumnInfo(name = "english_name")
    val englishName: String
)

@Entity(
    tableName = "part_of_speech",
    foreignKeys = [ForeignKey(
        entity = Language::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("language_id"),
        onDelete = CASCADE
    )]
)
data class PartOfSpeech(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "language_id")
    val languageId: Long,
    @ColumnInfo(name = "native_name")
    val nativeName: String,
    @ColumnInfo(name = "english_name")
    val englishName: String
)

@Entity(
    tableName = "form",
    foreignKeys = [ForeignKey(
        entity = PartOfSpeech::class,
        parentColumns = ["id"],
        childColumns = ["part_of_speech_id"],
        onDelete = CASCADE
    )]
)
data class Form(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "part_of_speech_id")
    val partOfSpeechId: Long,
    @ColumnInfo(name = "initial")
    val initial: Boolean,
    @ColumnInfo(name = "native_name")
    val nativeName: String,
    @ColumnInfo(name = "english_name")
    val englishName: String
)

@Entity(tableName = "lesson")
data class Lesson(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "creation_date_time")
    val creationDateTime: Long
)


@Entity(
    tableName = "word",
    foreignKeys = [ForeignKey(
        entity = Lesson::class,
        parentColumns = ["id"],
        childColumns = ["lesson_id"],
        onDelete = CASCADE
    )]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "translation")
    val translation: String,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Long
)

@Entity(tableName = "score",
    foreignKeys = [ForeignKey(
        entity = Word::class,
        parentColumns = ["id"],
        childColumns = ["word_id"],
        onDelete = CASCADE)])
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name="word_id")
    val wordId: Long,
    @ColumnInfo(name="date_time")
    val dateTime: Long,
    @ColumnInfo(name = "score_value")
    val scoreValue: Double
)

@Entity(
    tableName = "word_record",
    foreignKeys = [ForeignKey(
        entity = Word::class,
        parentColumns = ["id"],
        childColumns = ["word_id"],
        onDelete = CASCADE
    )]
)
data class WordRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "word_id")
    val wordId: Long,
    @ColumnInfo(name = "form_id")
    val formId: Long,
    @ColumnInfo(name = "spelling")
    val spelling: String
)


