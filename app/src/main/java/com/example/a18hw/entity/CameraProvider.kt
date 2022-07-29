package com.example.a18hw.entity

import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture

data class CameraProviderDto(
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    val cameraProvider: ProcessCameraProvider,
    val preview: Preview,
    val imageCapture: ImageCapture
)
