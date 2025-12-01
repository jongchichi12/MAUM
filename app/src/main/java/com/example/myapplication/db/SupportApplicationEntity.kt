package com.example.myapplication.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "support_applications")
data class SupportApplicationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val birthDate: String,
    val contact: String,
    val email: String,
    val adoptionTime: String?,
    val familyInfo: String,
    val createdAt: Long
)

fun SupportApplicationEntity.toModel(): SupportApplication =
    SupportApplication(
        id = id,
        name = name,
        birthDate = birthDate,
        contact = contact,
        email = email,
        adoptionTime = adoptionTime,
        familyInfo = familyInfo,
        createdAt = createdAt
    )

fun SupportApplication.toEntity(): SupportApplicationEntity =
    SupportApplicationEntity(
        id = id,
        name = name,
        birthDate = birthDate,
        contact = contact,
        email = email,
        adoptionTime = adoptionTime,
        familyInfo = familyInfo,
        createdAt = createdAt
    )
