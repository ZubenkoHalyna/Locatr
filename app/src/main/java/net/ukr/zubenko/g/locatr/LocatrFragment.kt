package net.ukr.zubenko.g.locatr

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.GoogleApiClient
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.ProgressBar
import com.google.android.gms.location.*
import net.ukr.zubenko.g.photogallery.GalleryItem


class LocatrFragment : Fragment() {
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mGalleryItem: GalleryItem
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

                mImageView.visibility = View.GONE
                mProgressBar.visibility = View.VISIBLE

                locationResult?.lastLocation?.let { location ->
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
            mImageView.setImageBitmap(bitmap)
            mImageView.visibility = View.VISIBLE
        }
        mProgressBar.visibility = View.GONE
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_locatr, container, false)
        mImageView = v.findViewById(R.id.image) as ImageView
        mImageView.setOnClickListener(::onClick)
        mProgressBar = v.findViewById(R.id.progressBar) as ProgressBar
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_locatr, menu)
    }
}