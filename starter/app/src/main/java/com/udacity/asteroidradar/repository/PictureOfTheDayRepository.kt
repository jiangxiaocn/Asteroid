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

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.Network
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

class PictureOfTheDayRepository(private val database: AsteroidsDatabase) {

    val pictureOfTheDay: LiveData<PictureOfDay> =
            Transformations.map(database.pictureDao.getPictureOfTheDay()) {
                it.asDomainModel()
    }

    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [videos]
     */
    suspend fun refreshPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            try {

                val picture  =  Network.pictureOfTheDayService.getImageOfTheDay()
                if (picture.mediaType=="image"){
                    database.pictureDao.clear()
                    database.pictureDao.insertAll(picture.asDatabaseModel())
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
