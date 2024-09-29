package com.ijonsabae.presentation.login

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.RegisterParam
import com.ijonsabae.domain.usecase.login.RegisterUseCase
import com.ijonsabae.domain.usecase.login.RequestEmailAuthCodeUseCase
import com.ijonsabae.domain.usecase.login.VerifyEmailAuthCodeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val requestEmailAuthCodeUseCase: RequestEmailAuthCodeUseCase,
    private val verifyEmailAuthCodeUseCase:VerifyEmailAuthCodeUseCase
) : ViewModel() {
    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name: StateFlow<String>
        get() = _name

    suspend fun setName(name: String) {
        _name.emit(name)
    }

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String>
        get() = _email

    suspend fun setEmail(email: String) {
        _email.emit(email)
    }

    private val _authCode: MutableStateFlow<String> = MutableStateFlow("")
    val authCode: StateFlow<String>
        get() = _authCode

    suspend fun setAuthCode(code: String) {
        _authCode.emit(code)
    }

    suspend fun requestAuthCode(): Result<CommonResponse<Unit>>{
        return requestEmailAuthCodeUseCase(email.value)
    }

    suspend fun verifyAuthCode(): Result<CommonResponse<Boolean>>{
        return verifyEmailAuthCodeUseCase(email.value, authCode.value)
    }

    private val _authChecked: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authChecked: StateFlow<Boolean>
        get() = _authChecked

    suspend fun setAuthCheckedStatus(authChecked: Boolean) {
        _authChecked.emit(authChecked)
    }

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String>
        get() = _password

    suspend fun setPassword(password: String) {
        _password.emit(password)
    }

    private val _passwordRepeat: MutableStateFlow<String> = MutableStateFlow("")
    val passwordRepeat: StateFlow<String>
        get() = _passwordRepeat

    suspend fun setPasswordRepeat(password: String) {
        _passwordRepeat.emit(password)
    }

    private val _policy1Confirmed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val policy1Confirmed: StateFlow<Boolean>
        get() = _policy1Confirmed

    suspend fun setPolicy1ConfirmStatus(confirm: Boolean) {
        _policy1Confirmed.emit(confirm)
    }

    private val _policy2Confirmed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val policy2Confirmed: StateFlow<Boolean>
        get() = _policy2Confirmed

    suspend fun setPolicy2ConfirmStatus(confirm: Boolean) {
        _policy2Confirmed.emit(confirm)
    }

    private val _validation: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val validation: SharedFlow<Boolean>
        get() = _validation

    suspend fun setValidation(validation: Boolean) {
        _validation.emit(validation)
    }

    private val _joinCompleted: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val joinCompleted: SharedFlow<Boolean>
        get() = _joinCompleted

    suspend fun setJoinCompletedStatus(joinCompletedStatus: Boolean) {
        _joinCompleted.emit(joinCompletedStatus)
    }

    suspend fun join() : Result<CommonResponse<Unit>> {
        return registerUseCase(
            RegisterParam(
                name = name.value,
                email = email.value,
                password = password.value
            )
        )
    }

//    suspend fun setToken(token: Token) {
//        _token.emit(token)
//        setLocalTokenUseCase(token)
//    }


}