package com.zeyad.genericusecase.domain.interactors.interactors;

import com.zeyad.genericusecase.data.services.realm_test_models.TestModel;
import com.zeyad.genericusecase.data.services.realm_test_models.TestViewModel;
import com.zeyad.genericusecase.domain.interactors.requests.PostRequest;

import junit.framework.Test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import rx.Subscriber;
import rx.observers.TestSubscriber;

class PostRequestTestRobot {


    static final Class DATA_CLASS = TestModel.class;
    static final boolean TO_PERSIST = false;
    static final Class DOMAIN_CLASS = TestViewModel.class;
    static final HashMap<String, Object> HASH_MAP = new HashMap<>();
    static final String ID_COLUMN_NAME = "id";
    static final JSONArray JSON_ARRAY = new JSONArray();
    static final JSONObject JSON_OBJECT = new JSONObject();
    static final Class PRESENTATION_CLASS = Test.class;
    static final Subscriber SUBSCRIBER = new TestSubscriber<>();
    static final String URL = "www.google.com";

    public static PostRequest buildPostRequest() {
        return new PostRequest.PostRequestBuilder(DATA_CLASS, TO_PERSIST)
                .hashMap(HASH_MAP)
                .idColumnName(ID_COLUMN_NAME)
                .jsonArray(JSON_ARRAY)
                .jsonObject(JSON_OBJECT)
                .presentationClass(PRESENTATION_CLASS)
                .subscriber(SUBSCRIBER)
                .url(URL)
                .build();
    }
}