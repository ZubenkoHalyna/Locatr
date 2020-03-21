package net.ukr.zubenko.g.locatr

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Looper
import com.google.android.gms.location.*


class LocatrFragment : Fragment() {
    private lateinit var mImageView: ImageView
    private lateinit var mClient: GoogleApiClient
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
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_locate -> {
                if (hasLocationPermission()) {
                    findImage()
                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
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
                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                    Log.i(TAG, "Got a fix: ${location.latitude}, ${location.longitude}")
                    SearchTask(::setBitmap).execute(location)
                }
            }
        }

        mFusedLocationClient.requestLocationUpdates(request, callback, null)


    }

    fun setBitmap(bitmap: Bitmap) {
        mImageView.setImageBitmap(bitmap)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_locatr, container, false)
        mImageView = v.findViewById(R.id.image) as ImageView
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_locatr, menu)
    }
}