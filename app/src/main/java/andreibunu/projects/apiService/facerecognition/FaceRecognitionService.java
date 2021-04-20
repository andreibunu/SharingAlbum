package andreibunu.projects.apiService.facerecognition;

import java.util.List;

import andreibunu.projects.apiService.response.ImageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FaceRecognitionService {

    @Headers("Content-Type: application/json")
    @GET("/one")
    Call<String> test();

    @Multipart
    @POST("/imgtest")
    Call<ImageResponse> testImg(@Part List<MultipartBody.Part> img);


}
