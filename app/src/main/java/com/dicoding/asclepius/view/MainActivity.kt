package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            startGallery()
        }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                moveToResult()
            } ?: run {
                showToast(getString(R.string.image_classifier_failed))
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showImage() {
        binding.previewImageView.setImageDrawable(null)
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        Log.d(TAG, "Initiating image classification")
        val intent = Intent(this, ResultActivity::class.java)
        currentImageUri?.let { uri ->
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            startActivityForResult(intent, REQUEST_IMAGE_CLASSIFICATION)
        } ?: run {
            showToast(getString(R.string.image_classifier_failed))
        }
    }

    private fun moveToResult() {
        Log.d(TAG, "Navigating to ResultActivity")
        val intent = Intent(this, ResultActivity::class.java)
        currentImageUri?.let { uri ->
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            startActivityForResult(intent, REQUEST_IMAGE_CLASSIFICATION)
        } ?: run {
            showToast(getString(R.string.image_classifier_failed))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "PhotoPicker"
        private const val REQUEST_IMAGE_CLASSIFICATION = 2001
    }
}
