package com.ijonsabae.data.exception

import com.ijonsabae.domain.model.CommonResponse
import com.ijonsabae.domain.model.RetrofitException
import okhttp3.Request
import okio.FileNotFoundException
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

class YoutubeCall<T>(private val delegate: Call<T>, private val retrofit: Retrofit) :
    Call<Result<T>> {
    override fun enqueue(callback: Callback<Result<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                // 서버에서 응답은 받아왔을 때
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        callback.onResponse(
                            this@YoutubeCall,
                            Response.success(
                                response.code(),
                                Result.success(response.body()!!)
                            )
                        )
                    }
                    // 응답은 왔지만 400번 등 에러가 발생했을 때
                    else {
                        if (response.errorBody() == null) {
                            callback.onResponse(
                                this@YoutubeCall,
                                Response.success(
                                    Result.failure(
                                        RetrofitException(
                                            "에러 메세지가 없습니다",
                                            404,
                                            HttpException(response)
                                        )
                                    )
                                )
                            )
                        } else {
                            val message: String = response.errorBody()!!.string() ?: "에러 메세지가 없습니다!"
                            val code: Int = response.code() ?: 404
                            callback.onResponse(
                                this@YoutubeCall,
                                Response.success(
                                    Result.failure(
                                        RetrofitException(
                                            message,
                                            code,
                                            HttpException(response)
                                        )
                                    )
                                )
                            )
                        }
                    }
                }

                // 서버에서 응답도 받아오지 못했을 때
                override fun onFailure(call: Call<T>, t: Throwable) {
                    val errorMessage = when (t) {
                        is FileNotFoundException -> "파일을 찾을 수 없습니다"
                        is IOException -> "인터넷이 연결되어 있지 않습니다"
                        is HttpException -> "알 수 없는 에러가 발생했습니다!"
                        else -> t.localizedMessage
                    }
                    callback.onResponse(
                        this@YoutubeCall,
                        Response.success(Result.failure((RuntimeException(errorMessage, t))))
                    )
                }

            }
        )
    }

    override fun clone(): Call<Result<T>> {
        return YoutubeCall(delegate.clone(), retrofit)
    }

    override fun execute(): Response<Result<T>> {
        return Response.success(Result.success(delegate.execute().body()!!))
    }

    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}