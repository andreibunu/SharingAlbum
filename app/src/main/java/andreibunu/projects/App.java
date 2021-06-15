package andreibunu.projects;

import android.app.Application;

import com.facebook.stetho.Stetho;

import andreibunu.projects.di.component.AppComponent;
import andreibunu.projects.di.component.DaggerAppComponent;
import andreibunu.projects.di.modules.ApiModule;
import andreibunu.projects.di.modules.AppModule;
import andreibunu.projects.di.modules.ClientModule;
import andreibunu.projects.di.modules.DatabaseModule;

public class App extends Application {

    private static App instance;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule())
                .databaseModule(new DatabaseModule())
                .clientModule(new ClientModule())
                .build();
        Stetho.initializeWithDefaults(this);
        appComponent.inject(this);
    }

    public static App getInstance() {
        return instance;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }


}
