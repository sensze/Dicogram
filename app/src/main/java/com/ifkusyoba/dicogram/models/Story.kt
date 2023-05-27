package com.ifkusyoba.dicogram.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Story(

	@PrimaryKey
	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("lat")
	val lat: Double? = null,

	@field:SerializedName("lon")
	val lng: Double? = null
) : Parcelable {
	constructor(p0: Parcel): this(
		p0.readString(),
		p0.readString(),
		p0.readString(),
		p0.readString(),
		p0.readString().toString(),
		p0.readValue(Double::class.java.classLoader) as? Double,
		p0.readValue(Double::class.java.classLoader) as? Double
	)

	override fun describeContents(): Int {
		return 0
	}

	override fun writeToParcel(p0: Parcel, p1: Int) {
		p0.writeString(id)
		p0.writeString(name)
		p0.writeString(description)
		p0.writeString(photoUrl)
		p0.writeString(createdAt)
		p0.writeValue(lat)
		p0.writeValue(lng)
	}
	companion object CREATOR: Parcelable.Creator<Story> {
		override fun createFromParcel(parcel: Parcel): Story {
			return Story(parcel)
		}

		override fun newArray(size: Int): Array<Story?> {
			return arrayOfNulls(size)
		}
	}
}