package com.zeyad.genericusecase.data.requests;

import android.support.annotation.Nullable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

@RunWith(JUnit4.class)
public class PostRequestTest {

    @Nullable
    private PostRequest mPostRequest;

    @Before
    public void setUp() throws Exception {
        mPostRequest = PostRequestTestRobot.buildPostRequest();
    }

    @After
    public void tearDown() throws Exception {
        mPostRequest = null;
    }

    @Test
    public void testGetUrl() throws Exception {
        assertThat(mPostRequest.getUrl(), is(equalTo(PostRequestTestRobot.URL)));
    }

    @Test
    public void testGetSubscriber() throws Exception {
        assertThat(mPostRequest.getSubscriber(), is(equalTo(PostRequestTestRobot.SUBSCRIBER)));
    }

    @Test
    public void testGetDataClass() throws Exception {
        assertThat(mPostRequest.getDataClass(), is(equalTo(PostRequestTestRobot.DATA_CLASS)));
    }

    @Test
    public void testGetPresentationClass() throws Exception {
        assertThat(mPostRequest.getPresentationClass(), is(equalTo(PostRequestTestRobot.PRESENTATION_CLASS)));
    }

    @Test
    public void testIsPersist() throws Exception {
        assertThat(mPostRequest.isPersist(), is(equalTo(PostRequestTestRobot.TO_PERSIST)));
    }

    @Test
    public void testGetIdColumnName() throws Exception {
        assertThat(mPostRequest.getIdColumnName(), is(equalTo(PostRequestTestRobot.ID_COLUMN_NAME)));
    }
}