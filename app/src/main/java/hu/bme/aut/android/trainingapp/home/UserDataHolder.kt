package hu.bme.aut.android.trainingapp.home

import android.graphics.Bitmap
import hu.bme.aut.android.trainingapp.model.User

interface UserDataHolder {
    fun getLoaded() : Boolean
    fun getUser(): User
    fun getBitmap(): Bitmap
    fun getEmail(): String
    fun setDate(date : String)
    fun getDate() : String
}