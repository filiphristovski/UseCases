package com.zeyad.usecases.api

import android.app.Application
import android.content.Context
import android.support.test.rule.BuildConfig
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author by ZIaDo on 5/14/17.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = [25])
class DataServiceFactoryTest {
    private val URL = "https://api.github.com/"
    private val cacheSize = 8192
    private lateinit var mDataServiceConfig: DataServiceConfig
    private lateinit var mockContext: Context
    private lateinit var builder: OkHttpClient.Builder
    private lateinit var cache: Cache

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mockContext = mock(Application::class.java)
        builder = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
        cache = Cache(File("/data/data/com/zeyad/usecases/cache/", "http-cache"),
                (10 * 1024 * 1024).toLong())
        mDataServiceConfig = DataServiceConfig.Builder(mockContext)
                .baseUrl(URL)
                .cacheSize(cacheSize)
                .okHttpBuilder(builder)
                .okhttpCache(cache)
                .withRealm()
                .build()
    }

    @Test
    @Throws(Exception::class)
    fun init() {
        DataServiceFactory(mDataServiceConfig)
        val instance = DataServiceFactory.dataService
        assertNotNull(com.zeyad.usecases.Config.apiConnection)
        assertNotNull(com.zeyad.usecases.Config.backgroundThread)
        assertNotNull(com.zeyad.usecases.Config.gson)
        assertNotNull(instance)
        assertEquals(com.zeyad.usecases.Config.baseURL, URL)
    }
}
