package com.example.awarely.overlay


import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import android.widget.Toast
import com.example.awarely.R

class FloatingTimerView(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var view: View? = null
    private var timerText: TextView? = null
    private var seconds = 0
    private var handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null

    private var layoutParams: WindowManager.LayoutParams? = null
    private var initialX = 0
    private var initialY = 0
    private var touchX = 0f
    private var touchY = 0f

    fun show() {
        if (view != null) return

        view = LayoutInflater.from(context).inflate(R.layout.floating_timer, null)
        timerText = view?.findViewById(R.id.timerText)

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        layoutParams?.gravity = Gravity.TOP or Gravity.START
        layoutParams?.x = 100
        layoutParams?.y = 300

        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager?.addView(view, layoutParams)

        view?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams!!.x
                    initialY = layoutParams!!.y
                    touchX = event.rawX
                    touchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams!!.x = initialX + (event.rawX - touchX).toInt()
                    layoutParams!!.y = initialY + (event.rawY - touchY).toInt()
                    windowManager?.updateViewLayout(view, layoutParams)
                    true
                }
                else -> false
            }
        }

        startTimer()
    }

    fun hide() {
        stopTimer()
        if (view != null) {
            windowManager?.removeView(view)
            view = null
        }
    }

    private fun startTimer() {
        seconds = 0
        runnable = object : Runnable {
            override fun run() {
                val minutes = seconds / 60
                val remSec = seconds % 60
                timerText?.text = String.format("%02d:%02d", minutes, remSec)
                seconds++
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable!!)
    }

    private fun stopTimer() {
        handler.removeCallbacks(runnable!!)
        runnable = null
    }
}
