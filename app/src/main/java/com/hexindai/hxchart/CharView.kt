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
    private val whitePointradius = 6.5f
    //留一点点空间
    private val topPading = whitePointradius * 2
    //底部起始高度
    private var startHeight = 0

    private var firstX: Float = 0.toFloat()
    private var firstY: Float = 0.toFloat()
    /**
     * 手指/fling的上次位置
     */
    private var lastX: Float = 0.toFloat()
    /**
     * 滚动当前偏移量
     */
    private var offset: Float = 0.toFloat()
    private var orientationX: Float = 0.toFloat()
    private val total = listOf(ValueAndText(8.0, "1"),
            ValueAndText(8.5, "2"),
            ValueAndText(9.0, "3"),
            ValueAndText(9.5, "4"),
            ValueAndText(10.0, "5"),
            ValueAndText(10.5, "6"),
            ValueAndText(11.0, "7"),
            ValueAndText(11.5, "8"),
            ValueAndText(12.0, "9"),
            ValueAndText(12.5, "10"),
            ValueAndText(13.0, "11"),
            ValueAndText(13.5, "12"),
            ValueAndText(14.0, "13"),
            ValueAndText(14.0, "14"),
            ValueAndText(14.0, "15"))
    private val whiteCircle: Paint = Paint()
    private val redCircle: Paint = Paint()
    private val redLine: Paint = Paint()
    private val redPath: Paint = Paint()
    private val textPaint: Paint = Paint()
    private val pointPosition = ArrayList<Point>()

    init {
        whiteCircle.color = Color.WHITE
        whiteCircle.isAntiAlias = true//设置线条等图形的抗锯齿

        redCircle.color = Color.parseColor("#FFE71D36")
        redCircle.isAntiAlias = true//设置线条等图形的抗锯齿

        redLine.color = Color.parseColor("#FFE71D36")
        redLine.isAntiAlias = true//设置线条等图形的抗锯齿
        redLine.strokeWidth = dip2px(1f).toFloat()

        redPath.color = Color.parseColor("#33E71D36")
//        redPath.alpha = 204
        redPath.strokeWidth = 3.0f
        redPath.isAntiAlias = true//设置线条等图形的抗锯齿

        textPaint.isAntiAlias = true;//设置线条等图形的抗锯齿
        textPaint.textSize = sp2px(12f).toFloat()
        textPaint.color = Color.parseColor("#FF333333")
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                firstX = lastX
                firstY = event.y
                super.onTouchEvent(event)
                return true
            }
            MotionEvent.ACTION_POINTER_DOWN -> lastX = event.getX(0)
            MotionEvent.ACTION_MOVE -> {
                orientationX = event.x - lastX
//                Log.e("orientationX", orientationX.toString())
                onScroll(orientationX)
                lastX = event.x
            }
            MotionEvent.ACTION_POINTER_UP // 计算出正确的追踪手指
            -> {

            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                lastX = event.x
                //中间的线
                val center = width / 2
                //屏幕中心 - x点 -偏移量
                Log.e("offset", offset.toString())
                //距离中心点最小
                val calculateDistanceToCenter = pointPosition.map {
                    //                    Log.e("offset", offset.toString())
//                    Log.e("pointPosition", (it.x - Math.abs(offset)).toString())
                    Log.e("pointPosition", (it.x - center + offset).toString())

                    val pointXYZ = PointXYZ(it)
                    pointXYZ.off = it.x - center + offset
                    pointXYZ
                }
                Log.e("MIN", minDistanceToCenter(calculateDistanceToCenter).off.toString())
                offset -= minDistanceToCenter(calculateDistanceToCenter).off
                postInvalidate()
            }
        }
//        minDistanceToCenter
        return super.onTouchEvent(event)
    }

    private fun minDistanceToCenter(list: List<PointXYZ>): PointXYZ {
        // 接近的数字
        val nearNum = 0
        // 差值实始化
        var diffNum = Math.abs(list[0].off - nearNum)
        // 最终结果
        var result = list[0]
        for (integer in list) {
            val diffNumTemp = Math.abs(integer.off - nearNum)
            if (diffNumTemp < diffNum) {
                diffNum = diffNumTemp
                result = integer
            }
        }

        return result
    }

    private fun onScroll(deltaX: Float) {
        offset += deltaX
//        Log.e("onScroll", offset.toString())
//        Log.e("onScroll", (pointPosition[pointPosition.size - 1].x.toFloat() + width / 2).toString())
        offset = when {
        //滑到最前
            offset > width / 2 -> (width / 2).toFloat()
        //最后
            Math.abs(offset) > pointPosition[pointPosition.size - 1].x.toFloat() - width / 2 -> -pointPosition[pointPosition.size - 1].x.toFloat() + width / 2
            else -> offset
        }
        invalidate()
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)
//        Log.e("translate", offset.toString())
        canvas.save()
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
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), dip2px(whitePointradius).toFloat(), whiteCircle)
            //小红色圆
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), dip2px(5f).toFloat(), redCircle)
            canvas.drawText(total[index].text+"月", point.x.toFloat(), (height - dip2px(8f)).toFloat(), textPaint)

        }
        canvas.restore()
        canvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), redLine)
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

    /**
     * 根据手机分辨率从DP转成PX
     * @param context
     * @param dpValue
     * @return
     */
    fun dip2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    fun sp2px(spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}

class ValueAndText(val value: Double, val text: String)

class PointXYZ(val list: Point) {
    var off = 0f
}