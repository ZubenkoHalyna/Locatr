package net.ukr.zubenko.g.locatr

import android.location.Location
import net.ukr.zubenko.g.photogallery.GalleryItem
import net.ukr.zubenko.g.photogallery.FlickrFetchr
import android.os.AsyncTask
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import java.util.*
import kotlin.reflect.KFunction2


class SearchTask(private val setBitmap: (Bitmap?, GalleryItem) -> Unit) : AsyncTask<Location, Void, Void>() {
    lateinit var mGalleryItem: GalleryItem
    var mBitmap: Bitmap? = null
    lateinit var mLocation: Location

    companion object {
        private val TAG = "LocatrFragment"
    }

    override fun doInBackground(vararg params: Location): Void? {
        val fetchr = FlickrFetchr()
        mLocation = params[0]
        val items = fetchr.searchPhotos(mLocation)
        if (items.isEmpty()) {
            return null
        }

        mGalleryItem = items[Random().nextInt(items.size)]
        try {
            val bytes = fetchr.getUrlBytes(mGalleryItem.mUrl)
            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (ioe: IOException) {
            Log.i(TAG, "Unable to download bitmap", ioe)
        }

        return null
    }

    override fun onPostExecute(result: Void?) {
        setBitmap(mBitmap, mGalleryItem)
    }
}