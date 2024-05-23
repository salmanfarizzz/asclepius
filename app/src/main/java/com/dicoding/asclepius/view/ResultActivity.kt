package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        Log.e(TAG, "Error: $error")
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { displayPredictionResult(it) }
                    }
                }
            )
            imageClassifierHelper.classifyImage(imageUri)
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayPredictionResult(results: List<Classifications>) {
        if (results.isNotEmpty()) {
            val topResult = results[0]
            val label = topResult.categories[0].label
            val score = topResult.categories[0].score

            fun Float.formatToString(): String {
                return String.format("%.2f%%", this * 100)
            }
            binding.resultText.text = "$label ${score.formatToString()}"
        } else {
            Log.e(TAG, "Empty result list")
        }
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Display Image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val TAG = "ImagePicker"
    }
}

