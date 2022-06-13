package com.lenskart.trendinggits.repository

import com.lenskart.trendinggits.network.RetrofitServiceKt

class UserRepositoryKt constructor(private val retrofitService: RetrofitServiceKt) {

    suspend fun getAllUsers() = retrofitService.getUserListData()


}