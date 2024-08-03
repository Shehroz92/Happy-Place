package eu.practice.favroutplace.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import eu.practice.favroutplace.models.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val DATABASE_VERSION = 1
        private const val TABLE_HAPPY_PLACE = "HappyPlaceModel"
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "_title"
        private const val KEY_IMAGE = "_image"
        private const val KEY_DESCRIPTION = "_description"
        private const val KEY_DATE = "_date"
        private const val KEY_LOCATION = "_location"
        private const val KEY_LATITUDE = "_latitude"
        private const val KEY_LONGITUDE = "_longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_TITLE, happyPlace.title)
            put(KEY_IMAGE, happyPlace.image)
            put(KEY_DATE, happyPlace.date)
            put(KEY_DESCRIPTION, happyPlace.description)
            put(KEY_LOCATION, happyPlace.location)
            put(KEY_LATITUDE, happyPlace.latitude)
            put(KEY_LONGITUDE, happyPlace.longitude)
        }

        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
        db.close()

        Log.d("DatabaseHandler", "Added place with ID: ${happyPlace.id}, result: $result")
        return result
    }

    fun getHappyPlaceList(): ArrayList<HappyPlaceModel> {
        val happyPlaceList = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM $TABLE_HAPPY_PLACE"
        val db = this.readableDatabase

        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                    happyPlaceList.add(place)
                    Log.d("DatabaseHandler", "Retrieved place: $place")
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            Log.e("DatabaseHandler", "Error fetching data: ${e.message}")
            db.execSQL(selectQuery)
            return ArrayList()
        }

        Log.d("DatabaseHandler", "Final list size: ${happyPlaceList.size}")
        return happyPlaceList
    }
}