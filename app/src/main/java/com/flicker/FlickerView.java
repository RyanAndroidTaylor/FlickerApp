package com.flicker;

import java.util.List;

/**
 * Created by ner on 2/18/17.
 */

public interface FlickerView {

    void displayImages(List<Photo> imageUrls);

    void displayErrorMessage(String message);
}
