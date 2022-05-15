package com.example.boardgamefinder.core

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object DrawableConverter {
    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun drawableToBitmapDescription(drawable: Drawable) : BitmapDescriptor?{
        val btm = drawableToBitmap(drawable)
        val smallMarker = btm?.let {
            Bitmap.createScaledBitmap(it, 100, 100, false)
        }
        return smallMarker?.let { BitmapDescriptorFactory.fromBitmap(it) }
    }
}