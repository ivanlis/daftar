package bilbao.ivanlis.kobeta.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface NotebookDao {

    // language

    @Query("SELECT * FROM language")
    fun getLanguages(): Array<Language>

    @Query("SELECT count(*) FROM language")
    fun getLanguageCount(): Long

    @Insert
    fun insertLanguage(lang: Language)

    @Query("INSERT INTO language (native_name, english_name) VALUES (:nativeName, :englishName)")
    fun registerLanguage(nativeName: String, englishName: String)

    @Delete
    fun deleteLanguage(lang: Language)


    // part_of_speech

    @Query("""INSERT INTO part_of_speech (language_id, native_name, english_name)
    SELECT lang.id, :posNativeName, :posEnglishName FROM language AS lang WHERE lang.english_name=:langEnglishName
    """)
    fun registerPartOfSpeech(langEnglishName: String, posNativeName: String, posEnglishName: String)

    // form

    @Query("""INSERT INTO form(part_of_speech_id, initial, native_name, english_name)
        SELECT pos.id, :initial, :formNativeName, :formEnglishName
        FROM part_of_speech AS pos INNER JOIN language AS lang ON pos.language_id=lang.id
        WHERE lang.english_name=:langEnglishName AND pos.english_name=:posEnglishName
    """)
    fun registerForm(langEnglishName: String, posEnglishName: String,
                   formNativeName: String, formEnglishName: String, initial: Boolean)


    // lesson
    @Query("SELECT * FROM lesson ORDER BY creation_date_time DESC")
    fun getAllLessons(): LiveData<List<Lesson>>

    @Query("SELECT * FROM LessonItemForList")
    fun getLessonItemsForList(): LiveData<List<LessonItemForList>>

    @Query("SELECT * FROM LessonItemForList")
    fun getLessonItemsForListNotLive(): List<LessonItemForList>

    @Query("SELECT count(*) FROM lesson")
    fun getLessonCount(): Long

    @Insert
    fun insertLesson(lesson: Lesson): Long

    @Delete
    fun deleteLesson(lesson: Lesson)

    @Query("DELETE FROM lesson")
    fun deleteAllLessons()

    @Update
    fun updateLesson(lesson: Lesson)

    // word

    @Insert
    fun insertWord(word: Word): Long

    @Delete
    fun deleteWord(word: Word)

    @Update
    fun updateWord(word: Word)

    // word_record

    @Insert
    fun insertWordRecord(wordRecord: WordRecord): Long

    @Delete
    fun deleteWordRecord(wordRecord: WordRecord)

    @Update
    fun updateWordRecord(wordRecord: WordRecord)

    // score

    @Insert
    fun insertScore(score: Score): Long

    @Delete
    fun deleteScore(score: Score)

    @Update
    fun updateScore(score: Score)


    //TODO: convert to view
    // query to extract the initial forms of every word belonging to a certain lesson
    @Query(
        """SELECT wr.spelling, w.translation FROM
        word AS w INNER JOIN word_record AS wr INNER JOIN form AS f
        ON w.id = wr.word_id AND wr.form_id = f.id
        WHERE f.initial = 1 AND w.lesson_id = :lessonId
        """)
    fun extractInitialFormsOfLesson(lessonId: Long): LiveData<List<WordTranslation>>



}