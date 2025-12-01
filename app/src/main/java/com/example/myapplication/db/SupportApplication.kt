package com.example.myapplication.db

/**
 * Domain model for a single support application entry.
 * Separated from Room entities to make future schema changes easier.
 */
data class SupportApplication(
    val id: Long = 0L,
    val name: String,
    val birthDate: String,
    val contact: String,
    val email: String,
    val adoptionTime: String?,
    val familyInfo: String,
    val createdAt: Long = System.currentTimeMillis()
)
