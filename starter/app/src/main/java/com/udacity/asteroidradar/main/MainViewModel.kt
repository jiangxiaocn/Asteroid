package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.android.devbyteviewer.repository.AsteroidsRepository
import com.example.android.devbyteviewer.repository.PictureOfTheDayRepository
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.network.Network
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception



@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    enum class MenuItemFilter {SAVED,SHOW_TODAY,WEEK}

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)
    private val pictureRepository = PictureOfTheDayRepository(database)

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()

  /*  private var asteroidListLiveData: LiveData<List<Asteroid>>*/

    private val _asteroidsList = MutableLiveData<List<Asteroid>>()

    val asteroidsList: LiveData<List<Asteroid>>
          get() = _asteroidsList

    private val asteroidListObserver = Observer<List<Asteroid>> {
        //Update new list to RecyclerView
        _asteroidsList.value = it
    }

    var asteroidList = asteroidsRepository.asteroids
    val picture = pictureRepository.pictureOfTheDay

    /**
     * init{} is called immediately when this ViewModel is created.
     */
    init {
        asteroidList =
                asteroidsRepository.getAsteroidSelection(MenuItemFilter.SAVED)
        asteroidList.observeForever(asteroidListObserver)

        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
            pictureRepository.refreshPictureOfTheDay()
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
    fun updateFilter(filter: MenuItemFilter) {
        //Observe the new filtered LiveData
        asteroidList = asteroidsRepository.getAsteroidSelection(filter)
        asteroidList.observeForever(asteroidListObserver)
    }

}