package com.acacia.simpletodo.todo.list

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.acacia.simpletodo.R
import java.util.*


interface MyButtonClickListener {
    fun onClick(pos: Int)
}

class MySimpleSwipeHelper(
    private val context: Context, private val recyclerView: RecyclerView, private val buttonWidth: Int, private val callback: (Int) -> Unit) : MySwipeHelper(context, recyclerView, buttonWidth) {
    override fun instantiatrMyButton(
        viewHolder: RecyclerView.ViewHolder?,
        buffer: MutableList<MyButton>?
    ) {
        if (buffer != null) {
            buffer.add(MyButton(context, "삭제", 40, 0, Color.RED, object :
                MyButtonClickListener {
                override fun onClick(pos: Int) {
                    callback.invoke(pos)
                }

            }) )

        }
    }
}

abstract class MySwipeHelper(
    context: Context?,
    private val recyclerView: RecyclerView,
    private val buttonWidth: Int
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var buttonList = mutableListOf<MyButton>()

    private var swipePosition = -1
    private var swipeThreshold = 0.5f
    private val buttonBuffer = mutableMapOf<Int, MutableList<MyButton>>()
    private val removerQueue = object : LinkedList<Int?>() {
        override fun add(element: Int?): Boolean {
            return if (contains(element)) false else super.add(element)
        }
    }
    private val gestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            for (button in buttonList) {
                if (button.onClick(e.x, e.y)) break
            }
            return super.onSingleTapUp(e)
        }
    }
    private val gestureDetector = GestureDetector(context, gestureListener)
    private val onTouchListener =
        OnTouchListener { v, motionEvent ->
            if (swipePosition < 0) return@OnTouchListener false
            val point =
                Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
            val swipeViewHolder =
                recyclerView.findViewHolderForAdapterPosition(swipePosition)
            val swipedItem = swipeViewHolder!!.itemView
            val rect = Rect()
            swipedItem.getGlobalVisibleRect(rect)
            if (motionEvent.action == MotionEvent.ACTION_DOWN || motionEvent.action == MotionEvent.ACTION_UP || motionEvent.action == MotionEvent.ACTION_MOVE
            ) {
                if (rect.top < point.y && rect.bottom > point.y) gestureDetector.onTouchEvent(
                    motionEvent
                ) else {
                    removerQueue.add(swipePosition)
                    swipePosition = -1
                    recoverSwipedItem()
                }
            }
            false
        }

    init {
        recyclerView.setOnTouchListener(onTouchListener)
        attachSwipe()
    }

    @Synchronized
    protected fun recoverSwipedItem() {
        while (!removerQueue.isEmpty()) {
            val pos = removerQueue.poll()
            if (pos != null) {
                if (pos > -1) recyclerView.adapter!!.notifyItemChanged(pos)
            }
        }
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    inner class MyButton(
        context: Context,
        private val text: String,
        private val textSize: Int,
        private val imageResId: Int,
        private val color: Int,
        listener: MyButtonClickListener
    ) {
        private var pos = 0
        private var clickRegion: RectF? = null
        private val listener: MyButtonClickListener
        private val context: Context
        private val resources: Resources
        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                listener.onClick(pos)
                return true
            }
            return false
        } // onClick()..

        fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
            val p = Paint()
            p.color = color
            c.drawRect(rectF, p)
            //text
            p.color = Color.WHITE
            p.textSize = textSize.toFloat()
            val r = Rect()
            val cHeight = rectF.height()
            val cWidth = rectF.width()
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)
            var x = 0f
            var y = 0f
//            x = cWidth / 2f - r.width() / 2f - r.left
//            y = cHeight / 2f + r.height() / 2f - r.bottom
            if (imageResId == 0) {
                x = cWidth / 2f - r.width() / 2f - r.left
                y = cHeight / 2f + r.height() / 2f - r.bottom
                c.drawText(text, rectF.left + x, rectF.top + y, p)
            } else {
                val d =
                    ContextCompat.getDrawable(context, imageResId)
                val bitmap = drawableToBitmap(d)
                c.drawBitmap(
                    bitmap,
                    (rectF.left + rectF.right) / 2 - (bitmap.width / 2),
                    (rectF.top + rectF.bottom) / 2 - (bitmap.height / 2),
                    p
                )
//                c.drawText(text, rectF.left + x + bitmap.width , rectF.top + y, p)
            }
            clickRegion = rectF
            this.pos = pos
        }

        init {
            this.listener = listener
            this.context = context
            resources = context.resources
        } // MyButton()..
    } // MyButton class..

    protected fun drawableToBitmap(d: Drawable?): Bitmap {
        if (d is BitmapDrawable) return d.bitmap
        val bitmap = Bitmap.createBitmap(
            d!!.intrinsicWidth,
            d.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipePosition != pos) removerQueue.add(swipePosition)
        swipePosition = pos
        if (buttonBuffer.containsKey(swipePosition)) buttonList =
            buttonBuffer[swipePosition]!! else buttonList.clear()
        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttonList.size * buttonWidth
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipePosition = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Log.d("yyy", " dx = $dX")
            if (dX < 0) {
                var buffer: MutableList<MyButton> =
                    ArrayList()
                if (!buttonBuffer.containsKey(pos)) {
                    instantiatrMyButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer
                } else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size * buttonWidth / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButton(
        c: Canvas,
        itemView: View,
        buffer: List<MyButton>,
        pos: Int,
        translationX: Float
    ) {
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * translationX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c, RectF(
                    left,
                    itemView.top.toFloat(), right,
                    itemView.bottom.toFloat()
                ), pos
            )
            right = left
        }
    }

    abstract fun instantiatrMyButton(
        viewHolder: RecyclerView.ViewHolder?,
        buffer: MutableList<MyButton>?
    )

     // MySwipeHelper*()..
} // MySwipeHelper class..


