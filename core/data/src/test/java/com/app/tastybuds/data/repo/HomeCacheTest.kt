package com.app.tastybuds.data.repo

import com.app.tastybuds.data.local.CachedResponseDao
import com.app.tastybuds.data.local.CachedResponseEntity
import com.app.tastybuds.domain.model.Banner
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class HomeCacheTest {

    private lateinit var dao: CachedResponseDao
    private lateinit var cache: HomeCache
    private val stored = mutableMapOf<String, CachedResponseEntity>()

    @Before
    fun setUp() {
        dao = mockk()
        coEvery { dao.put(any()) } answers {
            val e = firstArg<CachedResponseEntity>(); stored[e.key] = e
        }
        coEvery { dao.get(any()) } answers { stored[firstArg()] }
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        cache = HomeCache(dao, moshi)
    }

    @Test
    fun `domain list round-trips through JSON (reflective Moshi works on domain models)`() = runTest {
        cache.saveList("banners", listOf(Banner(id = "b1"), Banner(id = "b2")), Banner::class.java)

        val loaded = cache.loadList("banners", Banner::class.java)

        assertEquals(listOf("b1", "b2"), loaded!!.map { it.id })
    }

    @Test
    fun `loadList returns null when nothing is cached`() = runTest {
        coEvery { dao.get(any()) } returns null
        assertNull(cache.loadList("missing", Banner::class.java))
    }
}
