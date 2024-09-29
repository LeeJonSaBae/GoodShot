package com.ijonsabae.domain.model

class RetrofitException(override val message: String, val code: Int, throwable: Throwable) : Exception(message, throwable)