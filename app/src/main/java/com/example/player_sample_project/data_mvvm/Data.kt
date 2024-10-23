package com.example.player_sample_project.data_mvvm

import android.os.Parcel
import android.os.Parcelable

data class Data(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val hair : String,
    val img_url: String)
    : Parcelable { // extends Parcelable for handle savedInstance in fragment to avoid re-settled the apiData when navigate back, Currently not used
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString() ?: ""
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(status)
        parcel.writeString(species)
        parcel.writeString(gender)
        parcel.writeString(hair)
        parcel.writeString(img_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Data> {
        override fun createFromParcel(parcel: Parcel): Data {
            return Data(parcel)
        }

        override fun newArray(size: Int): Array<Data?> {
            return arrayOfNulls(size)
        }
    }
}
