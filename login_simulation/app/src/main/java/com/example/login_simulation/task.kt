package com.example.login_simulation

import com.google.firebase.Timestamp

data class Task(
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val `due-to`: Timestamp? = null,
    val participants: List<String>? = null
)
