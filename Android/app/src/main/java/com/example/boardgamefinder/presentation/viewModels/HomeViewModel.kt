package com.example.boardgamefinder.presentation.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.core.toMessage
import com.example.boardgamefinder.data.retrofit.UserRepository
import com.example.boardgamefinder.domain.models.Event
import com.example.boardgamefinder.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    private val _updateRecycler = MutableLiveData<Int>()
    val updateRecycler: LiveData<Int> = _updateRecycler

    private lateinit var mSettings: SharedPreferences

    init {
        getEvents()
    }

    fun setLike(recyclerItemId: Int){
        if(_events.value == null)
            return

        val useCase = SetLikeUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, _events.value!![recyclerItemId].id)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _events.value!![recyclerItemId].liked = true
                    _events.value!![recyclerItemId].likes++
                    _updateRecycler.value = recyclerItemId
                }
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun removeLike(recyclerItemId: Int){
        if(_events.value == null)
            return

        val useCase = RemoveLikeUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, _events.value!![recyclerItemId].id)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _events.value!![recyclerItemId].liked = false
                    _events.value!![recyclerItemId].likes--
                    _updateRecycler.value = recyclerItemId
                }
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun joinEvent(recyclerItemId: Int){
        if(_events.value == null)
            return

        val useCase = JoinEventUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, _events.value!![recyclerItemId].id)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _events.value!![recyclerItemId].subscriptionStatus = result.getOrNull()!!
                    _updateRecycler.value = recyclerItemId
                }
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun leaveEvent(recyclerItemId: Int){
        if(_events.value == null)
            return

        val useCase = LeaveEventUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, _events.value!![recyclerItemId].id)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _events.value!![recyclerItemId].subscriptionStatus = Event.SubscriptionStatus.NOT_SUBMITTED
                    _updateRecycler.value = recyclerItemId
                }
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getEvents(){
        val useCase = GetEventsUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt)

            withContext(Dispatchers.Main) {
                if (result.isSuccess)
                    _events.value = result.getOrNull()
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }
}
