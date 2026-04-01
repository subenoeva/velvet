package com.subenoeva.velvet.feature.detail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class DetailRoute(val movieId: Int) : NavKey
