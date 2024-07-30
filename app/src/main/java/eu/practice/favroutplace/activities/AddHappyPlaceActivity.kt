package eu.practice.favroutplace.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import eu.practice.favroutplace.R
import eu.practice.favroutplace.database.DatabaseHandler
import eu.practice.favroutplace.databinding.ActivityAddHappyPlaceBinding
import eu.practice.favroutplace.models.HappyPlaceModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddHappyPlaceBinding

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage : Uri ? = null
    private var mLatitude  :Double  = 0.0
    private var mLongitude :Double  = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarAddHappyPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarAddHappyPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
            updateDateInView()

        binding.etDate.setOnClickListener(this)
        binding.tvAddImage.setOnClickListener(this) // Ensure the click listener is set for the TextView
        binding.ivPlaceImage.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)
        binding.etTitle.setOnClickListener(this)
        binding.etDescription.setOnClickListener(this)
        binding.etLocation.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                        ).show()
                       }
            R.id.tv_add_image -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf(
                    "Select photo from gallery",
                    "Capture photo from camera"
                )
                pictureDialog.setItems(pictureDialogItems) { _, which ->
                    when (which) {
                        0 -> { choosePhotoFromGallery() }
                        1 -> { takePhotoFromCamera() }
                     }
                 }
                    pictureDialog.show()
                }
            R.id.btn_save -> {
                when {
                    binding.etTitle.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter title ", Toast.LENGTH_LONG).show()
                    }

                    binding.etDescription.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter Description", Toast.LENGTH_LONG).show()
                    }

                    binding.etLocation.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Please Enter Location", Toast.LENGTH_LONG).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Please Select the Image", Toast.LENGTH_LONG).show()
                    }

                    else -> {
                            val happyPlaceModel = HappyPlaceModel(
                                0,
                                binding.etTitle.text.toString(),
                                binding.ivPlaceImage.toString(),
                                binding.etDescription.text.toString(),
                                binding.etDate.text.toString(),
                                binding.etLocation.text.toString(),
                                mLatitude,
                                mLongitude
                            )
                        val dbHandler = DatabaseHandler(this)
                        val addHappyPlace = dbHandler.addHappyPLace(happyPlaceModel)
                        if (addHappyPlace > 0){
                            Toast.makeText(this, "The Happy Place details are inserted successfully", Toast.LENGTH_SHORT).show()
                            finish()

                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode ==  Activity.RESULT_OK ){
            if (requestCode ==  GALLERY){
                if (data != null){
                    val contentUri = data.data
                    try {
                        val selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver,contentUri)
                         saveImageToInternalStorage = saveImageToInternalStorage(selectedImage)

                        binding.ivPlaceImage.setImageBitmap(selectedImage)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load the image from the gallery", Toast.LENGTH_LONG).show()
                    }
                }
            } else if( requestCode == CAMERA_REQUEST_CODE ){

                val thumbNail : Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbNail)

                binding.ivPlaceImage.setImageBitmap(thumbNail)
            }
        }
    }



    private fun takePhotoFromCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }
    private fun choosePhotoFromGallery() {

        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                        val galleryIntent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI  )
                        startActivityForResult(galleryIntent , GALLERY)


                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }
            }).onSameThread()
            .check()

    }


    private fun showRationalDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage(
                "It looks like you have turned off the permission required for this feature. " +
                        "It can be enabled under the Application settings"
            )
            .setPositiveButton("Go To Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding.etDate.setText(sdf.format(cal.time))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }else{
                    showRationalDialogForPermission()

            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val contextWrapper = ContextWrapper(applicationContext)
        var file = contextWrapper.getDir(IMAGE_DIRECTORY , Context.MODE_PRIVATE )
        file = File(file,"${UUID.randomUUID()}.jpg")

        try {
            val stream :OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG , 100 ,stream)
            stream.flush()
            stream.close()


        }catch (e:IOException){
            e.printStackTrace()
        }

       return Uri.parse(file.absolutePath)
    }

    companion object{
        private const val GALLERY = 1
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val IMAGE_DIRECTORY = "HAPPY PLACE IMAGES"

    }

}
