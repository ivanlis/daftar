package bilbao.ivanlis.kobeta

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.notebook_toolbar))
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
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
