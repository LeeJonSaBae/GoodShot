package com.ijonsabae.presentation.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.LoginParam
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.usecase.login.LoginUseCase
import com.ijonsabae.domain.usecase.login.SetAutoLoginStatusUseCase
import com.ijonsabae.domain.usecase.login.SetLocalTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

private const val TAG = "LoginDialogViewModel 싸피"
@HiltViewModel
class LoginDialogViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setLocalTokenUseCase: SetLocalTokenUseCase,
    private val setAutoLoginStatusUseCase: SetAutoLoginStatusUseCase
): ViewModel() {
    private val _token: MutableSharedFlow<Token> = MutableSharedFlow()
    val token: SharedFlow<Token>
        get() = _token

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    private val password: StateFlow<String> = _password
    suspend fun setPassword(password: String){
        _password.emit(password)
    }

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    private val email: StateFlow<String> = _email
    suspend fun setEmail(email: String){
        _email.emit(email)
    }

    fun checkValidation(): Boolean = email.value.isNotBlank() && password.value.isNotBlank()

    suspend fun login() = loginUseCase(LoginParam(email.value, password.value))

    suspend fun setToken(token: Token) {
        _token.emit(token)
    }

    suspend fun saveToken(token: Token){
        setLocalTokenUseCase(token)
    }

    suspend fun setAutoLoginStatus(boolean: Boolean){
        setAutoLoginStatusUseCase(boolean)
    }

}