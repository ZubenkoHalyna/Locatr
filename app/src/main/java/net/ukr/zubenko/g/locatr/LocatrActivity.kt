package net.ukr.zubenko.g.locatr

import android.content.DialogInterface
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability




class LocatrActivity : SingleFragmentActivity() {
    companion object {
        private val REQUEST_ERROR = 0
    }

    override fun createFragment() = LocatrFragment()


    override fun onResume() {
        super.onResume()

        val apiAvailability = GoogleApiAvailability.getInstance()
        val errorCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (errorCode != ConnectionResult.SUCCESS) {
            val errorDialog = apiAvailability
                .getErrorDialog(this, errorCode, REQUEST_ERROR,
                    DialogInterface.OnCancelListener {
                        // Выйти, если сервис недоступен.
                        finish()
                    })
            errorDialog.show()
        }
    }
}
