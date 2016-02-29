package com.github.almazko.test1

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import kotlin.properties.Delegates

/**
 * Created by almaz on 21.02.16.
 */
class DDLayout2 : View.OnClickListener {
    val header: View
    val contentWrapper: ViewGroup
    val wrapperLp: LinearLayout.LayoutParams
    val lp: ViewGroup.MarginLayoutParams?

    private val view: DDLayout
    private var duration: Long = 0


    constructor(header: View,
                contentWrapper: ViewGroup,
                wrapperLp: LinearLayout.LayoutParams,
                view: DDLayout,
                duration: Long) {

        this.header = header
        this.contentWrapper = contentWrapper
        this.wrapperLp = wrapperLp
        this.view = view
        this.duration = duration
        this.lp = view.getLayoutParams() as ViewGroup.MarginLayoutParams
        this.vto = view.getViewTreeObserver()
        header.setOnClickListener(this)
    }


    internal val TAG = "ExpandableLayout"

    private val findScroll = FindScroll()

    private var mExpanded: Boolean = false
    //    private var mAnimator: ValueAnimator? = null


    private var contentListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var vto: ViewTreeObserver
    private var mOnExpandListener: OnExpandListener? = null


    interface OnExpandListener {
        fun onExpand(expandableLayout: DDLayout, contentWrapper: ViewGroup)
    }


    inner class Expander(var contentHeight: Int = 0) : ValueAnimator.AnimatorUpdateListener {

        override fun onAnimationUpdate(animation: ValueAnimator) {
            val value = animation.animatedValue as Float

            contentWrapper.setAlpha(value)
            wrapperLp.height = (value * contentHeight).toInt()
            contentWrapper.requestLayout()
            //            if (icoExpand != null) icoExpand!!.rotation = (-value * 180).toInt().toFloat()
        }
    }

    inner class Collapser(var contentHeight: Int = 0) : ValueAnimator.AnimatorUpdateListener {

        override fun onAnimationUpdate(animation: ValueAnimator) {
            val value = animation.animatedValue as Float
            contentWrapper.setAlpha(value)
            wrapperLp.height = (value * contentHeight).toInt()
            //            requestLayout()
        }
    }


    override fun onClick(v: View) {

        contentWrapper.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.AT_MOST), View.MeasureSpec.UNSPECIFIED)
        val measuredContentHeight = contentWrapper.getMeasuredHeight()

        //            Log.v(this@DDLayout.toString(), "ContentOnClickListener: content size: " + measuredContentHeight)

        val contentHeight = measuredContentHeight

        if (contentHeight == 0) {
            //                Log.v(this@DDLayout.toString(), "ContentOnClickListener has empty content")
            // TODO add simple behavior
        }

        val scroll = findScroll.findParentScroll()

        if (scroll == null) {
            //                Log.d(this@DDLayout.toString(), "Not found ScrollView's parent!")
        } else {

            val scrollY = scroll.getScrollY()
            val marginTop = if (lp != null) lp.topMargin else 0
            val marginBottom = if (lp != null) lp.bottomMargin else 0


            val top = findScroll.calcTopInScroll(this@DDLayout) - scrollY - marginTop
            val heightBottom = top + header.getHeight() + marginTop + marginBottom
            val scrollHeight = scroll.getHeight()

            Log.v(TAG, "ScrollView height=$scrollHeight, scrollY=$scrollY. Top position in ScrollView=$top")
            setChangeLayoutListener(scroll, top, heightBottom, scrollHeight)
        }



        bil.setHeight(contentHeight)
        bil.anim(contentHeight)
    }

    enum class State {
        EXPANDED, COLLAPSED, EXPANDING, COLLAPSING
    }

    val bil: AnimatorBuilder  by lazy { AnimatorBuilder(duration, mExpanded) }

    inner class AnimatorBuilder {
        private var expander: Expander? = null
        private var collapser: Collapser? = null
        private var duration: Long
        private var state: State


        val expandFinalizator = AnimatorCallback.get({
            wrapperLp.height = LinearLayout.LayoutParams.WRAP_CONTENT
            state = State.EXPANDED

        });

        val collapseFinalizator = AnimatorCallback.get({
            state = State.COLLAPSED

        });

        private var animSet: AnimatorSet? = null
        private var animator: ValueAnimator

        constructor(duration: Long, expanded: Boolean) {

            this.duration = duration
            //            collapser = Collapser(height)
            //            expander = Expander(height)

            this.state = if (expanded) State.EXPANDED else State.COLLAPSED

            this.duration = duration
            /*            //            if (mExpanded) {
                        //
                        //                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        //                    vto.removeGlobalOnLayoutListener(contentListener)
                        //                } else {
                        //                    vto.removeOnGlobalLayoutListener(contentListener)
                        //                }
                        //
                        //            } else {
                        //
                        //
                        //                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        //                    vto.removeGlobalOnLayoutListener(contentListener)
                        //                } else {
                        //                    vto.removeOnGlobalLayoutListener(contentListener)
                        //                }
                        //
                        //            }*/


            animator = ValueAnimator.ofFloat(1f, 0f)
            //            mAnimator.addUpdateListener(collapsing)
            //            mAnimator.setDuration(duration.toLong())
        }


        fun anim(height: Int) {

            setHeight(height)
            when (state) {
                State.COLLAPSED -> expand()
                State.EXPANDED -> collapse()
                State.EXPANDING -> interruptExpand()
                State.COLLAPSING -> interruptCollapsing()
            }

        }

        fun expand() {

            animator = ValueAnimator.ofFloat(0f, 1f)
            animator.addUpdateListener(expander)
            animator.setDuration(duration)

            AnimatorSet().play(animator)

            state = State.EXPANDING
        }

        fun collapse() {

            animator = ValueAnimator.ofFloat(1f, 0f)
            animator.addUpdateListener(collapser)
            animator.setDuration(duration)

            AnimatorSet().play(animator)

            state = State.COLLAPSING
        }

        fun interruptExpand() {
            stop()

            val value = animator.getAnimatedValue() as Float
            animator = ValueAnimator.ofFloat(value, 0f)
            animator.addUpdateListener(collapser)
            animator.setDuration((duration * value).toLong())

            AnimatorSet().play(animator).before(expandFinalizator)
            state = State.COLLAPSING
        }

        fun interruptCollapsing() {
            stop()

            val value = animator.getAnimatedValue() as Float
            animator = ValueAnimator.ofFloat(value, 1f)
            animator.addUpdateListener(expander)
            animator.setDuration((duration * (1 - value)).toLong())

            AnimatorSet().play(animator).before(expandFinalizator)

            state = State.EXPANDING
        }

        fun stop() {
            animator.cancel()
        }

        fun setHeight(height: Int) {
            expander?.contentHeight = height
            collapser?.contentHeight = height
        }


    }

    /* private fun starAnimate(collapsing: ValueAnimator.AnimatorUpdateListener, expanding: ValueAnimator.AnimatorUpdateListener) {
         val animSet = AnimatorSet()






         // start expanding
         //interrupt expanding

         // start colapsing
         //interrupr colapsing
         if (mExpanded) {

             removeLayoutListener()

             // start collapse
             mExpanded = !mExpanded


             if (mAnimator != null && mAnimator.isRunning()) {

                 (mAnimator as ValueAnimator).cancel()
                 val value = mAnimator.getAnimatedValue() as Float
                 mAnimator = ValueAnimator.ofFloat(value, 0)
                 mAnimator.addUpdateListener(collapsing)
                 mAnimator.setDuration((mAnimDuration * value).toLong())

             } else {
                 mAnimator = ValueAnimator.ofFloat(1, 0)
                 mAnimator.addUpdateListener(collapsing)
                 mAnimator.setDuration(mAnimDuration.toLong())
             }

             animSet.play(mAnimator)
         } else {

             // start expand
             mExpanded = !mExpanded
             if (mAnimator != null && mAnimator.isRunning()) {
                 mAnimator.cancel()
                 val value = mAnimator.getAnimatedValue() as Float
                 mAnimator = ValueAnimator.ofFloat(value, 1)
                 mAnimator.addUpdateListener(expanding)
                 mAnimator.setDuration((mAnimDuration * (1 - value)).toInt().toLong())

             } else {
                 mAnimator = ValueAnimator.ofFloat(0, 1)
                 mAnimator.addUpdateListener(expanding)
                 mAnimator.setDuration(mAnimDuration.toLong())
             }

             val finalizator = AnimatorCallback.get({


                 wrapperLp.height = LinearLayout.LayoutParams.WRAP_CONTENT

             });

             animSet.play(mAnimator).before(finalizator)
         }

         //if (mOnExpandListener != null) mOnExpandListener.onExpand(this, mExpanded)

         animSet.start()
     }
 */

    private fun removeLayoutListener() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            vto.removeGlobalOnLayoutListener(contentListener)
        } else {
            vto.removeOnGlobalLayoutListener(contentListener)
        }

    }

    //fixme move to class
    private fun setChangeLayoutListener(scroll: View, top: Int, heightBottom: Int, scrollHeight: Int) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            vto?.removeGlobalOnLayoutListener(contentListener)
        } else {
            vto?.removeOnGlobalLayoutListener(contentListener)
        }

        contentListener = object : ViewTreeObserver.OnGlobalLayoutListener {

            internal var isFit = false
            internal var exceeded = 0
            internal var prevExceeded = 0

            override fun onGlobalLayout() {
                exceeded = wrapperLp?.height + heightBottom - scrollHeight

                if (exceeded > 0) {
                    if (top > exceeded) {
                        scroll.scrollBy(0, exceeded - prevExceeded)
                        prevExceeded = exceeded

                    } else if (!isFit) {
                        isFit = true
                        scroll.scrollBy(0, top - prevExceeded)
                        prevExceeded = top
                    }
                }
            }
        }

        vto.addOnGlobalLayoutListener(contentListener)
    }
}