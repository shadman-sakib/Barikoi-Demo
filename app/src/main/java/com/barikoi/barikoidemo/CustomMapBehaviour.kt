package com.barikoi.barikoidemo

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

class CustomMapBehaviour(context: Context, attrs: AttributeSet) :
    CoordinatorLayout.Behavior<View>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency.id != R.id.fab &&
                dependency.id != R.id.autocompletepane &&
                dependency.id != R.id.layoutTop &&
                dependency.id != R.id.bottomsheet_navigation


    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        //Log.d("customMapBehaviour",dependency.getTop()+"");
        val actualPeek = (parent.height * 1.0 / 16.0 * 7.0).toInt()

        Log.d("customMapBehaviour","Actual Peek: " +actualPeek.toString())

        Log.d("customMapBehaviour","Dependency Top: " +dependency.top + " view: " +dependency.id.toString())



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (dependency.top == 0)
                dependency.isNestedScrollingEnabled = true
            else
                dependency.isNestedScrollingEnabled = false
        }

        if (dependency.top >= actualPeek) {

            val height = parent.height.toFloat()
            child.translationY = -(height - dependency.top) / 2
            return true
        } else
            return false
    }

    companion object {
        private val initialPositionY: Float = 0.toFloat()
    }


}
