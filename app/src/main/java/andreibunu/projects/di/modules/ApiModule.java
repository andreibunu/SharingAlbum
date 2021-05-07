package andreibunu.projects.di.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import andreibunu.projects.apiService.facerecognition.FaceRecognitionService;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class ApiModule {

    public static final String BASE_BACKEND_URL = "http:/192.168.0.234:2021/";

    @Provides
    Retrofit provideRetrofit() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        //necessary time for sending all the images to the backend for face recognition
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(10, TimeUnit.MINUTES)
                .writeTimeout(10, TimeUnit.MINUTES);
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_BACKEND_URL)
                .client(okHttpBuilder.build())
                .build();
    }

    @Provides
    FaceRecognitionService provideFaceRecognitionService(Retrofit retrofit) {
        return retrofit.create(FaceRecognitionService.class);
    }


}
