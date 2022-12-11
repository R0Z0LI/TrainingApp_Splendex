package hu.bme.aut.android.trainingapp.model

import android.os.Parcel
import android.os.Parcelable

data class Friend(
    var firstName: String? = "",
    var secondName: String? = "",
    var userName: String? = "",
    var trainingDetails: TrainingDetails ?= TrainingDetails("", "", "","")
): Parcelable {
    constructor(parcel: Parcel) : this (
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        TrainingDetails(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString()))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(firstName)
        parcel.writeString(secondName)
        parcel.writeString(userName)
        parcel.writeString(trainingDetails?.distanceTraveled)
        parcel.writeString(trainingDetails?.caloriesBurnt)
        parcel.writeString(trainingDetails?.hoursSpent)
        parcel.writeString(trainingDetails?.trainingsDone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Friend> {
        override fun createFromParcel(parcel: Parcel): Friend {
            return Friend(parcel)
        }

        override fun newArray(size: Int): Array<Friend?> {
            return arrayOfNulls(size)
        }
    }

}
