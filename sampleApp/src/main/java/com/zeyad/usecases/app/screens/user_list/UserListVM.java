package com.zeyad.usecases.app.screens.user_list;

import com.zeyad.usecases.api.IDataService;
import com.zeyad.usecases.app.components.redux.BaseEvent;
import com.zeyad.usecases.app.components.redux.BaseViewModel;
import com.zeyad.usecases.app.components.redux.SuccessStateAccumulator;
import com.zeyad.usecases.app.screens.user_list.events.DeleteUsersEvent;
import com.zeyad.usecases.app.screens.user_list.events.GetPaginatedUsersEvent;
import com.zeyad.usecases.app.screens.user_list.events.SearchUsersEvent;
import com.zeyad.usecases.requests.GetRequest;
import com.zeyad.usecases.requests.PostRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static com.zeyad.usecases.app.utils.Constants.URLS.USER;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
public class UserListVM extends BaseViewModel<UserListState> {

    private IDataService dataUseCase;

    @Override
    public void init(SuccessStateAccumulator<UserListState> successStateAccumulator, UserListState initialState,
                     Object... otherDependencies) {
        dataUseCase = (IDataService) otherDependencies[0];
        this.successStateAccumulator = successStateAccumulator;
        this.initialState = initialState;
    }

    @Override
    public Function<BaseEvent, Flowable<?>> mapEventsToExecutables() {
        return event -> {
            Flowable executable = Flowable.empty();
            if (event instanceof GetPaginatedUsersEvent)
                executable = getUsers(((GetPaginatedUsersEvent) event).getLastId());
            else if (event instanceof DeleteUsersEvent)
                executable = deleteCollection(((DeleteUsersEvent) event).getSelectedItemsIds());
            else if (event instanceof SearchUsersEvent)
                executable = search(((SearchUsersEvent) event).getQuery());
            return executable;
        };
    }

    public Flowable<List<User>> getUsers(long lastId) {
        return lastId == 0 ? dataUseCase.getListOffLineFirst(new GetRequest.Builder(User.class, true)
                .url(String.format(USERS, lastId))
                .build()) : dataUseCase.getList(new GetRequest.Builder(User.class, true)
                .url(String.format(USERS, lastId))
                .build());
    }

    public Flowable<List<User>> search(String query) {
        return dataUseCase.<User>queryDisk(realm -> realm.where(User.class).beginsWith(User.LOGIN, query))
                .zipWith(dataUseCase.<User>getObject(new GetRequest.Builder(User.class, true)
                                .url(String.format(USER, query))
                                .build())
                                .onErrorReturn(throwable -> null)
                                .map(user -> user != null ? Collections.singletonList(user) : Collections.emptyList()),
                        (BiFunction<List<User>, List<User>, List<User>>) (users, singleton) -> {
                            users.addAll(singleton);
                            return new ArrayList<>(new HashSet<>(users));
                        });
    }

    public Flowable<List<Long>> deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollectionByIds(new PostRequest.Builder(User.class, true)
                .payLoad(selectedItemsIds)
                .build())
                .map(o -> selectedItemsIds);
    }
}