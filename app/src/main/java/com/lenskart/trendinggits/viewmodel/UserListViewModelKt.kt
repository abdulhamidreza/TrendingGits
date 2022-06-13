package com.lenskart.trendinggits.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lenskart.trendinggits.repository.Repo
import com.lenskart.trendinggits.repository.UserRepositoryKt
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.Response

class UserListViewModelKt constructor(private val mainRepository: UserRepositoryKt) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val userKtList = MutableLiveData<List<Repo>>()
    val loadingStatus = MutableLiveData<Boolean>()
    var isListInitlized = false

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    var job: Job? = null

    fun intiUserList() {
        if (!isListInitlized) {
            getAllUsers()
        }
    }

    fun getAllUsers() {
        Log.d("*************", "getAllUsers Api hit")
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response: Response<String> = mainRepository.getAllUsers()

            var users: MutableList<Repo> = ArrayList<Repo>()
            var isParsed = true
            var errorMassage = ""

            //Todo Use Dispatchers.Default  with Async Coroutine
            if (response.body() != null) {
                var doc = Jsoup.parse(response.body())
                val allInfoList = doc.getElementsByClass("Box-row")

                for (rep in allInfoList) {
                    users.add(
                        Repo(
                            rep.getElementsByClass("col-9 color-fg-muted my-1 pr-4").text(),
                            rep.getElementsByClass("h3 lh-condensed").text().toString().replace(" ", ""),
                            rep.getElementsByClass("text-normal").text().toString().replace(" /",""),
                            rep.getElementsByClass("d-inline-block ml-0 mr-3").text(),
                            rep.getElementsByClass("d-inline-block float-sm-right").text(),
                            rep.getElementsByClass("Link--muted d-inline-block mr-3")[0].text(),
                            rep.getElementsByClass("Link--muted d-inline-block mr-3")[0].text()
                        )
                    )
                }

            } else {
                isParsed = false
                errorMassage = "Error : Body null "
            }

            withContext(Dispatchers.Main) {
                if (response.isSuccessful and isParsed) {
                    isListInitlized = true;
                    userKtList.postValue(users)
                    loadingStatus.postValue(true)
                } else {
                    onError("Error : ${response.message()} " + "Parse error " + errorMassage)
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loadingStatus.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}