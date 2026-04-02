package com.subenoeva.velvet.feature.home.category

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class CategoryRoute(val category: String, val title: String) : NavKey
