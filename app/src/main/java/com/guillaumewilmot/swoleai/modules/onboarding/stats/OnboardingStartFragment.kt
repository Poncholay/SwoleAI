package com.guillaumewilmot.swoleai.modules.onboarding.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import autodispose2.androidx.lifecycle.autoDispose
import com.guillaumewilmot.swoleai.R
import com.guillaumewilmot.swoleai.controller.ParentFragment
import com.guillaumewilmot.swoleai.databinding.FragmentOnboardingStatsBinding
import com.guillaumewilmot.swoleai.modules.onboarding.AttachViewPagerIndicator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnboardingStatsFragment : ParentFragment<FragmentOnboardingStatsBinding>() {

    private val viewModel: OnboardingStatsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentOnboardingStatsBinding.inflate(
        inflater,
        container,
        false
    ).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui()
    }

    override fun onResume() {
        super.onResume()

        viewModel.loaderVisibility
            .autoDispose(this)
            .subscribe {
                binding?.loader?.visibility = it
            }

        viewModel.isMale
            .autoDispose(this)
            .subscribe {
                activity?.let { context ->
                    Log.e("ISMALE", it.toString())
                    when (it) {
                        true -> {
                            binding?.maleCard?.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.backgroundCardViewSelected
                                )
                            )
                            binding?.femaleCard?.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.backgroundCardView
                                )
                            )
                        }
                        false -> {
                            binding?.maleCard?.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.backgroundCardView
                                )
                            )
                            binding?.femaleCard?.setCardBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.backgroundCardViewSelected
                                )
                            )
                        }
                    }
                }
            }

        viewModel.height
            .autoDispose(this)
            .subscribe {
                Log.e("HEIGHT", it.toString())
                binding?.heightSeekbar?.progress = it
            }

        viewModel.weight
            .autoDispose(this)
            .subscribe {
                Log.e("WEIGHT", it.toString())
                binding?.heightSeekbar?.progress = it
            }
    }

    private fun ui() {
        binding?.viewPagerDots?.let {
            parent?.attachIndicator(it)
        }

        binding?.titleText?.text = viewModel.titleText
        binding?.continueButton?.text = viewModel.nextButtonText

        binding?.continueButton?.setOnClickListener {
            viewModel.onNext()
                ?.autoDispose(this, Lifecycle.Event.ON_STOP)
                ?.subscribe {
                    parent?.userOnboardingStatsNext()
                }
        }

        binding?.maleCard?.setOnClickListener {
            viewModel.setSex(isMale = true)
        }
        binding?.femaleCard?.setOnClickListener {
            viewModel.setSex(isMale = false)
        }

        binding?.heightSeekbar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onHeightChanged(progress)
            }
        })
        binding?.weightSeekbar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.onWeightChanged(progress)
            }
        })
    }

    private val parent get() = (activity as? OnboardingStatsFragmentListener)

    interface OnboardingStatsFragmentListener : AttachViewPagerIndicator {
        fun userOnboardingStatsNext()
    }
}