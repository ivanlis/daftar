package bilbao.ivanlis.kobeta.database

import androidx.room.*

@Dao
interface NotebookDao {

    // language

    @Query("SELECT * FROM language")
    fun getLanguages(): Array<Language>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
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
}