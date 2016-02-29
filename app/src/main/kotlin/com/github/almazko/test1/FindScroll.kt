package com.github.almazko.test1

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ScrollView

/**
 * Created by almaz on 22.02.16.
 */
class FindScroll {
    private var mLastScroll: ViewGroup? = null

    private fun _findParentScroll(target: ViewParent?): ViewGroup? {
        var target = target

        target = target!!.parent

        if (target == null) return mLastScroll

        if (target is ScrollView || target is RecyclerView) {

            if (mLastScroll != null) {
                Log.w("FindScroll", "Nested similar scroll detected, pick top ScrollView")
            }

            mLastScroll = target as ViewGroup?

            //если жесткто установлены параметры разметки, берем найденный
            if (mLastScroll!!.layoutParams.height > 0) {

                return mLastScroll
            }
        }

        return _findParentScroll(target)
    }

    internal fun findParentScroll(target: View): ViewGroup? {
        mLastScroll = null
        // todo add saving link
        return _findParentScroll(target) ?: return null

    }

    internal fun calcTopInScroll(view: View): Int {

        var value = 0
        var current: View? = view

        val parent = findParentScroll(view) ?: return 0

        while (current != null && parent !== current) {
            value += current.top
            current = current.parent as View
        }

        return value
    }

}