/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.getOneWeekDateFormattedDate
import com.udacity.asteroidradar.api.getTodayDateFormattedDate
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.MainViewModel
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.time.LocalDate

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAllAsteroid()) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val _startDate = LocalDate.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val _endDate = _startDate.plusDays(7)

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [videos]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val startDate= getTodayDateFormattedDate()
                val endDate = getOneWeekDateFormattedDate()

                val asteroidsResult  =  Network.asteroidService.getProperties(
                        startDate , endDate)
                val parsedAsteroidsResult = parseAsteroidsJsonResult(JSONObject(asteroidsResult))

                database.asteroidDao.insertAll(*parsedAsteroidsResult.asDatabaseModel())
            }catch (e: Exception){
                Timber.d("RefreshAsteroids failed ${e.message}")
                e.printStackTrace()
            }
        }
    }
    suspend fun removeOldAsteroids(){
        withContext(Dispatchers.IO){
            database.asteroidDao.removeAsteroids(getTodayDateFormattedDate())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAsteroidSelection(filter: MainViewModel.MenuItemFilter): LiveData<List<Asteroid>> {
        return when (filter) {
            (MainViewModel.MenuItemFilter.SAVED) -> {
                Transformations.map(database.asteroidDao.getAllAsteroid())
                {
                    it.asDomainModel()
                }
            }
            (MainViewModel.MenuItemFilter.SHOW_TODAY) -> {
                Transformations.map(database.asteroidDao.getTodaysAsteroids(_startDate.toString())) {
                    it.asDomainModel()
                }
            }
            else -> {
                Transformations.map(database.asteroidDao.getWeeklyAsteroids(_startDate.toString(),_endDate.toString())) {
                    it.asDomainModel()
                }
            }
        }
    }
}
