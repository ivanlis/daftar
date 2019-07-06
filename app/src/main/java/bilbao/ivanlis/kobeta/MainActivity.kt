package bilbao.ivanlis.kobeta

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
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

    override fun onOptionsItemSelected(item: MenuItem?) = when(item.itemId) {
        R.id.action_to_lessons_list -> {
            //TODO: navigate to the lessons list fragment
            //supportFragmentManager.beginTransaction()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}
