package bilbao.ivanlis.kobeta

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import bilbao.ivanlis.kobeta.database.Lesson
import bilbao.ivanlis.kobeta.database.NotebookDao
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.Word
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import timber.log.Timber
import java.io.IOException
import java.lang.Exception

///**
// * Instrumented test, which will execute on an Android device.
// *
// * See [testing documentation](http://d.android.com/tools/testing).
// */
//@RunWith(AndroidJUnit4::class)
//class ExampleInstrumentedTest {
//    @Test
//    fun useAppContext() {
//        // Context of the app under test.
//        val appContext = InstrumentationRegistry.getTargetContext()
//        assertEquals("bilbao.ivanlis.kobeta", appContext.packageName)
//    }
//}


@RunWith(AndroidJUnit4::class)
class NotebookDbTest {
    private lateinit var notebookDb: NotebookDb
    private lateinit var notebookDao: NotebookDao
    //private lateinit var notebookRepository: NotebookRepository

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        notebookDb = NotebookDb.getInstance(context, forTesting = true)
        notebookDao = notebookDb.notebookDao()
        //notebookRepository = NotebookRepository(notebookDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        notebookDb.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertLessonsAndTestView() {

        notebookDao.deleteAllLessons()
        //d("insertLessonsAndTestView", "All lessons deleted.")
        Timber.d("All lessons deleted.")

        val lessonName1 = "First lesson"
        val lessonName2 = "Урок второй"
        val lessonName3 = "الدرس الثالث"

        val lesson1id = notebookDao.insertLesson(Lesson(name = lessonName1))
        Thread.sleep(5)
        val lesson2id = notebookDao.insertLesson(Lesson(name = lessonName2))
        Thread.sleep(3)
        val lesson3id = notebookDao.insertLesson(Lesson(name = lessonName3))

        notebookDao.insertWord(Word(translation = "translation 1.1", lessonId = lesson1id))
        notebookDao.insertWord(Word(translation = "translation 1.2", lessonId = lesson1id))
        notebookDao.insertWord(Word(translation = "translation 1.3", lessonId = lesson1id))
        notebookDao.insertWord(Word(translation = "translation 3.1", lessonId = lesson3id))

        val viewList = notebookDao.getLessonItemsForListNotLive()

        Timber.d("In view results: ${viewList.size} elements.")
        assertEquals(viewList.size, 3)
        Timber.d("Name of lesson 0: ${viewList[0].name}, word count: ${viewList[0].wordCount}")
        assertEquals(viewList[0].name, lessonName3)
        assertEquals(viewList[0].wordCount, 1)
        Timber.d("Name of lesson 1: ${viewList[1].name}, word count: ${viewList[1].wordCount}")
        assertEquals(viewList[1].name, lessonName2)
        assertEquals(viewList[1].wordCount, 0)
        Timber.d("Name of lesson 2: ${viewList[2].name}, word count: ${viewList[2].wordCount}")
        assertEquals(viewList[2].name, lessonName1)
        assertEquals(viewList[2].wordCount, 3)
    }
}
