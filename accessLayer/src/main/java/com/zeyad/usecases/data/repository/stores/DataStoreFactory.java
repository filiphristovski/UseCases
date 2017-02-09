package com.zeyad.usecases.data.repository.stores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.usecases.R;
import com.zeyad.usecases.data.db.DataBaseManager;
import com.zeyad.usecases.data.exceptions.NetworkConnectionException;
import com.zeyad.usecases.data.mappers.IDAOMapper;
import com.zeyad.usecases.data.network.RestApiImpl;
import com.zeyad.usecases.data.utils.Utils;
import com.zeyad.usecases.domain.interactors.data.DataUseCase;

import static com.zeyad.usecases.Config.getInstance;

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
    public DataStore dynamically(@NonNull String url, IDAOMapper entityDataMapper) throws Exception {
        if (!url.isEmpty())
            if (Utils.isNetworkAvailable(mContext))
                return new CloudDataStore(RestApiImpl.getInstance(), mDataBaseManager, entityDataMapper);
            else throw new NetworkConnectionException("Please Check your internet connection!");
        else if (mDataBaseManager == null)
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
        else
            return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a disk {@link DataStore}.
     */
    @NonNull
    public DataStore disk(IDAOMapper entityDataMapper) throws IllegalAccessException {
        if (!DataUseCase.hasRealm() || mDataBaseManager == null)
            throw new IllegalAccessException(getInstance().getContext().getString(R.string.no_db));
        return new DiskDataStore(mDataBaseManager, entityDataMapper);
    }

    /**
     * Creates a cloud {@link DataStore}.
     */
    @NonNull
    public DataStore cloud(IDAOMapper entityDataMapper) {
        return new CloudDataStore(RestApiImpl.getInstance(), mDataBaseManager, entityDataMapper);
    }
}
