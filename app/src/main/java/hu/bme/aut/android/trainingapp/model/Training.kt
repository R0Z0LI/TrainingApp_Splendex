package hu.bme.aut.android.trainingapp.model

import android.os.Parcel
import android.os.Parcelable

data class Training(
    var name : String ?= null,
    var type : String ?= null,
    var date: String ?= null,
    var duration: String ?= null,
    var calories: String ?= null,
    var distance: String ?= null,
    var avgSpeed: String ?= null,
    var maxSpeed: String ?= null,
    var avgPulse: String ?= null,
    var maxPulse: String ?= null
) : Parcelable{
    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(type)
        dest.writeString(date)
        dest.writeString(duration)
        dest.writeString(calories)
        dest.writeString(distance)
        dest.writeString(avgPulse)
        dest.writeString(maxSpeed)
        dest.writeString(avgPulse)
        dest.writeString(maxPulse)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Training>{
        override fun createFromParcel(parcel: Parcel): Training {
            return Training(parcel)
        }

        override fun newArray(size: Int): Array<Training?> {
            return arrayOfNulls(size)
        }
    }
}
