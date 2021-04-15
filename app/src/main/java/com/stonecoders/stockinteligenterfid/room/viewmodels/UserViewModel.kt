package com.stonecoders.stockinteligenterfid.room.viewmodels

import androidx.lifecycle.*
import com.stonecoders.stockinteligenterfid.entities.LogInResult
import com.stonecoders.stockinteligenterfid.entities.User
import com.stonecoders.stockinteligenterfid.entities.UserValidationResponse
import com.stonecoders.stockinteligenterfid.room.repositories.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val allUsers = repository.allUsers.asLiveData()
    var logInResult: MutableLiveData<LogInResult> = MutableLiveData()
    fun insert(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    val currentUser: MutableLiveData<UserValidationResponse> = MutableLiveData(null)

    fun validateUser(name: String, pwd: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.validateUser(name, pwd)
            if (result.isSuccessful) {
                if (name == result.body()?.usuario && pwd == result.body()?.clave) {
                    logInResult.postValue(LogInResult.SUCCESS)
                    currentUser.postValue(result.body())
                }
            } else {
                logInResult.postValue(LogInResult.FAILURE)
            }
        }
    }


}

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknownv view model class")
    }
}