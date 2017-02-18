package com.flicker;

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

    public void subscribe(FlickerView view) {
        this.view = view;
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
}
