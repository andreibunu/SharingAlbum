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
        /**
         * Design Pattern, Creational : BUILDER
         * Room.databaseBuilder(Context,     RoomDatabase,         String)
         *                      not null        not null           not null
         *                                                  not empty without spaces
         *
         *  will also set JournalMode on AUTOMATIC
         *  will also set mRequireMigration to TRUE
         *  will create a MigrationContainer for the database
         */

        return Room.databaseBuilder(application.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

}
