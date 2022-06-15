package com.lenskart.trendinggits.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lenskart.trendinggits.data.Repo
import com.lenskart.trendinggits.repository.UserRepositoryKt
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import retrofit2.Response

class UserListViewModelKt constructor(private val mainRepository: UserRepositoryKt) : ViewModel() {
    val errorMessage = MutableLiveData<String>()
    val userKtList = MutableLiveData<List<Repo>>()
    val lastSearchedKey = MutableLiveData<String>()
    val loadingStatus = MutableLiveData<Boolean>()
    private var isListInitlized = false

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private var job: Job? = null

    fun intiUserList() {
        if (!isListInitlized) {
            getTrendingRepo()
        }
    }

    fun getLastSearched(): String? {
        return lastSearchedKey.value
    }

    private fun getTrendingRepo() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response: Response<String> = mainRepository.getAllUsers()

            val users: MutableList<Repo>
            val usersDef = async { fetchHtmlElements(response) }
            users = usersDef.await()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body() != null && users.isNotEmpty()) {
                    isListInitlized = true;
                    userKtList.postValue(users)
                    loadingStatus.postValue(true)
                }
            }
        }
    }

    private suspend fun fetchHtmlElements(response: Response<String>): MutableList<Repo> =
        withContext(Dispatchers.Default) {
            var users: MutableList<Repo> = ArrayList()
            try {
                if (!response.isSuccessful) {
                    onError("Error : In Response  " + "fetchHtmlElements error")
                    return@withContext users
                }

                if (response.body() == null) {
                    onError("Error :  Body null " + "fetchHtmlElements error ")
                    return@withContext users
                }
                val doc = Jsoup.parse(response.body())
                val allInfoList = doc.getElementsByClass("Box-row")
                for (rep in allInfoList) {
                    users.add(
                        Repo(
                            users.size,
                            rep.getElementsByClass("col-9 color-fg-muted my-1 pr-4").text(),
                            rep.getElementsByClass("h3 lh-condensed").text().toString()
                                .replace(" ", ""),
                            rep.getElementsByClass("text-normal").text().toString()
                                .replace(" /", ""),
                            rep.getElementsByClass("d-inline-block ml-0 mr-3").text(),
                            rep.getElementsByClass("d-inline-block float-sm-right").text(),
                            rep.getElementsByClass("Link--muted d-inline-block mr-3")[0].text(),
                            rep.getElementsByClass("Link--muted d-inline-block mr-3")[1].text(),
                            false
                        )
                    )
                }

                if (users.isEmpty()) {
                    onError("Error :  List is Empty " + "fetchHtmlElements error ")
                    return@withContext users
                }

            } catch (e: Exception) {
                onError("Error : ${e.message} " + "fetchHtmlElements error ")
            }
            users
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