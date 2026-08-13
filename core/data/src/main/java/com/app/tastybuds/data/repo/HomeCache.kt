package com.app.tastybuds.data.repo

import com.app.tastybuds.data.local.CachedResponseDao
import com.app.tastybuds.data.local.CachedResponseEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generic "cache & serve stale" store: serializes a list of domain objects to JSON keyed by a
 * string, so the repository can return the last-known data when the network is unavailable.
 * Reusable for any feature — Home is the first consumer.
 */
@Singleton
class HomeCache @Inject constructor(
    private val dao: CachedResponseDao,
    private val moshi: Moshi
) {
    suspend fun <T> saveList(key: String, value: List<T>, elementType: Class<T>) {
        val adapter = moshi.adapter<List<T>>(Types.newParameterizedType(List::class.java, elementType))
        dao.put(CachedResponseEntity(key, adapter.toJson(value), System.currentTimeMillis()))
    }

    suspend fun <T> loadList(key: String, elementType: Class<T>): List<T>? {
        val json = dao.get(key)?.json ?: return null
        val adapter = moshi.adapter<List<T>>(Types.newParameterizedType(List::class.java, elementType))
        return runCatching { adapter.fromJson(json) }.getOrNull()
    }
}
