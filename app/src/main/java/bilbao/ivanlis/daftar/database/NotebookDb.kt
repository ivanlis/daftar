package bilbao.ivanlis.daftar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import bilbao.ivanlis.daftar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Database(
    entities = [Language::class,
        PartOfSpeech::class, Form::class, Lesson::class,
        Word::class, Score::class, WordRecord::class],
    views = [LessonItemForList::class],
    version = 4, exportSchema = false)
abstract class NotebookDb : RoomDatabase() {

    abstract fun notebookDao(): NotebookDao

    // make this a singleton
    companion object {
        @Volatile
        private var INSTANCE: NotebookDb? = null

        fun getInstance(context: Context, forTesting: Boolean = false): NotebookDb {

            Timber.d("Accessing DB instance...")

            return INSTANCE ?: synchronized(this) {
                // create database
                Timber.d("Calling buildDatabase()...")
                //val instance = buildDatabase(context)
                val instance = when(forTesting) {
                    false -> buildDatabase(context)
                    true -> buildDatabaseForTesting(context)
                }
                Timber.d("Instance ready")
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                NotebookDb::class.java, "notebook_data")

                .addCallback(

                    object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {

                            Timber.d("On database opening...")

                            super.onOpen(db)

                            //TODO: call prePopulateArabic() in an appropriate thread
                            val ioScope = CoroutineScope(Dispatchers.IO)
                            ioScope.launch {
                                prePopulateArabic(context)

                                //TODO: temporary
                                if (BuildConfig.DEBUG)
                                    addFakeLessons(context)
                            }
                        }

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            Timber.d("On database creation...")
                            super.onCreate(db)
                        }
                    }

                )
                .fallbackToDestructiveMigration()

                .build()

        private fun buildDatabaseForTesting(context: Context) =
            Room.inMemoryDatabaseBuilder(context, NotebookDb::class.java)
                .allowMainThreadQueries()
                .build()

        //TODO: suspend needed?
        private fun prePopulateArabic(context: Context) {

            if (getInstance(context).notebookDao().getLanguageCount() > 0)
                Timber.d( "There are registered languages. Not adding anything.")
            else {

                Timber.d("Inserting Arabic and everything related.")

                getInstance(context).notebookDao().registerLanguage("", LANG_ARABIC)

                getInstance(context).notebookDao().registerPartOfSpeech(
                    LANG_ARABIC,
                    "", POS_NOUN
                )
                getInstance(context).notebookDao().registerPartOfSpeech(
                    LANG_ARABIC,
                    "", POS_VERB
                )
                getInstance(context).notebookDao().registerPartOfSpeech(
                    LANG_ARABIC,
                    "", POS_PARTICLE
                )

                getInstance(context).notebookDao().registerForm(
                    LANG_ARABIC, POS_NOUN,
                    "", FORM_SINGULAR, true
                )
                getInstance(context).notebookDao().registerForm(
                    LANG_ARABIC, POS_NOUN,
                    "", FORM_PLURAL, false
                )
                getInstance(context).notebookDao().registerForm(
                    LANG_ARABIC, POS_VERB,
                    "", FORM_PAST, true
                )
                getInstance(context).notebookDao().registerForm(
                    LANG_ARABIC, POS_VERB,
                    "", FORM_NONPAST, false
                )
                getInstance(context).notebookDao().registerForm(
                    LANG_ARABIC, POS_VERB,
                    "", FORM_VERBALNOUN, false
                )
                getInstance(context).notebookDao().registerForm(
                    LANG_ARABIC, POS_PARTICLE,
                    "", FORM_PARTICLE, true
                )
            }
        }

        private fun addFakeLessons(context: Context) {
            val currentLessonCount = getInstance(context).notebookDao().getLessonCount()
            if (currentLessonCount > 0)
                Timber.d("We already have a lot of lessons, not creating anything")
            else {
                Timber.d("Adding fake lessons...")
                val fakeLesson1 = Lesson(name = "Fake lesson 1.", creationDateTime = System.currentTimeMillis())
                var newId = getInstance(context).notebookDao().insertLesson(fakeLesson1)
                Timber.d("Fake id = $newId")
                addFakeWords1(context, newId)

                val fakeLesson2 = Lesson(name = "Fake lesson 2.", creationDateTime = System.currentTimeMillis())
                newId = getInstance(context).notebookDao().insertLesson(fakeLesson2)
                Timber.d("Fake id = $newId")
                addFakeWords2(context, newId)
            }
        }

        private fun addFakeWords1(context: Context, lessonId: Long) {

            var wordId = getInstance(context).notebookDao().insertWord(Word(translation = "делать", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PAST, "فعل")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_NONPAST, "يفعل")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_VERBALNOUN, "فعل")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "глаз", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_SINGULAR, "عين")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PLURAL, "عيون")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "в", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PARTICLE, "في")
        }

        private fun addFakeWords2(context: Context, lessonId: Long) {

            var wordId = getInstance(context).notebookDao().insertWord(Word(translation = "просыпаться", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PAST, "اِسْتَيْقَظَ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_NONPAST, "يَسْتَيْقِظُ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_VERBALNOUN, "اِسْتِيقَاظ")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "на", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PARTICLE, "على")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "читать", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PAST, "قَرَأَ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_NONPAST, "يَقْرَأُ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_VERBALNOUN, "قِرَاءَة")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "красный", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_SINGULAR, "أَحْمَر")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                LANG_ARABIC, FORM_PLURAL, "حُمْر")

        }
    }

}