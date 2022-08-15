package com.example.a18hw.presentation.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.a18hw.databinding.CameraFragmentBinding
import com.example.a18hw.entity.CameraProviderDto
import com.example.a18hw.entity.State
import com.example.a18hw.utils.LauncherUtils
import com.example.a18hw.utils.LauncherUtils.getLauncher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor


@AndroidEntryPoint
class CameraFragment : Fragment() {

    private var _binding: CameraFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: Executor

    private val launcher = getLauncher { viewModel.startCamera() }

    private val viewModel: CameraViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CameraFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        executor = ContextCompat.getMainExecutor(requireContext())
        viewModel.checkCameraFragmentPermissions()

        binding.takePhotoButton.setOnClickListener { viewModel.takePhoto(executor) }

        lifecycleScope.launchWhenCreated {
            this.launch {
                viewModel.permissions.collect {
                    when (it) {
                        State.PermissionsNotGranted -> launcher.launch(LauncherUtils.CameraFragmentPermissions.REQUIRED_PERMISSIONS)
                        State.AllPermissionsGranted -> viewModel.startCamera()
                    }
                }
            }
            this.launch {
                viewModel.cameraProviderDto.collect {
                    if (it != null)
                        bindCamera(it)
                }
            }
        }
    }

    private fun bindCamera(cameraProviderDto: CameraProviderDto) {
        cameraProviderDto.cameraProviderFuture.addListener({
            cameraProviderDto.preview.setSurfaceProvider(binding.preview.surfaceProvider)
            cameraProviderDto.cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                cameraProviderDto.preview,
                cameraProviderDto.imageCapture
            )
        }, executor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
