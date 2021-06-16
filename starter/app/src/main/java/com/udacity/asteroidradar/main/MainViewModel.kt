package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidFilter
import kotlinx.coroutines.launch
import java.lang.Exception

enum class AsteroidApiStatus {LOADING,ERROR,DONE}

class MainViewModel : ViewModel() {
    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status= MutableLiveData<AsteroidApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<AsteroidApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<Asteroid>>()

    val properties: LiveData<List<Asteroid>>
        get() = _properties

    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedProperty

    private val _navigateToSelectedProperty = MutableLiveData<Asteroid>()

    fun displayPropertyDetails(asteroid: Asteroid) {
        _navigateToSelectedProperty.value = asteroid
    }
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    init {
        getAsteroidProperties(AsteroidFilter.START_DATE)
    }

    private fun getAsteroidProperties(filter: AsteroidFilter) {
        viewModelScope.launch{
            _status.value=AsteroidApiStatus.LOADING
            try {
                _properties.value = AsteroidApi.retrofitService.getProperties(filter.startDate,apiKey="tenXmXVEEyCENwPLBWNBDc7XuJkz5JYEdvM3XdnJ")
                _status.value = AsteroidApiStatus.DONE
            }catch (e: Exception){
                _status.value=AsteroidApiStatus.ERROR
                _properties.value=ArrayList()
            }
        }
    }

    fun updateFilter(filter:AsteroidFilter){
        getAsteroidProperties(filter)
    }

}