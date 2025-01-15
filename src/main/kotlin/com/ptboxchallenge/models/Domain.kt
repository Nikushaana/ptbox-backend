package com.ptboxchallenge.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Domain(
    val id: Int,
    var name: String,
    var ips: List<String> = emptyList(),
    var emails: List<String> = emptyList(),
    var hosts: List<String> = emptyList(),
    var startTime: String,
    var endTime: String,
)
