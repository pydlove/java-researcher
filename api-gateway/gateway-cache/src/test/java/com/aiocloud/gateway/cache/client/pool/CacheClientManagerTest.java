package com.aiocloud.gateway.cache.client.pool;

import com.aiocloud.gateway.cache.client.CacheClient;
import com.aiocloud.gateway.cache.conf.SystemProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CacheClientManagerTest {

    @Mock
    private CacheClientManager cacheClientManager;

    @Before
    public void setUp() throws Exception {
        cacheClientManager = CacheClientManager.getInstance();
    }

    @Test
    public void testSetCacheException() throws Exception {
        String key = "testKey";
        String value = "testValue";

        doThrow(new Exception("Test Exception")).when(cacheClientManager).setCache(key, value);

        cacheClientManager.setCache(key, value);

    }

    @Test
    public void testSetCacheSuccess() throws Exception {

        String key = "testKey";
        String value = "testValue";

        cacheClientManager.setCache(key, value);
    }

    @Test
    public void testGetCacheSuccess() throws Exception {

        String key = "testKey";
        String expectedValue = "testValue";

        String actualValue = String.valueOf(cacheClientManager.getCache(key));
        System.out.printf("actualValue: %s", actualValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetCacheNotFound() throws Exception {
        String key = "testKey";

        when(cacheClientManager.getCache(key)).thenReturn(null);
        String actualValue = String.valueOf(cacheClientManager.getCache(key));

        assertNull(actualValue);
    }

    @Test
    public void testGetCacheException() throws Exception {
        String key = "testKey";

        doThrow(new Exception("Test Exception")).when(cacheClientManager).getCache(key);
        String actualValue = String.valueOf(cacheClientManager.getCache(key));

        assertNull(actualValue);
    }
}