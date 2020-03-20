package com.ishanknijhawan.notefy.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.ScrollView


class VerticalScrollview : ScrollView {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                Log.i("VerticalScrollview", "onInterceptTouchEvent: DOWN super false")
                super.onTouchEvent(ev)
            }
            MotionEvent.ACTION_MOVE -> return false // redirect MotionEvents to ourself
            MotionEvent.ACTION_CANCEL -> {
                Log.i(
                    "VerticalScrollview",
                    "onInterceptTouchEvent: CANCEL super false"
                )
                super.onTouchEvent(ev)
            }
            MotionEvent.ACTION_UP -> {
                Log.i("VerticalScrollview", "onInterceptTouchEvent: UP super false")
                return false
            }
            else -> Log.i("VerticalScrollview", "onInterceptTouchEvent: $action")
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        super.onTouchEvent(ev)
        Log.i("VerticalScrollview", "onTouchEvent. action: " + ev.action)
        return true
    }
}