package bilbao.ivanlis.daftar.database

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import bilbao.ivanlis.daftar.constants.POS_NOUN
import bilbao.ivanlis.daftar.constants.POS_PARTICLE
import bilbao.ivanlis.daftar.constants.POS_VERB

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
    )],
    indices = [Index(value = ["language_id"])]
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
    )],
    indices = [Index(value = ["part_of_speech_id"])]
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
    var id: Long = 0L,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "creation_date_time")
    var creationDateTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "description")
    val description: String = ""
)


@Entity(
    tableName = "word",
    foreignKeys = [ForeignKey(
        entity = Lesson::class,
        parentColumns = ["id"],
        childColumns = ["lesson_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["lesson_id"])]
)
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "translation")
    val translation: String,
    @ColumnInfo(name = "lesson_id")
    val lessonId: Long
)

@Entity(
    tableName = "score",
    foreignKeys = [ForeignKey(
        entity = Word::class,
        parentColumns = ["id"],
        childColumns = ["word_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["word_id"])]
)
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "word_id")
    val wordId: Long,
    @ColumnInfo(name = "date_time")
    val dateTime: Long,
    @ColumnInfo(name = "score_value")
    val scoreValue: Double
)

@Entity(
    tableName = "word_record",
    foreignKeys = [
        ForeignKey(
            entity = Word::class,
            parentColumns = ["id"],
            childColumns = ["word_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Form::class,
            parentColumns = ["id"],
            childColumns = ["form_id"]
        )],
    indices = [Index(value = ["word_id"]), Index(value = ["form_id"])]
)
data class WordRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "word_id")
    val wordId: Long,
    @ColumnInfo(name = "form_id")
    val formId: Long,
    @ColumnInfo(name = "spelling")
    val spelling: String
)



// views

// View to show lessons: name, date, number of words
@DatabaseView("""
    SELECT les.id AS id, les.name AS name, les.creation_date_time AS creationDateTime, count(w.id) AS wordCount
    FROM lesson as les LEFT JOIN word AS w ON les.id = w.lesson_id
    GROUP BY les.id ORDER BY les.creation_date_time DESC
""")
data class LessonItemForList(
    val id: Long,
    val name: String,
    val creationDateTime: Long,
    val wordCount: Long
)

// auxiliary structures
// initial form and translation
data class WordInitialFormTranslation(val wordId: Long,
                                      val spelling: String,
                                      val translation: String,
                                      val partOfSpeechName: String)

data class WordPartOfSpeech(val wordId: Long,
                            val posName: String,
                            val posId: Long)

// Verb forms for Arabic
data class ArabicVerbForms(val wordId: Long,
                           val translation: String,
                           val pastForm: String,
                           val nonpastForm: String = "",
                           val verbalNounForm: String = "",
                           val pastFormId: Long,
                           val nonpastFormId: Long,
                           val verbalNounFormId: Long,
                           val posName: String = POS_VERB)

// Noun forms for Arabic
data class ArabicNounForms(val wordId: Long,
                           val translation: String,
                           val singularForm: String,
                           val pluralForm: String = "",
                           val singularFormId: Long,
                           val pluralFormId: Long,
                           val posName: String = POS_NOUN)


data class ArabicParticleForms(val wordId: Long,
                               val translation: String,
                               val particleForm: String,
                               val particleFormId: Long,
                               val posName: String = POS_PARTICLE)
