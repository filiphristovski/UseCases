package com.zeyad.usecases.db;

import android.support.test.rule.BuildConfig;

import com.google.gson.Gson;
import com.zeyad.usecases.TestRealmModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.subscribers.TestSubscriber;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static junit.framework.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * @author by ZIaDo on 2/15/17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@PrepareForTest({Realm.class, RealmQuery.class, RealmResults.class})
public class RealmManagerTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private RealmManager mRealmManager;

    public Realm mockRealm() {
        PowerMockito.mockStatic(Realm.class);
        Realm mockRealm = mock(Realm.class);
        RealmConfiguration realmConfiguration = mock(RealmConfiguration.class);
        RealmQuery<TestRealmModel> realmQuery = mock(RealmQuery.class);
        RealmResults<TestRealmModel> realmResults = mock(RealmResults.class);
        realmResults.addAll(Collections.singleton(new TestRealmModel()));
        Flowable flowable = Flowable.just(realmResults);

        PowerMockito.when(realmResults.isLoaded()).thenReturn(true);
        PowerMockito.when(mockRealm.where(TestRealmModel.class)).thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1L))
                .thenReturn(realmQuery);
        TestRealmModel value = new TestRealmModel();
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", 1L).findFirst())
                .thenReturn(value);

        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", new Long(1)))
                .thenReturn(realmQuery);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).equalTo("id", new Long(1))
                .findFirst())
                .thenReturn(value);

        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll()).thenReturn(realmResults);
        PowerMockito.when(mockRealm.where(TestRealmModel.class).findAll().asFlowable())
                .thenReturn(flowable);
        PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mockRealm);
        PowerMockito.when(mockRealm.getConfiguration()).thenReturn(realmConfiguration);
        PowerMockito.when(Realm.getInstance(realmConfiguration)).thenReturn(mockRealm);
        PowerMockito.when(mockRealm.copyFromRealm(value)).thenReturn(value);
        return mockRealm;
    }

    @Before
    public void before() {
        mockRealm();
        mRealmManager = new RealmManager();
    }

    @Test
    public void getById() {
        Flowable flowable = mRealmManager.getById("id", 1L, long.class, TestRealmModel.class);

        applyTestSubscriber(flowable);

        assertEquals(flowable.firstElement().blockingGet().getClass(), TestRealmModel.class);
    }

    @Test
    public void getAll() {
        Flowable flowable = mRealmManager.getAll(TestRealmModel.class);

        TestSubscriber testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);
        testSubscriber.assertSubscribed();
        testSubscriber.assertErrorMessage("TestRealmModel(s) were not found!");
    }

    private void applyTestSubscriber(Flowable flowable) {
        TestSubscriber testSubscriber = new TestSubscriber<>();
        flowable.subscribe(testSubscriber);
        testSubscriber.assertNoErrors();
        testSubscriber.assertSubscribed();
        testSubscriber.assertComplete();
    }

    @Test
    public void getQuery() {
        Flowable flowable = mRealmManager.getQuery(realm -> realm.where(TestRealmModel.class));

        applyTestSubscriber(flowable);

        assertEquals(LinkedList.class, flowable.firstElement().blockingGet().getClass());
    }

    @Test
    public void putJSONObject() throws Exception {
        Single<Boolean> completable = mRealmManager.put(new JSONObject(new Gson()
                .toJson(new TestRealmModel())), "id", int.class, TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    private void applyTestSubscriber(Single<Boolean> single) {
        TestObserver<Boolean> testSubscriber = new TestObserver<>();
        single.subscribe(testSubscriber);
        testSubscriber.assertComplete();
    }

    @Test
    public void putAllJSONArray() {
        Single<Boolean> completable = mRealmManager.putAll(new JSONArray(), "id", int.class,
                TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void putAllRealmObject() {
        Single<Boolean> completable = mRealmManager.putAll(new ArrayList<>(), TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictAll() {
        Single<Boolean> completable = mRealmManager.evictAll(TestRealmModel.class);
        applyTestSubscriber(completable);
    }

    @Test
    public void evictCollection() {
        Single<Boolean> completable =
                mRealmManager.evictCollection("id", new ArrayList<>(), String.class, TestRealmModel.class);
        applyTestSubscriber(completable);
    }

//    @Test
//    public void evictById() {
//        assertEquals(mRealmManager.evictById(TestRealmModel.class, "id", 1, Integer.class), true);
//    }
}
