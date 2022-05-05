package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.annotation.SuppressLint
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumewilmot.swoleai.databinding.AdapterViewExerciseSummaryBinding

class ExerciseSummaryAdapter :
    RecyclerView.Adapter<ExerciseSummaryAdapter.ExerciseSummaryViewHolder>() {
    var data: List<ExerciseSummaryViewHolder.ViewModel> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: ExerciseSummaryViewHolder, position: Int) = try {
        holder.bind(data[position])
    } catch (ignored: IndexOutOfBoundsException) {
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ExerciseSummaryViewHolder(
        AdapterViewExerciseSummaryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    class ExerciseSummaryViewHolder(
        private val binding: AdapterViewExerciseSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionViewModel: ViewModel) {
            binding.contentRoot.setOnClickListener {
//                callbackWrapper.wrap(sessionViewModel.onClickCallback::onClick)
            }

            binding.contentRoot.setBackgroundColor(sessionViewModel.backgroundColor)

            binding.infoButton.setOnClickListener {
//                callbackWrapper.wrap(sessionViewModel.onClickCallback::onClick)
            }

            binding.swapButton.setOnClickListener {
//                callbackWrapper.wrap(sessionViewModel.onClickCallback::onClick)
            }

            binding.nameText.text = sessionViewModel.nameText
        }

        data class ViewModel(
            val nameText: SpannableString,
            val backgroundColor: Int,
//            val onClickCallback: ParentActivity.AdapterCallback,
//            val infoCallback: ParentActivity.AdapterCallback,
//            val swapCallback: ParentActivity.AdapterCallback
        )
    }
}