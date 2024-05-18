package com.example.skysight.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skysight.data.DataRepository
import com.example.skysight.data.db.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: DataRepository, val sharedPreferences: SharedPreferences): ViewModel() {

    var table= MutableStateFlow<List<WeatherData>>(emptyList())
    private var _uiState = MutableStateFlow(ui())
    val uiState = _uiState.asStateFlow()
    private val _weatherFetched = MutableStateFlow(false)
    val weatherFetched = _weatherFetched.asStateFlow()

    private val _liveWeatherFetched = MutableStateFlow(false)
    val liveWeatherFetched = _liveWeatherFetched.asStateFlow()

    private val _weatherForecastFetched = MutableStateFlow(false)
    val weatherForecastFetched = _weatherForecastFetched.asStateFlow()

    fun setLocationGps(context: Context) {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        val geocoder = Geocoder(context)
        var addresses: MutableList<Address>? = arrayListOf()
        viewModelScope.launch {
            try {
                getLastKnownLocation(fusedLocationClient) { location ->
                    if (location != null) {
                        // Location obtained, execute the callback
                        addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val address: Address? = addresses?.get(0)
                        if (address != null) {

                            _uiState.value = _uiState.value.copy(
                                currentLocation = address.locality,
                                gpsLocation = address.locality
                            )
                            getWeather(address.locality)
                            getLiveWeather(address.locality)
                            getWeatherForecast(address.locality)

                        }
                    } else {
                        // Unable to get last known location, attempt to get current location
                        getCurrentLocation(fusedLocationClient) { currentLocation ->
                            if (currentLocation != null) {
                                // Current location obtained, execute the callback
                                if (location != null) {
                                    addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                                }
                                val address: Address? = addresses?.get(0)
                                if (address != null) {
                                    _uiState.value = _uiState.value.copy(
                                        currentLocation = address.locality,
                                        gpsLocation = address.locality
                                    )
                                    Log.d("loc List",uiState.value.locationList.toString())
                                    getWeather(address.locality)
                                    getLiveWeather(address.locality)
                                    getWeatherForecast(address.locality)

                                }
                            } else {
                                // Unable to get current location as well
                                // You can handle this case as needed
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                throw(e)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(
        fusedLocationClient: FusedLocationProviderClient,
        onLocationResult: (Location?) -> Unit
    ) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                onLocationResult(location)
            }
            .addOnFailureListener { exception ->
                onLocationResult(null)
            }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient,
        onLocationResult: (Location?) -> Unit
    ) {
        try {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            val cancellationTokenSource = CancellationTokenSource()
            val locationTask: Task<Location> = fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            locationTask.addOnSuccessListener { location ->
                onLocationResult(location)
            }.addOnFailureListener {
                onLocationResult(null)
            }
        } catch (e: Exception) {
            onLocationResult(null)
        }

    }

    fun AddLocationText(loc: String) {
        getWeather(loc)
        getLiveWeather(loc)
        getWeatherForecast(loc)
        val savedLocations = sharedPreferences.getStringSet("locations", setOf()) ?: setOf()
        val updatedLocations = savedLocations.toMutableSet()
        updatedLocations.add(loc)
        sharedPreferences.edit().putStringSet("locations", updatedLocations).apply()
        _uiState.value = _uiState.value.copy(locationList = _uiState.value.locationList + loc)

    }

    fun deleteLocation(loc : String){
        val savedLocations = sharedPreferences.getStringSet("locations", setOf()) ?: setOf()

        // Create a new set by removing the specified location
        val updatedLocations = savedLocations.toMutableSet()
        updatedLocations.remove(loc)
        sharedPreferences.edit().putStringSet("locations", updatedLocations).apply()
        _uiState.value = _uiState.value.copy(locationList = _uiState.value.locationList - loc)
        if(_uiState.value.currentLocation==loc)
            _uiState.value = _uiState.value.copy(currentLocation = _uiState.value.gpsLocation)
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocation(loc)
        }

    }

    fun setLocationText(loc: String) {
            Log.d("setLocation",loc)
            _uiState.value = _uiState.value.copy(currentLocation = loc)
            getWeather(loc)
            getLiveWeather(loc)
            getWeatherForecast(loc)
    }

    fun setWeatherFetched(boolean: Boolean){
        _weatherFetched.value=boolean
    }
    fun setWLiveweatherFetched(boolean: Boolean){
        _liveWeatherFetched.value=boolean
    }
    fun setWeatherForcastFetched(boolean: Boolean){
        _weatherForecastFetched.value=boolean
    }
    fun getWeatherForLocation(location: String,callback: (WeatherData)->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchWeather(location) {
               if (it != null) {
                   callback(it)
               }
                _weatherFetched.value = true

            }

        }
    }


    fun getWeather(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchWeather(location) {
                Log.d("weatherViewModel",it.toString())
                _uiState.value = _uiState.value.copy(TodayData = it)
                _weatherFetched.value = true
            }
        }
    }

    fun getLiveWeather(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.liveWeather(location) {
                _uiState.value = _uiState.value.copy(LiveWeatherData = it)
                _liveWeatherFetched.value = true
            }
        }
    }

    private fun getWeatherForecast(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherForecast(location) {
                _uiState.value = _uiState.value.copy(weatherForecast = it)
                _weatherForecastFetched.value = true
            }
        }
    }

    fun showWeatherTable() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeatherTable {
                table.value = it
            }
        }
    }
    init {
        val savedLocations = sharedPreferences.getStringSet("locations", setOf()) ?: setOf()
        _uiState.value = _uiState.value.copy(locationList = savedLocations)
    }
}

