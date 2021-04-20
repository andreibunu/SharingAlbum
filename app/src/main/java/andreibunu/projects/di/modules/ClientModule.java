package andreibunu.projects.di.modules;

import andreibunu.projects.apiService.facerecognition.FaceRecognitionHandler;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionProxy;
import andreibunu.projects.apiService.facerecognition.FaceRecognitionService;
import andreibunu.projects.database.DatabaseHandler;
import dagger.Module;
import dagger.Provides;

@Module
public class ClientModule {

    @Provides
    FaceRecognitionProxy provideFaceRecognitionProxy(FaceRecognitionService FaceRecognitionService) {
        return new FaceRecognitionProxy(FaceRecognitionService);
    }

    @Provides
    FaceRecognitionHandler provideFaceRecognitionHandler(FaceRecognitionProxy FaceRecognitionProxy, DatabaseHandler appDatabaseHandler) {
        return new FaceRecognitionHandler(FaceRecognitionProxy, appDatabaseHandler);
    }
}
