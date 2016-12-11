package com.zeyad.usecases.domain.interactors.files;

import android.content.Context;

import com.zeyad.usecases.data.utils.Utils;

/**
 * @author zeyad on 11/11/16.
 */

public class FileUseCaseFactory {

    private static IFileUseCase sFilesUseCase;

    public static IFileUseCase getInstance() {
        return sFilesUseCase;
    }

    public static void init(Context context) {
        if (!Utils.doesContextBelongsToApplication(context))
            throw new IllegalArgumentException("Context should be application context only.");
        FileUseCase.init(context);
        sFilesUseCase = FileUseCase.getInstance();
    }

    public static void destoryInstance() {
        sFilesUseCase = null;
    }
}
