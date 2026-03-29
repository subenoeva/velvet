package com.subenoeva.velvet.core.domain.model

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
) {
    val isYouTubeTrailer get() = site == "YouTube" && type == "Trailer"
}
