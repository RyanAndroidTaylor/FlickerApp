package com.flicker.models;

import java.util.List;

/**
 * Created by ner on 2/18/17.
 */

public class PhotoResponse {
    public Photos photos;

    public class Photos {
        public List<Photo> photo;
    }
}
