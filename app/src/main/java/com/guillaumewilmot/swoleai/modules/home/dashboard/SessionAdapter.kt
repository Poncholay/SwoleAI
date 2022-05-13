package com.guillaumewilmot.swoleai.modules.home.dashboard

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumewilmot.swoleai.databinding.AdapterViewSessionBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {
    var data: List<SessionViewHolder.ViewDataModel> = listOf()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val clickListenerSubject: PublishSubject<Int> = PublishSubject.create()

    fun getIndexClickedObservable(): Observable<Int> = clickListenerSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) = try {
        holder.apply {
            itemView.setOnClickListener {
                clickListenerSubject.onNext(position)
            }
        }.bind(data[position])
    } catch (ignored: IndexOutOfBoundsException) {
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SessionViewHolder(
        AdapterViewSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    class SessionViewHolder(
        private val binding: AdapterViewSessionBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionViewModel: ViewDataModel) {
            binding.activeLoader.visibility = if (sessionViewModel.isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.icon.visibility = if (sessionViewModel.iconId != null) {
                View.VISIBLE
            } else {
                View.GONE
            }
            if (sessionViewModel.iconId != null) {
                binding.icon.setImageResource(sessionViewModel.iconId)
            }
            binding.icon.setColorFilter(
                sessionViewModel.nameTextColor,
                PorterDuff.Mode.SRC_IN
            )
            binding.activeLoader.setIndicatorColor(sessionViewModel.nameTextColor)

            binding.nameText.text = sessionViewModel.nameText
            binding.nameText.setTextColor(sessionViewModel.nameTextColor)
        }

        data class ViewDataModel(
            val nameText: String,
            val nameTextColor: Int,
            val isLoading: Boolean,
            val iconId: Int?,
        )
    }
}