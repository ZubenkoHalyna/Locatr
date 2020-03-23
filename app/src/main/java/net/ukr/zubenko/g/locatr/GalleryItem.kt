package net.ukr.zubenko.g.photogallery

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class GalleryItem(
    @SerializedName("id")
    val mId: String,
    @SerializedName("owner")
    val mOwner: String,
    @SerializedName("title")
    val mCaption: String,
    @SerializedName("url_s")
    val mUrl: String,
    @SerializedName("latitude")
    val mLat: Double,
    @SerializedName("longitude")
    val mLon: Double) {

    val photoPageUri: Uri
        get() = Uri.parse("https://www.flickr.com/photos/")
            .buildUpon()
            .appendPath(mOwner)
            .appendPath(mId)
            .build()

    override fun toString() = mCaption
}