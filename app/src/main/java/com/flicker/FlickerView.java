package com.flicker;

import android.database.Cursor;

import com.flicker.database.ImageSearch;
import com.flicker.models.Photo;

import java.util.List;

/**
 * Created by ner on 2/18/17.
 */

public interface FlickerView {

    void displayImages(List<Photo> imageUrls);

    void displayErrorMessage(String message);

    void recentSearches(List<String> searches);
}
