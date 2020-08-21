package com.acacia.simpletodo.todo.list

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView


class SimpleSwipeHelpler(private val buttonsActions: ItemTouchHelperListener) :
    ItemTouchHelper.Callback() {

    interface ItemTouchHelperListener {
        fun onLeftClicked(position: Int)
        fun onRightClicked(position: Int)
    }

    internal enum class ButtonsState {
        GONE, LEFT_VISIBLE, RIGHT_VISIBLE
    }

    private var buttonShowedState: ButtonsState = ButtonsState.GONE
    private var buttonInstance: RectF? = null
    private val buttonWidth = 300f
    private var swipeBack = false
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0 , LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
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
//        if (actionState == ACTION_STATE_SWIPE) {
//            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                var dx = 0f
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                    dx = Math.max(dX, buttonWidth)
                }
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    dx = Math.min(dX, -buttonWidth)
                }
//                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dx,
                    dY,
                    actionState,
                    isCurrentlyActive
                );
            } else {
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                );
            }
            if (buttonShowedState == ButtonsState.GONE) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                );
            }
        }

//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        drawButtons(c, viewHolder)
    }

    private fun drawButtons(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder
    ) {
        val buttonWidthWithoutPadding: Float = buttonWidth - 20
        val corners = 16f
        val itemView: View = viewHolder.itemView
        val p = Paint()
        val rightButton = RectF(
            itemView.getRight() - buttonWidthWithoutPadding,
            itemView.getTop().toFloat(),
            itemView.getRight().toFloat(),
            itemView.getBottom().toFloat()
        )
        p.setColor(Color.RED)
        c.drawRoundRect(rightButton, corners, corners, p)
        drawText("DELETE", c, rightButton, p)
        buttonInstance = null
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {

        } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton
        }
    }

    private fun drawText(
        text: String,
        c: Canvas,
        button: RectF,
        p: Paint
    ) {
        val textSize = 60f
        p.setColor(Color.WHITE)
        p.setAntiAlias(true)
        p.setTextSize(textSize)
        val textWidth: Float = p.measureText(text)
        c.drawText(text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p)
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth)
                    buttonShowedState = ButtonsState.RIGHT_VISIBLE
                else if (dX > buttonWidth)
                    buttonShowedState = ButtonsState.LEFT_VISIBLE
                if (buttonShowedState !== ButtonsState.GONE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@SimpleSwipeHelpler.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                recyclerView.setOnTouchListener { v, event -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                if (buttonsActions != null && buttonInstance != null && buttonInstance!!.contains(
                        event.getX(),
                        event.getY()
                    )
                ) {
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                        buttonsActions.onLeftClicked(viewHolder.adapterPosition);
                    } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions.onRightClicked(viewHolder.adapterPosition);
                    }
                }



            }
            buttonShowedState = ButtonsState.GONE
            false
        }
    }

//    private fun setTouchUpListener2(
//        c: Canvas,
//        recyclerView: RecyclerView,
//        viewHolder: RecyclerView.ViewHolder,
//        dX: Float, dY: Float,
//        actionState: Int, isCurrentlyActive: Boolean
//    ) {
//        recyclerView.setOnTouchListener(new View . OnTouchListener () {
//            @Override public boolean onTouch(
//                View v,
//                MotionEvent event
//            ) {
//                ItemTouchHelperCallback.super.onChildDraw(
//                    c,
//                    recyclerView,
//                    viewHolder,
//                    0F,
//                    dY,
//                    actionState,
//                    isCurrentlyActive
//                ); recyclerView.setOnTouchListener(new View . OnTouchListener () {
//                @Override public boolean onTouch(
//                    View v,
//                    MotionEvent event
//                ) { return false; }
//            }); setItemsClickable(recyclerView, true); swipeBack =
//                false; if (listener != null && buttonInstance != null && buttonInstance.contains(
//                    event.getX(),
//                    event.getY()
//                )
//            ) {
//                if (buttonsShowedState == ButtonsState.LEFT_VISIBLE) {
//                    listener.onLeftClick(
//                        viewHolder.getAdapterPosition(),
//                        viewHolder
//                    ); } else if (buttonsShowedState == ButtonsState.RIGHT_VISIBLE) {
//                    listener.onRightClick(viewHolder.getAdapterPosition(), viewHolder); }
//            }
//                buttonsShowedState = ButtonsState . GONE; currenrtItemViewHolder = null; return false;
//            }
//        });
//    }


    private fun setItemsClickable(
        recyclerView: RecyclerView,
        isClickable: Boolean
    ) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }
}