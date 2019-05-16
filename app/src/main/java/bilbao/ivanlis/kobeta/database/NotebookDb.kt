package bilbao.ivanlis.kobeta.database

import android.content.Context
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
    version = 1, exportSchema = false)
abstract class NotebookDb : RoomDatabase() {

    abstract fun notebookDao(): NotebookDao

    // make this a singleton
    companion object {
        @Volatile
        private var INSTANCE: NotebookDb? = null

        fun getInstance(context: Context): NotebookDb {
            return INSTANCE ?: synchronized(this) {
                // create database
                val instance = buildDatabase(context)
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                NotebookDb::class.java, "notebook_data")

                .addCallback(

                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            //TODO: call prePopulateArabic() in an appropriate thread
                            val ioScope = CoroutineScope(Dispatchers.IO)
                            ioScope.launch {
                                prePopulateArabic(context)
                            }
                        }
                    }

                )

                .build()

        //TODO: suspend needed?
        private suspend fun prePopulateArabic(context: Context) {
            getInstance(context).notebookDao().registerLanguage("", "arabic")

            getInstance(context).notebookDao().registerPartOfSpeech("arabic",
                "", "noun")
            getInstance(context).notebookDao().registerPartOfSpeech("arabic",
                "", "verb")
            getInstance(context).notebookDao().registerPartOfSpeech("arabic",
                "", "particle")
        }
    }

}