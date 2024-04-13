package com.ifs21049.lostandfound.presentation.lostfound

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.ifs21049.lostandfound.data.remote.MyResult
import com.ifs21049.lostandfound.data.remote.response.DataAddLostFoundResponse
import com.ifs21049.lostandfound.data.remote.response.DelcomLostFoundResponse
import com.ifs21049.lostandfound.data.remote.response.DelcomResponse
import com.ifs21049.lostandfound.data.repository.LostFoundRepository
import com.ifs21049.lostandfound.presentation.ViewModelFactory

class LostFoundViewModel(
    private val lostfoundRepository: LostFoundRepository
) : ViewModel() {

    fun getLostFound(lostfoundId: Int): LiveData<MyResult<DelcomLostFoundResponse>>{
        return lostfoundRepository.getLostFound(lostfoundId).asLiveData()
    }

    fun postLostFound(
        title: String,
        description: String,
        status: String
    ): LiveData<MyResult<DataAddLostFoundResponse>>{
        return lostfoundRepository.postLostFound(
            title,
            description,
            status
        ).asLiveData()
    }

    fun putLostFound(
        lostfoundId: Int,
        title: String,
        description: String,
        isCompleted: Int,
        status: String
    ): LiveData<MyResult<DelcomResponse>> {
        val status = if (isCompleted == 1) "completed" else "incomplete" // Ubah status berdasarkan nilai isCompleted
        return lostfoundRepository.putLostFound(
            lostfoundId,
            title,
            description,
            isCompleted,
            status
        )
    }




    fun deleteLostFound(lostfoundId: Int): LiveData<MyResult<DelcomResponse>> {
        return lostfoundRepository.deleteLostFound(lostfoundId).asLiveData()
    }

    companion object {
        @Volatile
        private var INSTANCE: LostFoundViewModel? = null
        fun getInstance(
            lostfoundRepository: LostFoundRepository
        ): LostFoundViewModel {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = LostFoundViewModel(
                    lostfoundRepository
                )
            }
            return INSTANCE as LostFoundViewModel
        }
    }
}