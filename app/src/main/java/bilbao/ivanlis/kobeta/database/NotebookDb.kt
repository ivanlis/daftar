package bilbao.ivanlis.kobeta.database

import android.content.Context
import android.util.Log.d
import androidx.annotation.VisibleForTesting
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Language::class,
        PartOfSpeech::class, Form::class, Lesson::class,
        Word::class, Score::class, WordRecord::class],
    views = [LessonItemForList::class],
    version = 2, exportSchema = false)
abstract class NotebookDb : RoomDatabase() {

    abstract fun notebookDao(): NotebookDao

    // make this a singleton
    companion object {
        @Volatile
        private var INSTANCE: NotebookDb? = null

        fun getInstance(context: Context, forTesting: Boolean = false): NotebookDb {

            d("NotebookDb.getInstance", "Accessing DB instance...")

            return INSTANCE ?: synchronized(this) {
                // create database
                d("NotebookDb.getInstance", "Calling buildDatabase()...")
                //val instance = buildDatabase(context)
                val instance = when(forTesting) {
                    false -> buildDatabase(context)
                    true -> buildDatabaseForTesting(context)
                }
                d("NotebookDb.getInstance", "Instance ready")
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

                            d("onOpen", "On database opening...")

                            super.onOpen(db)

                            //TODO: call prePopulateArabic() in an appropriate thread
                            val ioScope = CoroutineScope(Dispatchers.IO)
                            ioScope.launch {
                                prePopulateArabic(context)

                                //TODO: temporary
                                addFakeLessons(context)
                            }
                        }

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            d("onOpen", "On database creation...")
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
                d("prePopulateArabic", "There are registered languages. Not adding anything.")
            else {

                d("prePopulateArabic", "Inserting Arabic and everything related.")

                getInstance(context).notebookDao().registerLanguage("", "arabic")

                getInstance(context).notebookDao().registerPartOfSpeech(
                    "arabic",
                    "", "noun"
                )
                getInstance(context).notebookDao().registerPartOfSpeech(
                    "arabic",
                    "", "verb"
                )
                getInstance(context).notebookDao().registerPartOfSpeech(
                    "arabic",
                    "", "particle"
                )

                getInstance(context).notebookDao().registerForm(
                    "arabic", "noun",
                    "", "singular", true
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "noun",
                    "", "plural", false
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "verb",
                    "", "past", true
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "verb",
                    "", "nonpast", false
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "verb",
                    "", "verbalnoun", false
                )
                getInstance(context).notebookDao().registerForm(
                    "arabic", "particle",
                    "", "particle", true
                )
            }
        }

        private fun addFakeLessons(context: Context) {
            val currentLessonCount = getInstance(context).notebookDao().getLessonCount()
            if (currentLessonCount > 0)
                d("addFakeLessons", "We already have a lot of lessons, not creating anything")
            else {
                d("addFakeLessons", "Adding fake lessons...")
                val fakeLesson1 = Lesson(name = "Fake lesson 1.", creationDateTime = System.currentTimeMillis())
                var newId = getInstance(context).notebookDao().insertLesson(fakeLesson1)
                d("addFakeLessons", "Fake id = $newId")
                addFakeWords1(context, newId)

                val fakeLesson2 = Lesson(name = "Fake lesson 2.", creationDateTime = System.currentTimeMillis())
                newId = getInstance(context).notebookDao().insertLesson(fakeLesson2)
                d("addFakeLessons", "Fake id = $newId")
                addFakeWords2(context, newId)
            }
        }

        private fun addFakeWords1(context: Context, lessonId: Long) {

            var wordId = getInstance(context).notebookDao().insertWord(Word(translation = "делать", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "past", "فعل")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "nonpast", "يفعل")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "verbalnoun", "فعل")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "глаз", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "singular", "عين")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "plural", "عيون")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "в", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "particle", "في")
        }

        private fun addFakeWords2(context: Context, lessonId: Long) {

            var wordId = getInstance(context).notebookDao().insertWord(Word(translation = "просыпаться", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "past", "اِسْتَيْقَظَ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "nonpast", "يَسْتَيْقِظُ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "verbalnoun", "اِسْتِيقَاظ")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "на", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "particle", "على")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "читать", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "past", "قَرَأَ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "nonpast", "يَقْرَأُ")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "verbalnoun", "قِرَاءَة")

            wordId = getInstance(context).notebookDao().insertWord(Word(translation = "красный", lessonId = lessonId))
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "singular", "أَحْمَر")
            getInstance(context).notebookDao().registerWordRecord(wordId,
                "arabic", "plural", "حُمْر")

        }
    }

}