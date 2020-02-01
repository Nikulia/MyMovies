package com.nikolaevtsev.mymovies.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nikolaevtsev.mymovies.model.api.ApiFactoryTrailerReview;
import com.nikolaevtsev.mymovies.model.api.ApiServiceTrailerReview;
import com.nikolaevtsev.mymovies.model.pojo.ResponseTrailer;
import com.nikolaevtsev.mymovies.model.pojo.Trailer;

import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ListTrailerViewModel extends AndroidViewModel {

    CompositeDisposable compositeDisposable;
    MutableLiveData<List<Trailer>> liveDataTrailers;
    private String lang;
    private static final String API_KEY = "cd13d25e707fedec6df80ea235a078fb";

    public ListTrailerViewModel(@NonNull Application application) {
        super(application);
        lang = Locale.getDefault().getLanguage();
        liveDataTrailers = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
    }

    public LiveData<List<Trailer>> getLiveDataTrailers() {
        return liveDataTrailers;
    }

    public void loadData(int movieId) {
        ApiFactoryTrailerReview apiFactoryTrailerReview = ApiFactoryTrailerReview.getInstance();
        ApiServiceTrailerReview apiServiceTrailerReview = apiFactoryTrailerReview.getApiService();
        Disposable disposable = apiServiceTrailerReview.getTrailers(movieId, API_KEY, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseTrailer>() {
                    @Override
                    public void accept(ResponseTrailer responseTrailer) throws Exception {
                        liveDataTrailers.setValue(responseTrailer.getTrailers());
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
