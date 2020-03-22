package net.ukr.zubenko.g.locatr

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment


class PhotoPageActivity: SingleFragmentActivity() {
    private lateinit var mFragment: PhotoPageFragment

    companion object {
        fun newIntent(context: Context, photoPageUri: Uri): Intent {
            val i = Intent(context, PhotoPageActivity::class.java)
            i.data = photoPageUri
            return i
        }
    }

    override fun createFragment(): Fragment {
        mFragment = PhotoPageFragment.newInstance(intent.data ?: Uri.EMPTY)
        return mFragment
    }

    override fun onBackPressed() {
        if(! mFragment.pressBack())
            super.onBackPressed()
    }
}