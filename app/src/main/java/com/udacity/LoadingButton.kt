package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText: String
    private val textRect = Rect()
    private var buttonBckgColor = 0
    private var progress = 0f
    private var valueAnimator = ValueAnimator()

    private var downloadBtnBckgColorDefaultState = 0
    private var downloadBtnBckgColorLoadingState = 0
    private var downloadBtnCircleColor = 0
    private var downloadBtnTextColor = 0

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Clicked -> {

            }

            ButtonState.Loading -> {
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    setAnimatorListeners()

                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.REVERSE
                    duration = 2000

                    start()
                }

                buttonText = resources.getString(R.string.button_loading)
                buttonBckgColor = downloadBtnBckgColorLoadingState
                invalidate()
            }

            ButtonState.Completed -> {
                valueAnimator.cancel()
                progress = 0f
                buttonText = resources.getString(R.string.download)
                buttonBckgColor = downloadBtnBckgColorDefaultState
                invalidate()
            }
        }
    }

    init {
        isClickable = true
        buttonText = resources.getString(R.string.download)
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            downloadBtnBckgColorDefaultState = getColor(R.styleable.LoadingButton_downloadBtnBckgColorDefaultState, 0)
            downloadBtnBckgColorLoadingState = getColor(R.styleable.LoadingButton_downloadBtnBckgColorLoadingState, 0)
            downloadBtnCircleColor = getColor(R.styleable.LoadingButton_downloadBtnCircleColor, 0)
            downloadBtnTextColor = getColor(R.styleable.LoadingButton_downloadBtnTextColor, 0)
        }
        buttonBckgColor = downloadBtnBckgColorDefaultState
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        color = downloadBtnTextColor
    }

    private val loadingBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = downloadBtnBckgColorLoadingState
    }
    private val defaultBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = downloadBtnBckgColorDefaultState
    }
    private val loadingArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = downloadBtnCircleColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val cornerRadius = 25.0f

        canvas?.drawColor(buttonBckgColor)

        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        val centerX = widthSize.toFloat() / 2
        val centerY = heightSize.toFloat() / 2 - textRect.centerY()

        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), defaultBackgroundPaint)

        if(buttonState == ButtonState.Loading) {
            var progressVal = progress * widthSize.toFloat()
            canvas?.drawRect(0f, 0f, progressVal, heightSize.toFloat(), loadingBackgroundPaint)

            val arcDiameter = cornerRadius * 2
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            progressVal = progress * 360f
            canvas?.drawArc(paddingStart.toFloat() + arcDiameter,
                    paddingTop.toFloat() + arcDiameter,
                    arcRectSize,
                    arcRectSize,
                    0f,
                    progressVal,
                    true,
                    loadingArcPaint)
        }

        canvas?.drawText(buttonText, centerX, centerY , textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setDownloadButtonState(state: ButtonState) {
        buttonState = state
    }

    private fun ValueAnimator.setAnimatorListeners() {
        addUpdateListener {
            progress = animatedValue as Float
            invalidate()
        }

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                isEnabled = false
            }

            override fun onAnimationEnd(animation: Animator?) {
                isEnabled = true
            }
        })
    }
}