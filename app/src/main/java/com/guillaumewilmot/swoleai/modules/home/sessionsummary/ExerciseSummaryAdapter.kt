package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumewilmot.swoleai.controller.ParentAdapter
import com.guillaumewilmot.swoleai.databinding.AdapterViewExerciseSummaryBinding

class ExerciseSummaryAdapter : ParentAdapter<
        ExerciseSummaryAdapter.ExerciseSummaryViewHolder.ViewDataModel,
        ExerciseSummaryAdapter.ExerciseSummaryViewHolder
        >() {

    override fun onBindViewHolder(holder: ExerciseSummaryViewHolder, position: Int) = try {
        holder.bind(dataset[position])
    } catch (ignored: IndexOutOfBoundsException) {
    }

    override fun getItemCount(): Int = dataset.size

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
        fun bind(sessionViewModel: ViewDataModel) {
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

        data class ViewDataModel(
            val nameText: SpannableString,
            val backgroundColor: Int,
            //TODO : Use observable
//            val onClickCallback: ParentActivity.AdapterCallback,
//            val infoCallback: ParentActivity.AdapterCallback,
//            val swapCallback: ParentActivity.AdapterCallback
        )
    }
}