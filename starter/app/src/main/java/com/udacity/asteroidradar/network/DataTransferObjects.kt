package com.udacity.asteroidradar.network


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.android.parcel.Parcelize

/**
 * Convert Network results to database objects
 */
@Entity
@Parcelize
data class PictureEntity(@PrimaryKey(autoGenerate = true) val id: Long=0L,
                         val mediaType: String, val title: String, val url: String) : Parcelable

fun PictureOfDay.asDatabaseModel():PictureEntity{
    return PictureEntity(
            mediaType = this.mediaType,
            title = this.title,
            url = this.url
    )
}

fun PictureEntity.asDomainModel():PictureOfDay{
    return PictureOfDay(
            mediaType = this.mediaType,
            title = this.title,
            url = this.url
    )
}
