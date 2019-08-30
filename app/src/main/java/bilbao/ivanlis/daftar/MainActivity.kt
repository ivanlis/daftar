package bilbao.ivanlis.daftar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import bilbao.ivanlis.daftar.database.DataExporter
import bilbao.ivanlis.daftar.database.NotebookDb
import bilbao.ivanlis.daftar.database.NotebookRepository
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.notebook_toolbar))
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // up button
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfig = AppBarConfiguration(navController.graph)
        findViewById<Toolbar>(R.id.notebook_toolbar).setupWithNavController(navController, appBarConfig)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notebook_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when(item!!.itemId) {
        R.id.action_to_lessons_list -> {
            val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
            navController.navigate(R.id.lessonsListFragment)

            true
        }
        R.id.action_export_training -> {
            val exporter = DataExporter(application,
                NotebookRepository(NotebookDb.getInstance(application).notebookDao()))
            try {
                exporter.exportTraining()
                Toast.makeText(application, R.string.saved_exclamation, Toast.LENGTH_LONG).show()
                true
            }
            catch (exc: Exception) {
                Toast.makeText(application, R.string.error_saving_scores, Toast.LENGTH_LONG).show()
                false
            }
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    fun setActionBarTitle(titleResId: Int) {
        supportActionBar?.setTitle(titleResId)
    }
}
