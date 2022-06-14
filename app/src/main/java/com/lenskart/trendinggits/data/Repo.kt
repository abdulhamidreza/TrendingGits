package com.lenskart.trendinggits.data

data class Repo(
    val itemId : Int,
    var repoName:String,
    var repoLink:String,
    var ownerAvatar: String,
    var language: String,
    var todayStars: String,
    var totalStars: String,
    var totalForks: String,
    var isSelected: Boolean
)
