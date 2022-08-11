package com.guillaumewilmot.swoleai.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * Calculates the spacing between each item
 * Supports Horizontal, Vertical and Grid layouts
 * @param spacing is the default spacing value to apply when necessary
 * @param spacingTop to override the default spacing for top
 * @param spacingBottom to override the default spacing for bottom
 * @param spacingRight to override the default spacing for right
 * @param spacingLeft to override the default spacing for left
 * @param forceTop to force apply the top spacing even if not necessary
 * @param forceBottom to force apply the bottom spacing even if not necessary
 * @param forceLeft to force apply the left spacing even if not necessary
 * @param forceRight to force apply the right spacing even if not necessary
 */
class EqualSpacingItemDecoration(private val spacing: Int) : RecyclerView.ItemDecoration() {
    var spacingTop: Int? = null
    var spacingBottom: Int? = null
    var spacingRight: Int? = null
    var spacingLeft: Int? = null
    var forceTop: Boolean = false
    var forceBottom: Boolean = false
    var forceRight: Boolean = false
    var forceLeft: Boolean = false

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager?,
        position: Int,
        itemCount: Int
    ) {
        when (resolveDisplayMode(layoutManager)) {
            HORIZONTAL -> {
                outRect.left = when {
                    forceLeft -> spacingLeft ?: spacing
                    position == 0 && spacingLeft != null -> spacingLeft ?: 0
                    position == 0 -> 0
                    else -> spacing
                }
                outRect.right = when {
                    forceRight -> spacingRight ?: spacing
                    position == itemCount - 1 && spacingRight != null -> spacingRight ?: 0
                    else -> 0
                }
                outRect.top = when {
                    forceTop -> spacingTop ?: spacing
                    spacingTop != null -> spacingTop ?: 0
                    else -> 0
                }
                outRect.bottom = when {
                    forceBottom -> spacingBottom ?: spacing
                    spacingBottom != null -> spacingBottom ?: 0
                    else -> 0
                }
            }
            VERTICAL -> {
                outRect.left = when {
                    forceLeft -> spacingLeft ?: spacing
                    spacingLeft != null -> spacingLeft ?: 0
                    else -> 0
                }
                outRect.right = when {
                    forceRight -> spacingRight ?: spacing
                    spacingRight != null -> spacingRight ?: 0
                    else -> 0
                }
                outRect.top = when {
                    forceTop -> spacingTop ?: spacing
                    position == 0 && spacingTop != null -> spacingTop ?: 0
                    position == 0 -> 0
                    else -> spacing
                }
                outRect.bottom = when {
                    forceBottom -> spacingBottom ?: spacing
                    position == itemCount - 1 && spacingBottom != null -> spacingBottom ?: 0
                    else -> 0
                }
            }
            GRID -> {
                outRect.left = when {
                    forceLeft -> spacingLeft ?: spacing
                    spacingLeft != null -> spacingLeft ?: 0
                    else -> spacing
                }
                outRect.right = when {
                    forceRight -> spacingRight ?: spacing
                    spacingRight != null -> spacingRight ?: 0
                    else -> spacing
                }
                outRect.top = when {
                    forceTop -> spacingTop ?: spacing
                    spacingTop != null -> spacingTop ?: 0
                    else -> spacing
                }
                outRect.bottom = when {
                    forceBottom -> spacingBottom ?: spacing
                    spacingBottom != null -> spacingBottom ?: 0
                    else -> spacing
                }
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager?): Int = when {
        layoutManager is GridLayoutManager || layoutManager is StaggeredGridLayoutManager -> GRID
        layoutManager?.canScrollHorizontally() == true -> HORIZONTAL
        else -> VERTICAL
    }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
        private const val GRID = 2
    }
}