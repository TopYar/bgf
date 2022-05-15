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
import com.example.boardgamefinder.data.retrofit.DataException
import com.example.boardgamefinder.data.retrofit.UserRepository
import com.example.boardgamefinder.domain.models.*
import com.example.boardgamefinder.domain.usecase.ConfirmCodeUseCase
import com.example.boardgamefinder.domain.usecase.RecoverPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

internal class PasswordRecoveryViewModel(app: Application) : AndroidViewModel(app) {
    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun send(email: String){
        val useCase = RecoverPasswordUseCase(UserRepository())
        viewModelScope.launch {
            val result = useCase.execute(email)

            withContext(Dispatchers.Main) {
                if (result.isSuccess)
                    _success.value = true
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }
}
