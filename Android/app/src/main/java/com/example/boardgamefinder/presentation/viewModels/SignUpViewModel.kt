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
import com.example.boardgamefinder.domain.models.UserCredentials
import com.example.boardgamefinder.domain.usecase.SignUpUseCase
import com.example.boardgamefinder.utils.CredentialsValidatorImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal class SignUpViewModel(app: Application) : AndroidViewModel(app) {

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun register(email: Email, password: Password, confirmPassword: Password){
        val useCase = SignUpUseCase(UserRepository(), CredentialsValidatorImpl)
        viewModelScope.launch {
            val result = useCase.execute(UserCredentials(email, password, confirmPassword))

            withContext(Dispatchers.Main) {
                if (result.isSuccess) {
                    registrationSuccess(result.getOrNull().toString())
                }
                else if(result.exceptionOrNull() != null) {
                    _success.value = false
                    if(result.exceptionOrNull() is DataException.Response422)
                        Toast.makeText(getApplication(), (getApplication() as Context).getString(R.string.user_already_exists), Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun registrationSuccess(jwt: String){
        // save jwt
        val mSettings: SharedPreferences =
            (getApplication() as Context).getSharedPreferences(MySettings.APP_PREFERENCES, Context.MODE_PRIVATE)
        val editor = mSettings.edit()
        editor.putString(MySettings.APP_PREFERENCES_TOKEN, jwt)

        // past registration, waiting for code
        editor.putBoolean(MySettings.APP_PREFERENCES_CODE_STAGE, true)
        editor.apply()

        _success.value = true
    }
}
