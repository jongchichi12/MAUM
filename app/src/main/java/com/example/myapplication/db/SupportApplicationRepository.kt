package com.example.myapplication.db

/**
 * Thin repository layer to keep DB access centralized for future expansion.
 */
class SupportApplicationRepository(
    private val dao: SupportApplicationDao
 ) {

    suspend fun submit(application: SupportApplication): Result<Long> =
        runCatching { dao.upsert(application.toEntity()) }

    suspend fun latest(): Result<SupportApplication?> =
        runCatching { dao.getLatest()?.toModel() }

    suspend fun getAll(): Result<List<SupportApplication>> =
        runCatching { dao.getAll().map { it.toModel() } }
}
