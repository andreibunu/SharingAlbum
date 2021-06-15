package andreibunu.projects.apiService.facerecognition;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import andreibunu.projects.apiService.response.ImageResponse;
import andreibunu.projects.utils.ImageUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static andreibunu.projects.utils.ImageUtils.getFileFormat;

public class FaceRecognitionProxy {
    public static final String TAG = FaceRecognitionProxy.class.getCanonicalName();
    FaceRecognitionService service;

    @Inject
    public FaceRecognitionProxy(FaceRecognitionService service) {
        this.service = service;
    }

    public List<Integer> testImg2(File imgFile, String uid) {
        try {
            imgFile = ImageUtils.saveBitmapToFile(imgFile);
            List<MultipartBody.Part> addProductRequest = new ArrayList<>();

            RequestBody requestBody;
            requestBody = RequestBody.create(MediaType.parse("image/" + getFileFormat(imgFile.getName())), imgFile);

            MultipartBody.Part uploadImagesPart = MultipartBody.Part.createFormData("img1", imgFile.getName(), requestBody);

            addProductRequest.add(uploadImagesPart);

            MultipartBody.Part uidPart = MultipartBody.Part.createFormData("uid", uid);
            addProductRequest.add(uidPart);

            Response<ImageResponse> response = service.sendImage(addProductRequest).execute();
            if (response.isSuccessful() && response.body() != null) {
                Log.d(TAG, response.body().getResult().toString());
                return response.body().getResult();
            }
        } catch (Exception e) {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return null;
    }


}
