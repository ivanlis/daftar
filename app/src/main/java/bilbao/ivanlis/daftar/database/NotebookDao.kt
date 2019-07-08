package bilbao.ivanlis.daftar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import bilbao.ivanlis.daftar.*

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
    @Query("SELECT * FROM lesson WHERE id = :lessonId")
    fun getLesson(lessonId: Long): LiveData<Lesson>

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

    @Query("DELETE FROM lesson WHERE id = :lessonId")
    fun deleteLessonById(lessonId: Long)

    @Query("DELETE FROM lesson")
    fun deleteAllLessons()

    @Update
    fun updateLesson(lesson: Lesson)

    // word

    @Insert
    fun insertWord(word: Word): Long

    @Delete
    fun deleteWord(word: Word)

    @Query("DELETE FROM word WHERE id = :wordId")
    fun deleteWordById(wordId: Long)

    @Update
    fun updateWord(word: Word)

    @Query(
        """
            UPDATE word
            SET translation = :translation
            WHERE id = :wordId
        """
    )
    fun updateWordById(wordId: Long, translation: String)

    // word_record

    @Insert
    fun insertWordRecord(wordRecord: WordRecord): Long

    @Delete
    fun deleteWordRecord(wordRecord: WordRecord)

    @Query(
        """
            UPDATE word_record
            SET spelling = :spelling
            WHERE word_id = :wordId AND form_id = :formId
        """
    )
    fun updateWordRecordByWordAndForm(wordId: Long, formId: Long, spelling: String)

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
        """SELECT :wordId AS wordId, sel0.translation AS translation,
            sel1.spelling AS pastForm, sel2.spelling AS nonpastForm,
            sel3.spelling AS verbalNounForm,
            sel1.form_id AS pastFormId, sel2.form_id AS nonpastFormId,
            sel3.form_id AS verbalNounFormId
                FROM
            (SELECT w.translation AS translation FROM word AS w WHERE w.id = :wordId) AS sel0
            LEFT JOIN
            (SELECT wr.spelling AS spelling, wr.form_id AS form_id FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="$FORM_PAST") AS sel1
            LEFT JOIN
            (SELECT wr.spelling AS spelling, wr.form_id AS form_id FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="$FORM_NONPAST") AS sel2
            LEFT JOIN
            (SELECT wr.spelling AS spelling, wr.form_id AS form_id FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="$FORM_VERBALNOUN") AS sel3
        """
    )
    fun extractArabicVerbForms(wordId: Long): LiveData<ArabicVerbForms>

    // query to extract all forms for an Arabic noun
    //TODO: extract constant literals as constants
    @Query(
        """SELECT :wordId AS wordId, sel0.translation AS translation,
            sel1.spelling AS singularForm, sel2.spelling AS pluralForm,
            sel1.form_id AS singularFormId, sel2.form_id AS pluralFormId
                FROM
            (SELECT w.translation AS translation FROM word AS w WHERE w.id = :wordId) AS sel0
            LEFT JOIN
            (SELECT wr.spelling AS spelling, wr.form_id AS form_id FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="$FORM_SINGULAR") AS sel1
            LEFT JOIN
            (SELECT wr.spelling AS spelling, wr.form_id AS form_id FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="$FORM_PLURAL") AS sel2

        """
    )
    fun extractArabicNounForms(wordId: Long): LiveData<ArabicNounForms>

    // query to extract the spelling of an Arabic particle
    //TODO: extract constant literals as constants
    @Query(
        """SELECT :wordId AS wordId, sel0.translation AS translation,
            sel1.spelling AS particleForm, sel1.form_id AS particleFormId
            FROM
            (SELECT w.translation AS translation FROM word AS w WHERE w.id = :wordId) AS sel0
            LEFT JOIN
            (SELECT wr.spelling AS spelling, wr.form_id AS form_id FROM word_record AS wr INNER JOIN form AS f
                ON wr.form_id = f.id AND wr.word_id = :wordId AND f.english_name="$FORM_PARTICLE") AS sel1
        """
    )
    fun extractArabicParticleForms(wordId: Long): LiveData<ArabicParticleForms>

}