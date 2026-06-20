package com.example.homeworkone.models

data class ScoreRecord(
    val name: String,
    val score: Int,
    val lat: Double = 0.0,
    val lon: Double = 0.0
)

data class ScoreList(
    val scores: MutableList<ScoreRecord> = mutableListOf()
)