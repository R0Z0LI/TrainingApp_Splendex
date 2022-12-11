package hu.bme.aut.android.trainingapp

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class FireBaseData : Application() {
    override fun onCreate() {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        super.onCreate()
    }
}