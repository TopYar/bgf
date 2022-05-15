package com.example.boardgamefinder.presentation.viewModels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.core.toMessage
import com.example.boardgamefinder.data.retrofit.DataException
import com.example.boardgamefinder.data.retrofit.UserRepository
import com.example.boardgamefinder.domain.models.CreatingEvent
import com.example.boardgamefinder.domain.usecase.CreateEventUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

internal class NewEventViewModel(app: Application) : AndroidViewModel(app) {
    private val _created = MutableLiveData<Boolean>()
    val created: LiveData<Boolean> = _created

    fun createEvent(event: CreatingEvent){
        val sp = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)
        val jwt = sp.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        val useCase = CreateEventUseCase(UserRepository())
        viewModelScope.launch {
            val result = useCase.execute(jwt, event)

            withContext(Dispatchers.Main) {
                if (result.isSuccess)
                    _created.value = true
                else if(result.exceptionOrNull() != null) {
                    _created.value = false
                    if(result.exceptionOrNull() is DataException.Response422)
                        Toast.makeText(getApplication(), "Wrong data", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ToDo move to utils
    fun validateCoordinates(coordinates: String): Boolean {
        val decimalPattern = "([0-9]*)\\.([0-9]*)"
        val coos = coordinates.split(", ").toTypedArray()
        return coos.size == 2 &&
                Pattern.matches(decimalPattern, coos[0]) &&
                Pattern.matches(decimalPattern, coos[1]) &&
                coos[0].toDouble() <= 90 &&
                coos[0].toDouble() >= -90 &&
                coos[1].toDouble() <= 180 &&
                coos[1].toDouble() >= -180
    }
}