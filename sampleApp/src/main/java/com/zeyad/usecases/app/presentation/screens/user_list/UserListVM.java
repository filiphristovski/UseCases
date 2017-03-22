package com.zeyad.usecases.app.presentation.screens.user_list;

import com.zeyad.usecases.app.components.mvvm.BaseViewModel;
import com.zeyad.usecases.data.requests.GetRequest;
import com.zeyad.usecases.data.requests.PostRequest;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;
import com.zeyad.usecases.domain.interactors.data.IDataUseCase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static com.zeyad.usecases.app.presentation.screens.user_list.UserListState.onSearch;
import static com.zeyad.usecases.app.utils.Constants.URLS.USER;
import static com.zeyad.usecases.app.utils.Constants.URLS.USERS;

/**
 * @author zeyad on 11/1/16.
 */
class UserListVM extends BaseViewModel<UserListState> implements UserListViewModel {

    private final IDataUseCase dataUseCase;

    UserListVM() {
        dataUseCase = DataUseCaseFactory.getInstance();
    }

    UserListVM(IDataUseCase dataUseCase) {
        this.dataUseCase = dataUseCase;
    }

    @Override
    public void getUsers() {
        dataUseCase.getListOffLineFirst(new GetRequest
                .GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState() != null ? getViewState().getCurrentPage() : 0,
                        getViewState() != null ? getViewState().getLastId() : 0))
                .build())
                .map(UserListState::onNext)
                .compose(applyStates())
                .subscribe();
    }

    @Override
    public void incrementPage() {
        dataUseCase.getList(new GetRequest.GetRequestBuilder(UserRealm.class, true)
                .url(String.format(USERS, getViewState().getCurrentPage() + 1, getViewState().getLastId()))
                .build())
                .map(UserListState::onNext)
                .compose(applyStates())
                .subscribe();
    }

    @Override
    public Observable<UserListState> search(String query) {
        return dataUseCase.queryDisk(realm -> realm.where(UserRealm.class)
                .beginsWith(UserRealm.LOGIN, query), UserRealm.class)
                .zipWith(dataUseCase.getObject(new GetRequest
                        .GetRequestBuilder(UserRealm.class, true)
                        .url(String.format(USER, query))
                        .build())
                        .onErrorReturn(null), (list, userRealm) -> {
                    list.add(0, userRealm);
                    return Arrays.asList(new HashSet<>(list));
                })
                .map(list -> onSearch((List<UserRealm>) list))
                .compose(applyStates())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable deleteCollection(List<Long> selectedItemsIds) {
        return dataUseCase.deleteCollection(new PostRequest.PostRequestBuilder(UserRealm.class, true)
                .payLoad(selectedItemsIds)
                .build());
    }
}
