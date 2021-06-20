package com.udacity.asteroidradar.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.android.parcel.Parcelize

/*Database object*/
@Entity(tableName = "asteroid_table")
@Parcelize
data class AsteroidDatabaseEntity constructor(
        @PrimaryKey(autoGenerate = true) val id: Long, val codename: String, val closeApproachDate: String,
        val absoluteMagnitude: Double, val estimatedDiameter: Double,
        val relativeVelocity: Double, val distanceFromEarth: Double,
        val isPotentiallyHazardous: Boolean): Parcelable

/*Transfer Database object to domain object*/
fun List<AsteroidDatabaseEntity>.asDomainModel(): List<Asteroid> {
    return map {
        Asteroid(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous)
    }
}
fun List<Asteroid>.asDatabaseModel(): Array<AsteroidDatabaseEntity> {
    return map {
        AsteroidDatabaseEntity(
                id = it.id,
                codename = it.codename,
                closeApproachDate = it.closeApproachDate,
                absoluteMagnitude = it.absoluteMagnitude,
                estimatedDiameter = it.estimatedDiameter,
                relativeVelocity = it.relativeVelocity,
                distanceFromEarth = it.distanceFromEarth,
                isPotentiallyHazardous = it.isPotentiallyHazardous)
    }.toTypedArray()
}


@Entity(tableName = "picture_of_the_day_table")
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
