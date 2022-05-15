package com.example.boardgamefinder.presentation.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boardgamefinder.R
import com.example.boardgamefinder.core.MySettings
import com.example.boardgamefinder.core.toMessage
import com.example.boardgamefinder.data.retrofit.DataException
import com.example.boardgamefinder.data.retrofit.UserRepository
import com.example.boardgamefinder.domain.models.Email
import com.example.boardgamefinder.domain.models.Password
import com.example.boardgamefinder.domain.models.Tokens
import com.example.boardgamefinder.domain.usecase.LogInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class LogInViewModel(app: Application) : AndroidViewModel(app) {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private lateinit var mSettings: SharedPreferences

    fun login(email: Email, password: Password){
        val useCase = LogInUseCase(UserRepository())
        viewModelScope.launch {
            val result = useCase.execute(email, password)

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    loginSuccess(result.getOrNull()!!)
                }
                else if(result.exceptionOrNull() != null) {
                    _success.value = false
                    if(result.exceptionOrNull() is DataException.Response401)
                        Toast.makeText(getApplication(), (getApplication() as Context).getString(R.string.wrong_email_password), Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loginSuccess(tokens: Tokens){
        // save jwt
        mSettings = (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mSettings.edit()
        editor.putString(MySettings.APP_PREFERENCES_TOKEN, tokens.accessToken)
        editor.putString(MySettings.APP_PREFERENCES_REFRESH_TOKEN, tokens.refreshToken)
        editor.putBoolean(MySettings.APP_PREFERENCES_LOGGED_IN, true)
        editor.apply()

        _success.value = true
    }
}
