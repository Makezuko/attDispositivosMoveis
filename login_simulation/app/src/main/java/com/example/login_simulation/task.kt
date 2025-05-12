package com.example.login_simulation

data class Task(
    var id: String = "",
    var title: String = "",
    var description: String? = "descrição",
    var status: String = "",
    var createdBy: String? = null,
    var modifiedBy: String? = null,
    var parentId: String? = null
)
