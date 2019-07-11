package bilbao.ivanlis.daftar

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import bilbao.ivanlis.daftar.database.TrainingProcess
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class TrainingProcessTest {

    private lateinit var trainingProcess: TrainingProcess

    @Before
    fun createTrainingProcess() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        trainingProcess = TrainingProcess.getInstance(context, "bilbao.ivanlis.daftar.training.test")
    }

    @After
    fun cleanup() {

    }

    @Test
    @Throws(Exception::class)
    fun assignLessonToStudy() {
        val valueToAssign = 19L
        trainingProcess.lessonToStudy = valueToAssign
        Assert.assertEquals(trainingProcess.lessonToStudy, valueToAssign)
    }

    @Test
    @Throws(Exception::class)
    fun assignNumExercises() {
        val valueToAssign = 10L
        trainingProcess.numExercises = valueToAssign
        Assert.assertEquals(trainingProcess.numExercises, valueToAssign)
    }
}