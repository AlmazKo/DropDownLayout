package com.github.almazko.test1

import android.content.Context
import android.os.Build
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.LinearLayout
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by almaz on 21.02.16.
 */
class DDLayout : LinearLayout {

    @LayoutRes private val mHeaderId: Int
    @LayoutRes private val mContentId: Int
    @IdRes private val mOnClickHandlerId: Int

    private var mExpanded: Boolean = false
    private var mHeaderText: String? = null
    private var mAnimDuration: Int = 0

    private val mInflater: LayoutInflater
    private var mWrapperLp: LinearLayout.LayoutParams? = null


    interface OnExpandListener {
        fun onExpand(expandableLayout: DDLayout, isExpand: Boolean)
    }


    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DropDowLayout, 0, 0)

        try {
            mHeaderId = a.getResourceId(R.styleable.DropDowLayout_header, 0)
            mContentId = a.getResourceId(R.styleable.DropDowLayout_content, 0)
            mOnClickHandlerId = a.getResourceId(R.styleable.DropDowLayout_onclick_handler, 0)
            mExpanded = a.getBoolean(R.styleable.DropDowLayout_expanded, false)
            mHeaderText = a.getString(R.styleable.DropDowLayout_header_text)

            val defaultAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime)
            mAnimDuration = a.getInt(R.styleable.DropDowLayout_anim_time, defaultAnimTime)
        } finally {

        }

                if (mHeaderId == 0) {
                    throw IllegalArgumentException(
                            "The handle attribute is required and must refer to a valid header layout.")
                }

        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        orientation = LinearLayout.VERTICAL
    }

    //
    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val header = initHeader(mHeaderId, mHeaderText)
        val contentWrapper = initContentWrapper()



        if (mContentId != 0) {
            var content = mInflater.inflate(mContentId, contentWrapper, false)
            contentWrapper.addView(content)
        }
        val dd = DDLayout2(header, contentWrapper, mWrapperLp!!, this)
    }


    private fun initHeader(headerId: Int, text: CharSequence?): View {
        val header = mInflater.inflate(headerId, this, false)
        header.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.AT_MOST)
        addView(header)
        //        setText(header, text)

        return header
    }

    private fun initContentWrapper(): ViewGroup {
        val wrapper = FrameLayout(context)
        mWrapperLp = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        wrapper.layoutParams = mWrapperLp

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wrapper.id = generateViewId2()
        } else {
            wrapper.id = View.generateViewId()
        }

        addView(wrapper)

        return wrapper
    }

    private val sNextGeneratedId = AtomicInteger(1)

    fun generateViewId2(): Int {
        while (true) {
            val result = sNextGeneratedId.get()
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            var newValue = result + 1
            if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result
            }
        }
    }


}