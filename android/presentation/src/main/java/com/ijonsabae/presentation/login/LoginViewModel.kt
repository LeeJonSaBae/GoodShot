package com.ijonsabae.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ijonsabae.domain.model.Token
import com.ijonsabae.domain.usecase.login.GetLocalAccessTokenUseCase
import com.ijonsabae.domain.usecase.login.GetLocalRefreshTokenUseCase
import com.ijonsabae.domain.usecase.login.SetLocalTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getLocalAccessTokenUseCase: GetLocalAccessTokenUseCase,
    private val getLocalRefreshTokenUseCase: GetLocalRefreshTokenUseCase,
    private val setLocalTokenUseCase: SetLocalTokenUseCase,
) : ViewModel() {
    private val _token: MutableSharedFlow<Token> = MutableSharedFlow()
    val token: SharedFlow<Token>
        get() = _token

    init {
        viewModelScope.launch {
            val accessToken = getLocalAccessTokenUseCase()
            val refreshToken = getLocalRefreshTokenUseCase()
            if (accessToken != null && refreshToken != null) {
                setToken(Token(accessToken, refreshToken))
            } else {
                setToken(Token("", ""))
            }
        }
    }

    suspend fun setToken(token: Token) {
        _token.emit(token)
    }

    suspend fun saveToken(token: Token){
        setLocalTokenUseCase(token)
    }


}