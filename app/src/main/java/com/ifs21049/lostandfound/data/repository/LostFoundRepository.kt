package com.ifs21049.lostandfound.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.google.gson.Gson
import com.ifs21049.lostandfound.data.remote.MyResult
import com.ifs21049.lostandfound.data.remote.response.DelcomResponse
import com.ifs21049.lostandfound.data.remote.retrofit.IApiService
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import retrofit2.HttpException

class LostFoundRepository private constructor(
    private val apiService: IApiService,
) {
    fun postLostFound(
        title: String,
        description: String,
        status: String?
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.postLostFound(title, description, status).data
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }


    fun putLostFound(
        lostfoundId: Int,
        title: String,
        description: String,
        isCompleted: Int,
        status: String
    ): LiveData<MyResult<DelcomResponse>> {
        return flow {
            emit(MyResult.Loading)
            try {
                emit(
                    MyResult.Success(
                        apiService.putLostFound(
                            lostfoundId.toString(), // Ubah menjadi String
                            title,
                            description,
                            status,
                            isCompleted
                        )
                    )
                )
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                emit(
                    MyResult.Error(
                        Gson()
                            .fromJson(jsonInString, DelcomResponse::class.java)
                            .message
                    )
                )
            }
        }.asLiveData()
    }


    fun getLostFounds(
        isFinished: Int?,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.getLostFounds(
                        isCompleted = isFinished,
                        isMe = null,
                        status = null
                    )
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }

    fun getLostFound(
        lostfoundId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.getLostFound(lostfoundId.toString())
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }


    fun deleteLostFound(
        lostfoundId: Int,
    ) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            emit(
                MyResult.Success(
                    apiService.deleteLostFound(lostfoundId.toString())
                )
            )
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }

    fun postCoverLostFound(cover: MultipartBody.Part) = flow {
        emit(MyResult.Loading)
        try {
            //get success message
            val response = apiService.postCoverLostFound(cover)
            emit(MyResult.Success(response))
        } catch (e: HttpException) {
            //get error message
            val jsonInString = e.response()?.errorBody()?.string()
            emit(
                MyResult.Error(
                    Gson()
                        .fromJson(jsonInString, DelcomResponse::class.java)
                        .message
                )
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundRepository? = null
        fun getInstance(
            apiService: IApiService,
        ): LostFoundRepository {
            synchronized(LostFoundRepository::class.java) {
                INSTANCE = LostFoundRepository(
                    apiService
                )
            }
            return INSTANCE as LostFoundRepository
        }
    }
}
