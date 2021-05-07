package andreibunu.projects.di.modules;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Singleton;

import andreibunu.projects.database.AppDatabase;
import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {
    private static final String DATABASE_NAME = "gallery_database";

    @Singleton
    @Provides
    AppDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

}
