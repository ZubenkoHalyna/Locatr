package net.ukr.zubenko.g.locatr

import android.location.Location
import net.ukr.zubenko.g.photogallery.GalleryItem
import net.ukr.zubenko.g.photogallery.FlickrFetchr
import android.os.AsyncTask
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.IOException
import kotlin.reflect.KFunction1


class SearchTask(private val setBitmap: (Bitmap) -> Unit) : AsyncTask<Location, Void, Void>() {
    var mGalleryItem: GalleryItem? = null
    var mBitmap: Bitmap? = null


    companion object {
        private val TAG = "LocatrFragment"
    }

    override fun doInBackground(vararg params: Location): Void? {
        val fetchr = FlickrFetchr()
        val items = fetchr.searchPhotos(params[0])
        if (items.isEmpty()) {
            return null
        }
        mGalleryItem = items[0]
        try {mGalleryItem?.let {
            val bytes = fetchr.getUrlBytes(it.mUrl)
            mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        } catch (ioe: IOException) {
            Log.i(TAG, "Unable to download bitmap", ioe)
        }


        return null
    }

    override fun onPostExecute(result: Void?) {
        mBitmap?.let { bitmap ->
            setBitmap(bitmap)
        }
    }
}