package com.example.to_do_app

import com.google.firebase.Timestamp
import java.util.Date

data class usermodel(
    val notetext: String? =null,
    val completed: Boolean = false,
    val timestamp: Timestamp,
    val userid: String? =null,
    var firestoreId: String
){
    // No-argument constructor
    constructor() : this("", false, Timestamp(Date()), "", "")
}
