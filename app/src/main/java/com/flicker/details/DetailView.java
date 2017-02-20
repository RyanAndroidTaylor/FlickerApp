package com.flicker.details;

import com.flicker.PhotoInfoResponse;

/**
 * Created by ner on 2/20/17.
 */

interface DetailView {

    void displayPhotoInfo(PhotoInfoResponse photoInfoResponse);

    void displayErrorMessage(String message);
}
