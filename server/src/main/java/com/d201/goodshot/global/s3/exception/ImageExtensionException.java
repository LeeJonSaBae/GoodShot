package com.d201.goodshot.global.s3.exception;

import static com.d201.goodshot.global.s3.exception.ImageExceptionList.IMAGE_EXTENSION_ERROR;

public class ImageExtensionException extends ImageException {
    public ImageExtensionException(){
        super(IMAGE_EXTENSION_ERROR.getErrorCode(),
                IMAGE_EXTENSION_ERROR.getHttpStatus(),
                IMAGE_EXTENSION_ERROR.getMessage());
    }
}
