package com.udacity.asteroidradar.database

import android.content.Context
import android.graphics.Picture
import androidx.lifecycle.LiveData
import androidx.room.*

/*For accessing data in database*/
@Dao
interface AsteroidDao {
    @Query("select * from asteroid_table order by date(closeApproachDate) asc")
    fun getAllAsteroid(): LiveData<List<AsteroidDatabaseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidDatabaseEntity)

    @Query("select * from asteroid_table where closeApproachDate = :date")
    fun getTodaysAsteroids(date: String): LiveData<List<AsteroidDatabaseEntity>>

    @Query("select * from asteroid_table where closeApproachDate between :startDate and :endDate order by date(closeApproachDate) asc")
    fun getWeeklyAsteroids(startDate: String, endDate: String) : LiveData<List<AsteroidDatabaseEntity>>

    @Query("delete from asteroid_table where closeApproachDate < :date")
    suspend fun removeAsteroids(date: String)

}


@Database(entities = [AsteroidDatabaseEntity::class,PictureEntity::class], version = 1)
abstract class AsteroidsDatabase : RoomDatabase() {
    abstract val asteroidDao:AsteroidDao
    abstract val pictureDao:PictureDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                    AsteroidsDatabase::class.java,
                    "asteroids").build()
        }
    }
    return INSTANCE
}

@Dao
interface PictureDao{
    @Query("select * from picture_of_the_day_table")
    fun getPictureOfTheDay() : LiveData<PictureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg pictureEntity: PictureEntity)

    @Query("Delete from picture_of_the_day_table")
    fun clear()
}

