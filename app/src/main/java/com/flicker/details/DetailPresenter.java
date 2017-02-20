package com.flicker.details;

import com.flicker.FlickerService;
import com.flicker.PhotoInfoResponse;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by ner on 2/20/17.
 */

public class DetailPresenter {

    private DetailView view;

    private CompositeDisposable subscriptions = new CompositeDisposable();

    public void subscribe(DetailView view) {
        this.view = view;
    }

    public void unSubscribe() {
        this.view = null;

        subscriptions.clear();
    }

    public void getDetails(Long photoId) {
        FlickerService.instance().getPhotoInfo(photoId)
                .subscribe(
                        new Consumer<PhotoInfoResponse>() {
                            @Override
                            public void accept(PhotoInfoResponse photoInfoResponse) throws Exception {
                                if (view != null)
                                    view.displayPhotoInfo(photoInfoResponse);
                            }
                        },
                        new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (view != null)
                                    view.displayErrorMessage(throwable.getMessage());
                            }
                        });
    }
}
