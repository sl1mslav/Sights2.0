package com.example.a18hw.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.a18hw.R
import com.example.a18hw.databinding.MainFragmentBinding
import com.example.a18hw.presentation.PhotoAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private val photoAdapter = PhotoAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonToCameraFragment.setOnClickListener {
            val action = MainFragmentDirections.mainFragmentToCameraFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.clearDataButton.setOnClickListener { viewModel.onClearDataButton() }

        binding.photosRecyclerView.adapter = photoAdapter

        binding.buttonToMap.setOnClickListener {
            findNavController().navigate(R.id.action_main_fragment_to_maps_fragment)
        }

        lifecycleScope.launchWhenCreated {
            viewModel.allPhotos.collect {
                photoAdapter.submitList(it.reversed())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}