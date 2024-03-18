package com.example.weather.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favoriteWeather",indices = [Index(value = ["city"], unique = true)])
data class FavoriteWeather(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherEntry>,
    val city: City
){
constructor(): this(0,"",0,0, listOf(),City())
}
@Entity(tableName = "favoritePlace")
data class FavoritePlace(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val address: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FavoritePlace> {
        override fun createFromParcel(parcel: Parcel): FavoritePlace {
            return FavoritePlace(parcel)
        }

        override fun newArray(size: Int): Array<FavoritePlace?> {
            return arrayOfNulls(size)
        }
    }
}

