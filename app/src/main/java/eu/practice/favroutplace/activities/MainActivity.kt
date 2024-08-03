package eu.practice.favroutplace.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import eu.practice.favroutplace.R
import eu.practice.favroutplace.adapter.HappyPlaceAdapter
import eu.practice.favroutplace.database.DatabaseHandler
import eu.practice.favroutplace.models.HappyPlaceModel

class MainActivity : AppCompatActivity() {

    private var addBtn: FloatingActionButton? = null
    private var toolbar: Toolbar? = null
    private var rvHappyPlacesList: RecyclerView? = null
    private lateinit var noRecordsAvailable: TextView
    private lateinit var placesAdapter: HappyPlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addBtn = findViewById(R.id.fabAddHappyPlace)
        toolbar = findViewById(R.id.toolbar_happy_place)
        rvHappyPlacesList = findViewById(R.id.rv_happy_places_list)
        noRecordsAvailable = findViewById(R.id.no_records_availabe)

        setSupportActionBar(toolbar)

        addBtn!!.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }

        // Initialize the adapter with an empty list
        placesAdapter = HappyPlaceAdapter(this, ArrayList())
        rvHappyPlacesList!!.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList!!.adapter = placesAdapter

        getHappyPlaceListFromLocalDb()
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>) {
        Log.d("MainActivity", "Setting up RecyclerView with list size: ${happyPlaceList.size}")
        placesAdapter.setList(happyPlaceList)
        placesAdapter.notifyDataSetChanged()
    }

    private fun getHappyPlaceListFromLocalDb() {
        val dbHandler = DatabaseHandler(this)
        val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlaceList()
        Log.d("MainActivity", "HappyPlaceList size from DB: ${getHappyPlaceList.size}")

        if (getHappyPlaceList.size > 0) {
            rvHappyPlacesList!!.visibility = View.VISIBLE
            noRecordsAvailable.visibility = View.INVISIBLE
            setupHappyPlacesRecyclerView(getHappyPlaceList)
        } else {
            rvHappyPlacesList!!.visibility = View.GONE
            noRecordsAvailable.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                getHappyPlaceListFromLocalDb()
            } else {
                Log.e("MainActivity", "Cancelled or back pressed")
            }
        }
    }

    companion object {
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }
}
