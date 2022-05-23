package com.guillaumewilmot.swoleai.controller

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView

abstract class ParentAdapter<T, VH : RecyclerView.ViewHolder?> : RecyclerView.Adapter<VH>() {

    protected val dataset: MutableList<T> = mutableListOf()

    fun setDatasetAnimated(dataset: List<T>) {
        applyAndAnimateRemovals(dataset)
        applyAndAnimateAdditions(dataset)
        applyAndAnimateMovedItems(dataset)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setDataset(dataset: List<T>) {
        this.dataset.clear()
        this.dataset.addAll(dataset)
        notifyDataSetChanged()
    }

    //Remove an item at position and notify changes.
    private fun removeItem(position: Int): T {
        return dataset.removeAt(position).also {
            notifyItemRemoved(position)
        }
    }

    //Add an item at position and notify changes.
    private fun addItem(position: Int, model: T) {
        dataset.add(position, model)
        notifyItemInserted(position)
    }

    //Move an item at fromPosition to toPosition and notify changes.
    private fun moveItem(fromPosition: Int, toPosition: Int) {
        val model: T = dataset.removeAt(fromPosition)
        dataset.add(toPosition, model)
        notifyItemMoved(fromPosition, toPosition)
    }

    //Remove items that no longer exist in the new models.
    private fun applyAndAnimateRemovals(newTs: List<T>) {
        for (i in dataset.indices.reversed()) {
            val model = dataset[i]
            if (!newTs.contains(model)) {
                removeItem(i)
            }
        }
    }

    //Add items that do not exist in the old models.
    private fun applyAndAnimateAdditions(newTs: List<T>) {
        newTs.forEachIndexed { i, model ->
            if (!dataset.contains(model)) {
                addItem(i, model)
            }
        }
    }

    //Move items that have changed their position.
    private fun applyAndAnimateMovedItems(newTs: List<T>) {
        for (toPosition in newTs.indices.reversed()) {
            val model = newTs[toPosition]
            val fromPosition = dataset.indexOf(model)
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition)
            }
        }
    }
}