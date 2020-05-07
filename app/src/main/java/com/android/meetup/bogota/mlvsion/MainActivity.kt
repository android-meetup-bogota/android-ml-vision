package com.android.meetup.bogota.mlvsion

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { dispatchTakePictureIntent() }
    }

    private val REQUEST_IMAGE_CAPTURE = 1

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photo.setImageBitmap(imageBitmap)
            findImageLabels(imageBitmap)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun findImageLabels(bitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler

        labeler.processImage(image)
            .addOnSuccessListener { labels ->
                val stringBuilder = StringBuilder()
                for (label in labels) {
                    stringBuilder.append(label.text).append("\n")
                }
                labelsText.text = stringBuilder.toString()
            }
            .addOnFailureListener { e ->
                Log.e("MainActivity", "Error getting labels", e)
            }
    }

}
