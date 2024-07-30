package eu.practice.favroutplace.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.practice.favroutplace.R

class MainActivity : AppCompatActivity() {

    private var addBtn:FloatingActionButton?=null
    private var toolbar :Toolbar?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addBtn = findViewById(R.id.fabAddHappyPlace)
        toolbar = findViewById(R.id.toolbar_happy_place)
        setSupportActionBar(toolbar)

        addBtn!!.setOnClickListener {
            val intent = Intent(this , AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
    }
}