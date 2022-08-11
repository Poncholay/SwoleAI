package com.guillaumewilmot.swoleai.modules.home.sessionsummary

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.guillaumewilmot.swoleai.controller.ParentAdapter
import com.guillaumewilmot.swoleai.databinding.AdapterViewExerciseSummaryBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class ExerciseSummaryAdapter : ParentAdapter<
        ExerciseSummaryAdapter.ViewDataModel,
        ExerciseSummaryAdapter.ExerciseSummaryViewHolder
        >() {

    private val clickListenerSubject: PublishSubject<Int> = PublishSubject.create()
    private val infoClickListenerSubject: PublishSubject<Int> = PublishSubject.create()
    private val swapClickListenerSubject: PublishSubject<Int> = PublishSubject.create()

    fun getIndexClickedObservable(): Observable<Int> = clickListenerSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getIndexInfoClickedObservable(): Observable<Int> = infoClickListenerSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun getIndexSwapClickedObservable(): Observable<Int> = swapClickListenerSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    override fun onBindViewHolder(holder: ExerciseSummaryViewHolder, position: Int) = try {
        holder.bind(dataset[position], position)
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

    inner class ExerciseSummaryViewHolder(
        private val binding: AdapterViewExerciseSummaryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(sessionViewModel: ViewDataModel, position: Int) {
            binding.contentRoot.setOnClickListener {
                clickListenerSubject.onNext(position)
            }

            binding.contentRoot.setBackgroundColor(sessionViewModel.backgroundColor)

            binding.infoButton.visibility = sessionViewModel.infoButtonVisibility
            binding.infoButton.setOnClickListener {
                infoClickListenerSubject.onNext(position)
            }

            binding.swapButton.visibility = sessionViewModel.swapButtonVisibility
            binding.swapButton.setOnClickListener {
                swapClickListenerSubject.onNext(position)
            }

            binding.nameText.text = sessionViewModel.nameText
        }
    }

    data class ViewDataModel(
        val nameText: SpannableString,
        val backgroundColor: Int,
        val infoButtonVisibility: Int,
        val swapButtonVisibility: Int
    )
}