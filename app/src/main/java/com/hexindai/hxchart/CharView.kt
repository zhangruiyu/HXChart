package com.hexindai.hxchart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CharView : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val maxPoint = 5
    //左右pad
    private val padding = 40
    private val whitePointradius = 20F
    //留一点点空间
    private val topPading = whitePointradius * 2
    //底部起始高度
    private var startHeight = 0

    private var firstX: Float = 0.toFloat()
    private var firstY: Float = 0.toFloat()
    /**
     * 根据可见点数计算出的两点之间的距离
     */
    private var realBetween: Float = 0.toFloat()
    /**
     * 手指/fling的上次位置
     */
    private var lastX: Float = 0.toFloat()
    /**
     * 滚动当前偏移量
     */
    private var offset: Float = 0.toFloat()
    /**
     * 滚动上一次的偏移量
     */
    private var lastOffset: Float = 0.toFloat()
    /**
     * 滚动偏移量的边界
     */
    private var maxOffset: Float = 0.toFloat()
    private var orientationX: Float = 0.toFloat()
    private val total = listOf(ValueAndText(8.0, "1"), ValueAndText(8.5, "2"), ValueAndText(9.0, "1"), ValueAndText(9.5, "1"),
            ValueAndText(10.0, "1"), ValueAndText(10.5, "1"), ValueAndText(11.0, "1"),
            ValueAndText(11.5, "1"), ValueAndText(12.0, "1"), ValueAndText(12.5, "1"), ValueAndText(13.0, "1"),
            ValueAndText(13.5, "1"),
            ValueAndText(14.0, "1"),
            ValueAndText(14.0, "1"),
            ValueAndText(14.0, "1"))
    private val whiteCircle: Paint = Paint()
    private val redCircle: Paint = Paint()
    private val redLine: Paint = Paint()
    private val redPath: Paint = Paint()

    init {
        whiteCircle.color = Color.WHITE

        whiteCircle.isAntiAlias = true;//设置线条等图形的抗锯齿
        redCircle.color = Color.RED
        redCircle.isAntiAlias = true;//设置线条等图形的抗锯齿
        redLine.color = Color.RED
        redLine.isAntiAlias = true;//设置线条等图形的抗锯齿
        redLine.strokeWidth = 3.0f
        redPath.color = Color.BLUE
        redPath.strokeWidth = 3.0f
        redPath.isAntiAlias = true;//设置线条等图形的抗锯齿
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.getActionMasked()) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.getX()
                firstX = lastX
                firstY = event.getY()
//                scroller.abortAnimation()
//                initOrResetVelocityTracker()
//                velocityTracker.addMovement(event)
                super.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> lastX = event.getX(0)
            MotionEvent.ACTION_MOVE -> {
                orientationX = event.getX() - lastX
                Log.e("orientationX", orientationX.toString())
                onScroll(orientationX)
                lastX = event.getX()
                /*   velocityTracker.addMovement(event)
                   if (needEdgeEffect && datas.get(0).size > maxOfVisible) {
                       if (isArriveAtLeftEdge()) {
                           edgeEffectLeft.onPull(Math.abs(orientationX) / linesArea.height())
                       } else if (isArriveAtRightEdge()) {
                           edgeEffectRight.onPull(Math.abs(orientationX) / linesArea.height())
                       }
                   }*/
            }
        /* MotionEvent.ACTION_POINTER_UP // 计算出正确的追踪手指
         -> {
             var minID = event.getPointerId(0)
             for (i in 0 until event.getPointerCount()) {
                 if (event.getPointerId(i) <= minID) {
                     minID = event.getPointerId(i)
                 }
             }
             if (event.getPointerId(event.getActionIndex()) == minID) {
                 minID = event.getPointerId(event.getActionIndex() + 1)
             }
             lastX = event.getX(event.findPointerIndex(minID))
         }
         MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
             if (needShowHint && event.getAction() == MotionEvent.ACTION_UP) {
                 val canCallTap = Math.abs(event.getX() - firstX) < 2 && Math.abs(event.getY() - firstY) < 2
                 if (canCallTap) {
                     onTap(event.getX(), event.getY())
                 }
             }
             velocityTracker.addMovement(event)
             velocityTracker.computeCurrentVelocity(1000, maxVelocity.toFloat())
             val initialVelocity = velocityTracker.getXVelocity().toInt()
             velocityTracker.clear()
             if (!isArriveAtLeftEdge() && !isArriveAtRightEdge()) {
                 scroller.fling(event.getX().toInt(), event.getY().toInt(), initialVelocity / 2,
                         0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0)
                 invalidate()
             } else {
                 edgeEffectLeft.onRelease()
                 edgeEffectRight.onRelease()
             }
             lastX = event.getX()
         }*/
        }

        return super.onTouchEvent(event)
    }

    /**
     * 滑动方法，同时检测边缘条件
     *
     * @param deltaX
     */
    private fun onScroll(deltaX: Float) {
        offset += deltaX
        Log.e("onScroll", offset.toString())
        offset = if (offset > width / 2) {
            (width / 2).toFloat()
        } else if (Math.abs(offset) > maxOffset) offset else offset
        invalidate()
    }

    val pointPosition = ArrayList<Point>()
    override fun draw(canvas: Canvas) {
        super.draw(canvas)
//        Log.e("translate", offset.toString())
        canvas.translate(offset, 0F)
        initPointPosition()
        //话最前面的区域
        canvas.drawLine((-width / 2).toFloat(), pointPosition[0].y.toFloat(), pointPosition[0].x.toFloat(), pointPosition[0].y.toFloat(), redLine)
        val path = Path()
        path.moveTo((-width / 2).toFloat(), pointPosition[0].y.toFloat())
        pointPosition.forEachIndexed { index, point ->
            path.lineTo(pointPosition[index].x.toFloat(), pointPosition[index].y.toFloat())
        }
        path.lineTo(pointPosition[pointPosition.size - 1].x.toFloat() + width / 2, pointPosition[pointPosition.size - 1].y.toFloat())
//        path.lineTo(pointPosition[pointPosition.size - 1].x.toFloat() + width / 2, pointPosition[pointPosition.size - 1].y.toFloat())
        path.lineTo(pointPosition[pointPosition.size - 1].x.toFloat() + width / 2, height.toFloat())
        path.lineTo((-width / 2).toFloat(), height.toFloat())
        path.close()
        canvas.drawPath(path, redPath)
        pointPosition.forEachIndexed { index, point ->

            //            canvas.drawCircle(itemWidth / 2f * index, itemWidth / 2f, 10f, redCircle)
            try {
                canvas.drawLine(point.x.toFloat(), point.y.toFloat(), pointPosition[index + 1].x.toFloat(), pointPosition[index + 1].y.toFloat(), redLine)
            } catch (e: Exception) {

            }
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), whitePointradius, whiteCircle)
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 10f, redCircle)

        }


    }

    private fun initPointPosition() {
        pointPosition.clear()
        //这个就是最高点
        val maxValuePoint = total.maxBy {
            it.value
        }
        val canUseHeight = getCanUseHeight()
        //初始化底部高度
        startHeight = (((1 - total[0].value / maxValuePoint!!.value) * canUseHeight) + topPading).toInt()

        Log.e("CharView", width.toString())
        Log.e("CharView", height.toString())
        val itemWidth = getItemWidth()

        total.forEachIndexed { index, valueAndText ->
            pointPosition.add(Point(padding + itemWidth / 2 + itemWidth * index, (((1 - valueAndText.value / maxValuePoint.value) * canUseHeight) + topPading).toInt()))

        }
    }

    private fun getCanUseHeight(): Float {
        return height - topPading
    }

    private fun getCanUseWidth(): Int {
        return width - padding * 2
    }

    private fun getItemWidth(): Int {
        return (getCanUseWidth() / maxPoint)
    }
}

class ValueAndText(val value: Double, val text: String)