package com.lenskart.trendinggits.repository

data class Repo(
    var repoName:String,
    var repoLink:String,
    var ownerAvatar: String,
    var language: String,
    var todayStars: String,
    var totalStars: String,
    var totalForks: String,
)
