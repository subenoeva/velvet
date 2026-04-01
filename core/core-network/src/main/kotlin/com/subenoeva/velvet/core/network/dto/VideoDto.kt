package com.subenoeva.velvet.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)

@Serializable
data class VideoResultsDto(
    val results: List<VideoDto> = emptyList()
)
