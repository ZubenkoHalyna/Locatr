package net.ukr.zubenko.g.locatr

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class PhotoPageFragment: Fragment() {
    private lateinit var mUri: Uri
    private lateinit var mWebView: WebView
    private lateinit var mProgressBar: ProgressBar

    companion object {
        private val ARG_URI = "photo_page_url"
        fun newInstance(uri: Uri): PhotoPageFragment {
            val args = Bundle()
            args.putParcelable(ARG_URI, uri)
            val fragment = PhotoPageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mUri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_photo_page, container, false)
        mProgressBar = v.findViewById(R.id.progress_bar) as ProgressBar
        mProgressBar.max = 100 // Значения в диапазоне 0-100

        mWebView = v.findViewById(R.id.web_view) as WebView
        mWebView.settings.javaScriptEnabled = true

        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(webView: WebView, newProgress: Int) {
                if (newProgress == 100) {
                    mProgressBar.visibility = View.GONE
                } else {
                    mProgressBar.visibility = View.VISIBLE
                    mProgressBar.progress = newProgress
                }
            }

            override fun onReceivedTitle(webView: WebView, title: String) {
                val activity = activity as? AppCompatActivity
                activity?.supportActionBar?.subtitle = title
            }
        }

        mWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val uri = Uri.parse(url)
                val shouldOverride = (uri.scheme != "http" && uri.scheme != "https")

                if (shouldOverride) {
                    try {
                        Intent.parseUri(url, Intent.URI_INTENT_SCHEME).data?.let { link ->
                           val i = Intent(Intent.ACTION_VIEW, link)
                           context?.startActivity(i)
                       }
                    } catch (e: Exception) {
                        return true
                    }
                }
                return shouldOverride
            }
        }

        mWebView.loadUrl(mUri.toString())
        return v
    }

    fun pressBack(): Boolean {
        val canGoBack = mWebView.canGoBack()
        if (canGoBack)
            mWebView.goBack()
        return canGoBack
    }
}