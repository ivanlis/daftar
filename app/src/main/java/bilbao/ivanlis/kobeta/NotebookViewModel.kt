package bilbao.ivanlis.kobeta

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import bilbao.ivanlis.kobeta.database.NotebookDb
import bilbao.ivanlis.kobeta.database.NotebookRepository

class NotebookViewModel(application: Application): AndroidViewModel(application) {
    private lateinit var repository: NotebookRepository

    init {
        val notebookDao = NotebookDb.getInstance(application).notebookDao()
        repository = NotebookRepository(notebookDao)
    }
}