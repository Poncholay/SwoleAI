package com.guillaumewilmot.swoleai.modules.home.session

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumewilmot.swoleai.controller.ParentActivity
import com.guillaumewilmot.swoleai.databinding.AdapterViewSessionBinding

class SessionAdapter(
    private val callbackWrapper: ParentActivity.AdapterCallbackWrapper
) : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {
    var data: List<SessionViewHolder.ViewModel> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) = try {
        holder.bind(data[position])
    } catch (ignored: IndexOutOfBoundsException) {
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SessionViewHolder(
        AdapterViewSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        callbackWrapper
    )

    class SessionViewHolder(
        private val binding: AdapterViewSessionBinding,
        private val callbackWrapper: ParentActivity.AdapterCallbackWrapper
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionViewModel: ViewModel) {
            itemView.setOnClickListener {
                callbackWrapper.wrap(sessionViewModel.callback::onClick)
            }

            binding.isCompleteIcon.setColorFilter(
                sessionViewModel.nameTextColor,
                PorterDuff.Mode.SRC_IN
            )
            binding.isCompleteIcon.visibility = sessionViewModel.isCompleteIconVisibility
            binding.nameText.text = sessionViewModel.nameText
            binding.nameText.setTextColor(sessionViewModel.nameTextColor)
        }

        data class ViewModel(
            val nameText: String,
            val nameTextColor: Int,
            val isCompleteIconVisibility: Int,
            val callback: ParentActivity.AdapterCallback
        )
    }
}