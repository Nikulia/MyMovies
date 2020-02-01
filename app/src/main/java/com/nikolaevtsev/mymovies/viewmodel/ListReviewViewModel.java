package com.nikolaevtsev.mymovies.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nikolaevtsev.mymovies.model.api.ApiFactoryTrailerReview;
import com.nikolaevtsev.mymovies.model.api.ApiServiceTrailerReview;
import com.nikolaevtsev.mymovies.model.pojo.ResponseMovie;
import com.nikolaevtsev.mymovies.model.pojo.ResponseReview;
import com.nikolaevtsev.mymovies.model.pojo.Review;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ListReviewViewModel extends AndroidViewModel {
    private CompositeDisposable compositeDisposable;
    private MutableLiveData<List<Review>> liveDataReviews;
    private String lang;
    private static final String API_KEY = "cd13d25e707fedec6df80ea235a078fb";

    public ListReviewViewModel(@NonNull Application application) {
        super(application);
        lang = Locale.getDefault().getLanguage();
        compositeDisposable = new CompositeDisposable();
        liveDataReviews = new MutableLiveData<>();
    }

    public LiveData<List<Review>> getLiveDataReviews() {
        return liveDataReviews;
    }

    public void loadData(int movieId) {
        ApiFactoryTrailerReview apiFactoryTrailerReview = ApiFactoryTrailerReview.getInstance();
        ApiServiceTrailerReview apiServiceTrailerReview = apiFactoryTrailerReview.getApiService();
        Disposable disposable = apiServiceTrailerReview.getReviews(movieId, API_KEY, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseReview>() {
                    @Override
                    public void accept(ResponseReview responseReview) throws Exception {
                        liveDataReviews.setValue(responseReview.getReviews());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i("Error", throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null)
            compositeDisposable.dispose();
    }
}
