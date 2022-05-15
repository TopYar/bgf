package com.example.boardgamefinder.core

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MyIconRendered(
    context: Context, map: GoogleMap,
    clusterManager: ClusterManager<EventClusteringItem>
) : DefaultClusterRenderer<EventClusteringItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(
        item: EventClusteringItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(item.markerOptions?.icon)
    }
}