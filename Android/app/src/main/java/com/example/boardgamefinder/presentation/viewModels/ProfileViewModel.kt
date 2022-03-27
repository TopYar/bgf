package com.example.boardgamefinder.presentation.viewModels

import com.example.boardgamefinder.core.toMessage
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.boardgamefinder.data.retrofit.UserRepository
import com.example.boardgamefinder.domain.usecase.GetBreedsUseCase
import kotlinx.coroutines.*

/**
 * Used for operations inside a profile tab
 */
internal class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    //ToDo remove example
    private val _breedsCount = MutableLiveData<Int>()
    val breedsCount: LiveData<Int> = _breedsCount

    // ToDo remove example
    fun getBreeds(){
        val useCase = GetBreedsUseCase(UserRepository())
        viewModelScope.launch {
            val result = useCase.execute()

            withContext(Dispatchers.Main) {
                if (result.isSuccess)
                    _breedsCount.value = result.getOrNull()?.size ?: 0
                else if(result.exceptionOrNull() != null)
                    Toast.makeText(getApplication(), (result.exceptionOrNull() as Exception).toMessage(getApplication()), Toast.LENGTH_LONG).show()
            }
        }
    }
}
