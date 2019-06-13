package bilbao.ivanlis.kobeta

import android.app.Application
import timber.log.Timber

class KobetaApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}