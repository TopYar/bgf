package com.example.boardgamefinder.core

import android.graphics.drawable.Drawable
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterItem

class EventClusteringItem(
    private var latitude: Double,
    private var longitude: Double,
    private val title: String,
    private val snippet: String,
    private val markerType: MarkerType,
    //ToDo maybe not to store it here
    private val drawable: Drawable,
    private val id : Int
) : ClusterItem {

    var markerOptions: MarkerOptions? = null

    companion object{
        private var eventMarker: BitmapDescriptor? = null
    }

    enum class MarkerType {
        EVENT
    }

    init {
        setMarkerOptions()
    }

    fun getMarkerType(): MarkerType {
        return markerType
    }

    fun getId(): Int {
        return id
    }

    override fun getPosition(): LatLng {
        return LatLng(latitude, longitude)
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    private fun setMarkerIcons(){
        eventMarker = DrawableConverter.drawableToBitmapDescription(drawable)
    }

    private fun setMarkerOptions(){
        val mo = MarkerOptions()
            .position(position)
            .title(title)
            .snippet(snippet)

        if(eventMarker == null)
            setMarkerIcons()

        mo.icon(eventMarker)

        markerOptions = mo
    }
}