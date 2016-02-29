//package com.github.almazko.test1
//
//import android.animation.AnimatorSet
//import android.animation.ValueAnimator
//import android.os.Build
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import android.view.ViewTreeObserver
//import android.widget.LinearLayout
//
///**
// * Created by almaz on 22.02.16.
// */
//class ContentOnClickListener(
//        var mContentWrapper: ViewGroup)
//: View.OnClickListener {
//    private var mAnimator: ValueAnimator? = null
//    private var mExpanded: Boolean = false
//    private var mAnimDuration: Int = 0
//
//    override fun onClick(v: View) {
//
//        mContentWrapper.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST), View.MeasureSpec.UNSPECIFIED)
//        val measuredContentHeight = mContentWrapper.getMeasuredHeight()
//
////        Log.v(this@DDLayout.toString(), "ContentOnClickListener: content size: " + measuredContentHeight)
//
//        val contentHeight = measuredContentHeight
//
//        if (contentHeight == 0) {
////            Log.v(this@DDLayout.toString(), "ContentOnClickListener has empty content")
//            // TODO add simple behavior
//        }
//
//        val scroll = findScroll.findParentScroll()
//
//        if (scroll == null) {
//            Log.d(this@DDLayout.toString(), "Not found ScrollView's parent!")
//        } else {
//
//            val lp = layoutParams as ViewGroup.MarginLayoutParams
//            val scrollY = scroll!!.getScrollY()
//            val marginTop = if (lp != null) lp.topMargin else 0
//            val marginBottom = if (lp != null) lp.bottomMargin else 0
//
//
//            val top = findScroll.calcTopInScroll(this@DDLayout) - scrollY - marginTop
//            val heightBottom = top + mHeader.getHeight() + marginTop + marginBottom
//            val scrollHeight = scroll!!.getHeight()
//
//            Log.v(TAG, "ScrollView height=$scrollHeight, scrollY=$scrollY. Top position in ScrollView=$top")
//            setChangeLayoutListener(scroll, top, heightBottom, scrollHeight)
//        }
//
//        //            val icoExpand = mHeader.findViewById(R.id.ico_expand)
//        //            mExpander.setInitial(icoExpand, contentHeight)
//        //            mCollapser.setInitial(icoExpand, contentHeight)
//
//        starAnimate(mCollapser, mExpander)
//    }
//
//    private fun starAnimate(collapsing: ValueAnimator.AnimatorUpdateListener, expanding: ValueAnimator.AnimatorUpdateListener) {
//        val animSet = AnimatorSet()
//
//        if (mExpanded) {
//
//            if (mVto != null) {
//
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    mVto.removeGlobalOnLayoutListener(contentListener)
//                } else {
//                    (mVto as ViewTreeObserver).removeOnGlobalLayoutListener(contentListener)
//                }
//            }
//
//            // start collapse
//            mExpanded = !mExpanded
//
//            var man: ValueAnimator
//            if (mAnimator != null && mAnimator.isRunning()) {
//
//                mAnimator.cancel()
//                val value = man.getAnimatedValue() as Float
//                man = ValueAnimator.ofFloat(value, 0)
//                man.addUpdateListener(collapsing)
//                man.setDuration((mAnimDuration * value).toInt().toLong())
//
//            } else {
//                mAnimator = ValueAnimator.ofFloat(1, 0)
//                mAnimator.addUpdateListener(collapsing)
//                mAnimator.setDuration(mAnimDuration.toLong())
//            }
//
//            animSet.play(mAnimator)
//        } else {
//
//            // start expand
//            mExpanded = !mExpanded
//            if (mAnimator != null && mAnimator.isRunning()) {
//                mAnimator.cancel()
//                val value = mAnimator.getAnimatedValue() as Float
//                mAnimator = ValueAnimator.ofFloat(value, 1)
//                mAnimator.addUpdateListener(expanding)
//                mAnimator.setDuration((mAnimDuration * (1 - value)).toInt().toLong())
//
//            } else {
//                mAnimator = ValueAnimator.ofFloat(0, 1)
//                mAnimator.addUpdateListener(expanding)
//                mAnimator.setDuration(mAnimDuration.toLong())
//            }
//
//            val finalizator = AnimatorCallback.get({
//
//                if (mVto == null) return
//
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    mVto.removeGlobalOnLayoutListener(contentListener)
//                } else {
//                    mVto.removeOnGlobalLayoutListener(contentListener)
//                }
//
//                mWrapperLp.height = LinearLayout.LayoutParams.WRAP_CONTENT
//
//            });
//
//            animSet.play(mAnimator).before(finalizator)
//        }
//
//        if (mOnExpandListener != null) mOnExpandListener.onExpand(this, mExpanded)
//
//        animSet.start()
//    }
//
//    //fixme move to class
//    private fun setChangeLayoutListener(scroll: View, top: Int, heightBottom: Int, scrollHeight: Int) {
//        //FIXME добавить удаление в финализаторе
//        mVto = viewTreeObserver
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            mVto?.removeGlobalOnLayoutListener(contentListener)
//        } else {
//            mVto?.removeOnGlobalLayoutListener(contentListener)
//        }
//
//        contentListener = object : ViewTreeObserver.OnGlobalLayoutListener {
//
//            var isFit = false
//            var exceeded = 0
//            var prevExceeded = 0
//
//            override fun onGlobalLayout() {
//                exceeded = mWrapperLp?.height + heightBottom - scrollHeight
//
//                if (exceeded > 0) {
//                    if (top > exceeded) {
//                        scroll.scrollBy(0, exceeded - prevExceeded)
//                        prevExceeded = exceeded
//
//                    } else if (!isFit) {
//                        isFit = true
//                        scroll.scrollBy(0, top - prevExceeded)
//                        prevExceeded = top
//                    }
//                }
//            }
//        }
//
//        mVto?.addOnGlobalLayoutListener(contentListener)
//    }
//}