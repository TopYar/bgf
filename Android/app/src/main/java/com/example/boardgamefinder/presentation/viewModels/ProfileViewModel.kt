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
import com.example.boardgamefinder.domain.models.User
import com.example.boardgamefinder.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Used for operations inside a profile tab
 */
internal class ProfileViewModel(app: Application) : AndroidViewModel(app) {
    private val _logOutSuccess = MutableLiveData<Boolean>()
    val logOutSuccess: LiveData<Boolean> = _logOutSuccess

    private val _subscription = MutableLiveData<Boolean>()
    val subscription: LiveData<Boolean> = _subscription

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private lateinit var mSettings: SharedPreferences

    fun logOut(){
        val useCase = LogOutUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt)

            withContext(Dispatchers.Main) {
                if (result.isSuccess)
                    logOutSuccess()
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun getProfile(userId: Int){
        val useCase = GetProfileUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, userId)

            withContext(Dispatchers.Main) {
                if (result.isSuccess)
                    _user.value = result.getOrNull()
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun subscribe(userId: Int){
        val useCase = SubscribeUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, userId)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _user.value?.subscribersCount = _user.value?.subscribersCount!! + 1
                    _subscription.value = true
                }
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun unsubscribe(userId: Int){
        val useCase = UnsubscribeUseCase(UserRepository())

        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, AppCompatActivity.MODE_PRIVATE)

        val jwt = mSettings.getString(MySettings.APP_PREFERENCES_TOKEN, "") ?: ""

        viewModelScope.launch {
            val result = useCase.execute(jwt, userId)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    _user.value?.subscribersCount = _user.value?.subscribersCount!! - 1
                    _subscription.value = false
                }
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun logOutSuccess(){
        // save jwt
        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mSettings.edit()
        editor.putString(MySettings.APP_PREFERENCES_TOKEN, "")
        editor.putString(MySettings.APP_PREFERENCES_REFRESH_TOKEN, "")
        editor.putBoolean(MySettings.APP_PREFERENCES_LOGGED_IN, false)
        editor.apply()

        _logOutSuccess.value = true
    }
}
