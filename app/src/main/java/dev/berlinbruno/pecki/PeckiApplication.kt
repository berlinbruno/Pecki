package dev.berlinbruno.pecki

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp
import dev.berlinbruno.pecki.data.security.AppSessionManager
import javax.inject.Inject

@HiltAndroidApp
class PeckiApplication : Application() {

    @Inject
    lateinit var sessionManager: AppSessionManager

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(sessionManager)
    }
}
