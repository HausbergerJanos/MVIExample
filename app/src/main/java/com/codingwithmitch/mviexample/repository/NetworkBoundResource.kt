package com.codingwithmitch.mviexample.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.codingwithmitch.mviexample.ui.main.state.MainViewState
import com.codingwithmitch.mviexample.util.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class NetworkBoundResource<ResponseObject, ViewStateType> {

    protected val result = MediatorLiveData<DataState<ViewStateType>>()

    init {

        result.value = DataState.loading(true)

        GlobalScope.launch(IO) {

                delay(Constants.TESTING_NETWORK_DELAY)

                withContext(Main) {

                    val apiResponse =  createCall()
                    result.addSource(apiResponse) { response ->
                        result.removeSource(apiResponse)
                        
                        handleNetworkCall(response)
                    }
                }
        }
    }

    private fun handleNetworkCall(response: GenericApiResponse<ResponseObject>?) {
        when (response) {
            is ApiSuccessResponse -> {
                handleApiSuccessResponse(response)
            }

            is ApiErrorResponse -> {
                Log.d("DEBUG-->", "NetworkBoundResource: ${response.errorMessage}")
                onReturnError(response.errorMessage)
            }

            is ApiEmptyResponse -> {
                Log.d("DEBUG-->", "NetworkBoundResource: HTTP 204. Returned NOTHING!")
                onReturnError("HTTP 204. Returned NOTHING!")
            }
        }
    }

    fun onReturnError(message: String) {
        result.value = DataState.error(message)
    }

    abstract fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

    fun asLiveData() = result as LiveData<DataState<ViewStateType>>
}