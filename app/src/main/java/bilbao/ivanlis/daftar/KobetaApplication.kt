package bilbao.ivanlis.daftar

import android.app.Application
import timber.log.Timber

class KobetaApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}