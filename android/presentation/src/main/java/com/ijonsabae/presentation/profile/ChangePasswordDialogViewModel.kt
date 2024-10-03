package com.ijonsabae.presentation.profile

import androidx.lifecycle.ViewModel
import com.ijonsabae.domain.usecase.profile.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChangePasswordDialogViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
): ViewModel() {
    private val _oldPassword: MutableStateFlow<String> = MutableStateFlow("")
    private val oldPassword: StateFlow<String> = _oldPassword
    suspend fun setOldPassword(oldPassword: String){
        _oldPassword.emit(oldPassword)
    }

    private val _newPasswordRepeat: MutableStateFlow<String> = MutableStateFlow("")
    private val newPasswordRepeat: StateFlow<String> = _newPasswordRepeat
    suspend fun setNewPasswordRepeat(oldPassword: String){
        _newPasswordRepeat.emit(oldPassword)
    }

    private val _newPassword: MutableStateFlow<String> = MutableStateFlow("")
    private val newPassword: StateFlow<String> = _newPassword
    suspend fun setNewPassword(newPassword: String){
        _newPassword.emit(newPassword)
    }

    fun checkValidation(): Boolean = oldPassword.value.isNotBlank() && newPasswordRepeat.value.isNotBlank() && newPassword.value.isNotBlank() && newPassword.value == newPasswordRepeat.value

    suspend fun changePassword() = changePasswordUseCase(oldPassword.value, newPassword.value)

}