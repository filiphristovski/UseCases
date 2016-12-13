package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.R;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.mappers.IDaoMapper;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory;

import static com.zeyad.usecases.Config.getInstance;
import static com.zeyad.usecases.domain.interactors.data.DataUseCaseFactory.NONE;

public class DataStoreFactory {

    private final Context mContext;
    @Nullable
    private DataBaseManager mDataBaseManager;

    public DataStoreFactory(Context context) {
        mContext = context;
        mDataBaseManager = null;
    }

    public DataStoreFactory(@Nullable DataBaseManager dataBaseManager, Context context) {
        if (dataBaseManager == null)
            throw new IllegalArgumentException(context.getString(R.string.dbmanager_null_error));
        mContext = context;
        mDataBaseManager = dataBaseManager;
    }

    /**
     * Create {@link DataStore} .
     */
    @NonNull
    public DataStore dynamically(@NonNull String url, boolean isGet, IDaoMapper entityDataMapper) throws IllegalAccessException {
        if (!url.isEmpty() && (!isGet || Utils.isNetworkAvailable(mContext)))
            return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper);
        else if (mDataBaseManager == null)
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
        else
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(IDaoMapper entityDataMapper) throws IllegalAccessException {
        if (DataUseCaseFactory.getDBType() == NONE || mDataBaseManager == null)
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
        return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(IDaoMapper entityDataMapper) {
        return new CloudDataStore(new RestApiImpl(), mDataBaseManager, entityDataMapper);
    }
}