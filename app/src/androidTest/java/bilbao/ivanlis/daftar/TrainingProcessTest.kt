package bilbao.ivanlis.daftar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import bilbao.ivanlis.daftar.database.TrainingProcess
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@SmallTest
class TrainingProcessTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val trainingProcess = TrainingProcess.getInstance(context, "bilbao.ivanlis.daftar.training.test")

//    @Before
//    fun createTrainingProcess() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        trainingProcess = TrainingProcess.getInstance(context, "bilbao.ivanlis.daftar.training.test")
//    }
//
//    @After
//    fun cleanup() {
//
//    }

    @Test
    @Throws(Exception::class)
    fun assignLessonToStudy() {
        val valueToAssign = 19L
        trainingProcess.lessonToStudy = valueToAssign
        assertThat(trainingProcess.lessonToStudy == valueToAssign)
    }

    @Test
    @Throws(Exception::class)
    fun assignNumExercises() {
        val valueToAssign = 10L
        trainingProcess.numExercises = valueToAssign
        assertThat(trainingProcess.numExercises == valueToAssign)
    }
}