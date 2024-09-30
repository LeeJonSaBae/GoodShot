package com.ijonsabae.presentation.login

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.usecase.login.GenerateTemporaryPassWordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FindPasswordViewModel @Inject constructor(
    private val generateTemporaryPassWordUseCase: GenerateTemporaryPassWordUseCase
) : ViewModel(){
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

    suspend fun generateTemporaryPassword(): Result<CommonResponse<Unit>>{
        return generateTemporaryPassWordUseCase(name = name.value, email = email.value)
    }
}