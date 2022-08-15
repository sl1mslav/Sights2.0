package com.example.a18hw.presentation.camera

import android.app.Application
import android.content.ContentValues
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a18hw.data.Repository
import com.example.a18hw.entity.CameraProviderDto
import com.example.a18hw.entity.State
import com.example.a18hw.utils.Constants.FILENAME_FORMAT
import com.example.a18hw.utils.LauncherUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    private var imageCapture: ImageCapture? = null

    private val _permissions = MutableStateFlow<State>(State.PermissionsNotGranted)
    val permissions = _permissions.asStateFlow()

    private val _cameraProviderDto = MutableStateFlow<CameraProviderDto?>(null)
    val cameraProviderDto = _cameraProviderDto.asStateFlow()

    private fun name() =
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

    private fun onTakePhoto(filepath: String, date: String) {
        viewModelScope.launch {
            repository.insertPhoto(filepath, date)
        }
    }

    fun checkCameraFragmentPermissions() {
        LauncherUtils.checkPermissions(
            app.applicationContext,
            LauncherUtils.CameraFragmentPermissions.REQUIRED_PERMISSIONS,
            onGranted = { _permissions.value = State.AllPermissionsGranted },
            onNotGranted = { _permissions.value = State.PermissionsNotGranted }
        )
    }

    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(app.applicationContext)
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build()
        imageCapture = ImageCapture.Builder().build()
        cameraProvider.unbindAll()
        _cameraProviderDto.value = CameraProviderDto(
            cameraProviderFuture,
            cameraProvider,
            preview,
            imageCapture!!
        )
    }

    fun takePhoto(executor: Executor) {

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            app.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture?.takePicture(
            outputOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onTakePhoto(outputFileResults.savedUri.toString(), name())
                    Toast.makeText(
                        app.applicationContext,
                        "Photo was saved at ${outputFileResults.savedUri}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        app.applicationContext,
                        "Error ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    exception.printStackTrace()
                }
            }
        )
    }


}

