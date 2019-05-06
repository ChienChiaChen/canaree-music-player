package dev.olog.msc.presentation.widget

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dev.olog.msc.shared.extensions.dip
import dev.olog.presentation.base.interfaces.HasSlidingPanel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

private const val DEFAULT_SWIPED_THRESHOLD = 100

class SwipeableView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null

) : View(context, attrs), SlidingUpPanelLayout.PanelSlideListener {

    private val swipedThreshold = DEFAULT_SWIPED_THRESHOLD
    private var xDown = 0f
    private var xUp = 0f
    private var yDown = 0f
    private var yUp = 0f
    private var swipeListener: SwipeListener? = null
    private val isTouchingPublisher = PublishSubject.create<Boolean>()

    private var isTouchEnabled = true

    private val sixtyFourDip by lazy(LazyThreadSafetyMode.NONE) { context.dip(64) }

    fun setOnSwipeListener(swipeListener: SwipeListener?) {
        this.swipeListener = swipeListener
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (!isInEditMode && context is HasSlidingPanel){
            ((context as Activity) as HasSlidingPanel).addPanelSlideListener(this)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        this.swipeListener = null
        if (context is HasSlidingPanel){
            ((context as Activity) as HasSlidingPanel).removePanelSlideListener(this)

        }
    }

    fun isTouching(): Observable<Boolean> = isTouchingPublisher.distinctUntilChanged()

    override fun onTouchEvent(event: MotionEvent): Boolean {

        return when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                isTouchingPublisher.onNext(true)
                onActionDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                onActionMove(event)
                isTouchingPublisher.onNext(true)
                return true
            }
            MotionEvent.ACTION_UP  -> {
                isTouchingPublisher.onNext(false)
                onActionUp(event)
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun onActionDown(event: MotionEvent) : Boolean{
        xDown = event.x
        yDown = event.y
        return true
    }

    private fun onActionMove(event: MotionEvent) {
    }

    private fun onActionUp(event: MotionEvent) : Boolean {
        xUp = event.x
        yUp = event.y
        val swipedHorizontally = Math.abs(xUp - xDown) > swipedThreshold
        val swipedVertically = Math.abs(yUp - yDown) > swipedThreshold

        val isHorizontalScroll = swipedHorizontally && Math.abs(xUp - xDown) > Math.abs(yUp - yDown)

        if (isHorizontalScroll) {
            val swipedRight = xUp > xDown
            val swipedLeft = xUp < xDown

            if (swipedRight) {
                if (swipeListener != null && isTouchEnabled) {
                    swipeListener!!.onSwipedRight()
                    return true
                }
            }
            if (swipedLeft) {
                if (swipeListener != null && isTouchEnabled) {
                    swipeListener!!.onSwipedLeft()
                    return true
                }
            }
        }

        if (!swipedHorizontally && !swipedVertically) {
            when {
                xDown < sixtyFourDip && isTouchEnabled -> swipeListener?.onLeftEdgeClick()
                ((width - xDown) < sixtyFourDip) && isTouchEnabled-> swipeListener?.onRightEdgeClick()
                else -> {
                    if (isTouchEnabled){
                        swipeListener?.onClick()
                    }
                }
            }
            return true
        }
        return false
    }

    override fun onPanelSlide(panel: View?, slideOffset: Float) {

    }

    override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState) {
        isTouchEnabled = newState == SlidingUpPanelLayout.PanelState.EXPANDED
    }

    interface SwipeListener {
        fun onSwipedLeft() {}
        fun onSwipedRight() {}
        fun onClick() {}
        fun onLeftEdgeClick(){}
        fun onRightEdgeClick(){}
    }
}
