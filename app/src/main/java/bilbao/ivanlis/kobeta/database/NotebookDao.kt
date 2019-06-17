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

    @Query("SELECT name FROM lesson WHERE id = :lessonId")
    fun getLessonName(lessonId: Long): LiveData<String>

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

    @Query("""
        INSERT INTO word_record (word_id, form_id, spelling)
        SELECT :wordId, f.id, :spelling
        FROM language AS l INNER JOIN part_of_speech AS pof
        INNER JOIN form AS f
        ON l.id = pof.language_id AND pof.id = f.part_of_speech_id
        WHERE l.english_name = :languageName AND f.english_name = :formName
    """)
    fun registerWordRecord(wordId: Long, languageName: String, formName: String, spelling: String): Long

    // score

    @Insert
    fun insertScore(score: Score): Long

    @Delete
    fun deleteScore(score: Score)

    @Update
    fun updateScore(score: Score)


    // query to extract the initial forms of every word belonging to a certain lesson
    @Query(
        """SELECT wr.word_id AS wordId, wr.spelling, w.translation, pos.english_name AS partOfSpeechName
            FROM
        word AS w INNER JOIN word_record AS wr INNER JOIN form AS f INNER JOIN part_of_speech AS pos
        ON w.id = wr.word_id AND wr.form_id = f.id AND f.part_of_speech_id = pos.id
        WHERE f.initial = 1 AND w.lesson_id = :lessonId
        """)
    fun extractInitialFormsForLesson(lessonId: Long): LiveData<List<WordInitialFormTranslation>>

    // query to extract all forms for an Arabic word
    //TODO: extract constant literals as constants
    @Query(
        """SELECT :wordId AS wordId, sel1.spelling AS pastForm, sel2.spelling AS nonpastForm,
            sel3.spelling AS verbalNounForm
                FROM
            (SELECT wr.spelling AS spelling FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="past") AS sel1
            LEFT JOIN
            (SELECT wr.spelling AS spelling FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="nonpast") AS sel2
            LEFT JOIN
            (SELECT wr.spelling AS spelling FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="verbalnoun") AS sel3
        """
    )
    fun extractArabicVerbForms(wordId: Long): LiveData<ArabicVerbForms>

    // query to extract all forms for an Arabic noun
    //TODO: extract constant literals as constants
    @Query(
        """SELECT :wordId AS wordId, sel1.spelling AS singularForm, sel2.spelling AS pluralForm
            FROM
            (SELECT wr.spelling AS spelling FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="singular") AS sel1
            LEFT JOIN
            (SELECT wr.spelling AS spelling FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="plural") AS sel2

        """
    )
    fun extractArabicNounForms(wordId: Long): LiveData<ArabicNounForms>

}