package com.zeyad.usecases.integration;

import android.support.test.rule.BuildConfig;

import com.zeyad.usecases.api.DataServiceConfig;
import com.zeyad.usecases.api.DataServiceFactory;
import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.mapper.DAOMapper;
import com.zeyad.usecases.network.ApiConnection;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.stores.CloudStore;
import com.zeyad.usecases.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.net.HttpURLConnection;

import io.appflate.restmock.RESTMockServer;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static com.zeyad.usecases.stores.CloudStore.APPLICATION_JSON;
import static io.appflate.restmock.utils.RequestMatchers.pathContains;
import static junit.framework.Assert.assertEquals;

/**
 * @author by ZIaDo on 6/17/17.
 */
//@RunWith(RobolectricTestRunner.class)
@RunWith(AndroidRobolectricRunner.class)
@Config(constants = BuildConfig.class, sdk = 25, application = TestApplication.class)
public class APIIntegrationTest {
    private final static String userResponse = "\"{\\n  \\\"login\\\": \\\"Zeyad-37\\\",\\n  \\\"id\\\": 5938141,\\n  \\\"avatar_url\\\": \\\"https://avatars2.githubusercontent.com/u/5938141?v=3\\\",\\n}\"";
    private IDataService dataService;
    private String url;
    private MockWebServer mockWebServer;
    private CloudStore cloudStore;

    @Before
    public void setUp() throws IOException {
//        RESTMockServer.reset();
//        mockWebServer = new MockWebServer();
//        mockWebServer.start();

        // Be sure to reset the server before each test
//        RESTMockServerStarter.startSync(new JVMFileParser());
        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(RESTMockServer.getUrl())
//                .baseUrl(mockWebServer.url("/").toString())
                .baseUrl("https://api.github.com/")
//                .okHttpBuilder(new OkHttpClient.Builder()
//                        .addNetworkInterceptor(provideMockInterceptor())
//                        .addInterceptor(provideMockInterceptor()))
//                .withCache(3, TimeUnit.MINUTES)
                .withRealm()
                .build());
        cloudStore = new CloudStore(new ApiConnection(ApiConnection.init(null),
                ApiConnection.initWithCache(null, null)), null, new DAOMapper(), null, Utils.getInstance());
//        dataService = DataServiceFactory.getInstance();
    }

    @Test
    public void testValidUser() throws Exception {
        RESTMockServer.reset();

//        RESTMockServerStarter.startSync(new JVMFileParser());

//        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(RESTMockServer.getUrl())
////                .withCache(3, TimeUnit.MINUTES)
//                .withRealm()
//                .build());
        dataService = DataServiceFactory.getInstance();

        String path = String.format("users/%s", "Zeyad-37");
        RESTMockServer.whenGET(pathContains(path))
                .thenReturnString(200, userResponse);

        User testUser = new User();
        testUser.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
        testUser.setId(5938141);
        testUser.setLogin("Zeyad-37");

        GetRequest getRequest = new GetRequest.Builder(User.class, true)
                .url(path)
                .id("Zeyad-37", User.LOGIN, String.class)
                .build();
        TestSubscriber<User> testSubscriber = new TestSubscriber<>();
//        dataService.<User>getObject(getRequest)
//                .subscribe(testSubscriber);
        cloudStore.<User>dynamicGetObject(getRequest.getUrl(), getRequest.getIdColumnName(),
                getRequest.getItemId(), getRequest.getIdType(), getRequest.getDataClass(),
                getRequest.isPersist(), getRequest.isShouldCache())
                .subscribe(testSubscriber);

//        RequestsVerifier.verifyGET(pathContains(path)).invoked();

//        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(testUser)
                .assertComplete();
        assertEquals(1, testSubscriber.valueCount());
        assertEquals(testUser, testSubscriber.values().get(0));
    }

    @Test
    public void testValidUser2() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
//
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(userResponse));
//
//        DataServiceFactory.init(new DataServiceConfig.Builder(RuntimeEnvironment.application)
//                .baseUrl(mockWebServer.url("/").toString())
////                .withCache(3, TimeUnit.MINUTES)
////                .withRealm()
//                .build());
        dataService = DataServiceFactory.getInstance();
//
        User testUser = new User();
        testUser.setAvatarUrl("https://avatars2.githubusercontent.com/u/5938141?v=3");
        testUser.setId(5938141);
        testUser.setLogin("Zeyad-37");

        TestSubscriber<User> testSubscriber = new TestSubscriber<>();
        dataService.<User>getObject(new GetRequest.Builder(User.class, false)
                .url(String.format("users/%s", "Zeyad-37"))
                .id("Zeyad-37", User.LOGIN, String.class)
//                .cache(User.LOGIN)
                .build());
//                .subscribe(testSubscriber);
//
//        RecordedRequest request = mockWebServer.takeRequest();
//        assertEquals("/Zeyad-37", request.getPath());
//        assertEquals("GET", request.getMethod());
//
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertSubscribed()
                .assertNoErrors()
                .assertValueCount(1)
                .assertValue(testUser)
                .assertComplete();
        mockWebServer.shutdown();
    }

    public Interceptor provideMockInterceptor() {
        return chain -> {
            final String url = chain.request().url().toString();
            if (url.endsWith("Zeyad-37")) {
                final ResponseBody responseBody = ResponseBody
                        .create(MediaType.parse(APPLICATION_JSON), userResponse);
                return new Response.Builder()
                        .body(responseBody)
                        .request(chain.request()).message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .build();
            }
            return new Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(404)
                    .build();
        };
    }

//    public MockRetrofit provideMockRetrofit(Retrofit retrofit) {
//        final NetworkBehavior behavior = NetworkBehavior.create();
//        behavior.setErrorPercent(50);
//        behavior.setDelay(4, TimeUnit.SECONDS);
//        behavior.setVariancePercent(10);
//        final ExecutorService executor = Executors.newSingleThreadExecutor();
//        return new MockRetrofit.Builder(retrofit).backgroundExecutor(executor).networkBehavior(behavior).build();
//    }
}
