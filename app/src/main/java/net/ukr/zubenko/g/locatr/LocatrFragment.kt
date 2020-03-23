package net.ukr.zubenko.g.locatr

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.maps.SupportMapFragment
import net.ukr.zubenko.g.photogallery.GalleryItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.CameraUpdateFactory
import android.text.method.TextKeyListener.clear
import com.google.android.gms.maps.model.*


class LocatrFragment : SupportMapFragment() {
    private lateinit var mGalleryItem: GalleryItem
    private lateinit var mMapImage: Bitmap
    private lateinit var mCurrentLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    companion object {
        private val TAG = "LocatrFragment"
        private val REQUEST_LOCATION_PERMISSIONS = 0
        private val LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        fun newInstance() = LocatrFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        context?.let { context ->
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }

        getMapAsync {
            mMap = it
        }
    }

    private fun updateUI() {
        if (::mMap.isInitialized && ::mMapImage.isInitialized) {
            val itemPoint = LatLng(mGalleryItem.mLat, mGalleryItem.mLon)
            val myPoint = LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)

            val itemBitmap = BitmapDescriptorFactory.fromBitmap(mMapImage)
            val itemMarker = MarkerOptions()
                .position(itemPoint)
                .title("photo")
                .icon(itemBitmap)
            val picMarker = MarkerOptions()
                .position(itemPoint)
            val myMarker = MarkerOptions()
                .position(myPoint)
            mMap.clear()
            mMap.addMarker(itemMarker)
            mMap.addMarker(myMarker)
            mMap.addMarker(picMarker)
            mMap.setOnMarkerClickListener { marker ->
                if (marker.title == itemMarker.title)
                    context?.let { context ->
                        onClick(View(context))
                    }
                true
            }

            val bounds = LatLngBounds.Builder()
                .include(itemPoint)
                .include(myPoint)
                .build()
            val margin = resources.getDimensionPixelSize(R.dimen.map_inset_margin)
            val update = CameraUpdateFactory.newLatLngBounds(bounds, margin)
            mMap.animateCamera(update)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_locate -> {
                if (hasLocationPermission()) {
                    findImage()
                } else {
                    if (shouldShowRequestPermissionRationale(LOCATION_PERMISSIONS[0])) {
                        createLocationPermissionDialog().show()
                    } else
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun createLocationPermissionDialog(): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.location_permission_title)
            .setMessage(R.string.location_permission_request)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS)
            }
            .create()
    }

    private fun hasLocationPermission(): Boolean {
        val result = activity?.checkSelfPermission(LOCATION_PERMISSIONS[0]) ?: false
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS -> {
                if (hasLocationPermission()) {
                    findImage()
                } else {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @SuppressLint("MissingPermission")
    private fun findImage() {
        val request = LocationRequest.create()
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        request.numUpdates = 1
        request.interval = 0

        val callback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                locationResult?.lastLocation?.let { location ->
                    mCurrentLocation = location
                    Log.i(TAG, "Got a fix: ${location.latitude}, ${location.longitude}")
                    SearchTask(::searchTaskCallback).execute(location)
                }
            }
        }

        mFusedLocationClient.requestLocationUpdates(request, callback, null)
    }

    fun searchTaskCallback(bitmap: Bitmap?, item: GalleryItem) {
        mGalleryItem = item
        bitmap?.let {
            mMapImage = bitmap
        }
        updateUI()
    }

    fun onClick(v: View) {
        val uri = mGalleryItem.photoPageUri
        val i =
            if (uri.scheme == "http" || uri.scheme == "https")
                context?.let { context ->
                    PhotoPageActivity.newIntent(context, uri)
                }
            else
                Intent(Intent.ACTION_VIEW, uri)

        context?.startActivity(i)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_locatr, menu)
    }
}