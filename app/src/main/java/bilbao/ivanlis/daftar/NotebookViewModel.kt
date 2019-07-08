package bilbao.ivanlis.daftar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository

class NotebookViewModel(application: Application): AndroidViewModel(application) {
    private var repository: NotebookRepository

    init {
        val notebookDao = NotebookDb.getInstance(application).notebookDao()
        repository = NotebookRepository(notebookDao)
    }
}