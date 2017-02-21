package com.flicker;

import com.flicker.database.ImageSearch;
import com.flicker.models.Photo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by ner on 2/18/17.
 */

public class FlickerPresenter {

    private FlickerView view;

    private CompositeDisposable subscriptions = new CompositeDisposable();

    public void subscribe(final FlickerView flickerView) {
        this.view = flickerView;

        subscriptions.add(
                FlickerService.instance().observeRecentSearches()
                        .subscribe(new Consumer<List<ImageSearch>>() {
                            @Override
                            public void accept(List<ImageSearch> searches) throws Exception {
                                if (view != null) {
                                    ArrayList<String> recentSearches = new ArrayList<>();

                                    for (ImageSearch imageSearch: searches) {
                                        recentSearches.add(imageSearch.query);
                                    }

                                    view.recentSearches(recentSearches);
                                }
                            }
                        })
        );
    }

    public void unSubscribe() {
        this.view = null;

        subscriptions.clear();
    }

    public void findImages(String query) {
        Disposable subscription = FlickerService.instance()
                .getImages(query)
                .subscribe(
                        new Consumer<List<Photo>>() {
                            @Override
                            public void accept(List<Photo> photos) throws Exception {
                                if (view != null)
                                    view.displayImages(photos);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (view != null)
                                    view.displayErrorMessage(throwable.getMessage());
                            }
                        });

        subscriptions.add(subscription);
    }

    public void updateRecentQueries(String text) {
        FlickerService.instance().updateCurrentQueryText(text);
    }
}
