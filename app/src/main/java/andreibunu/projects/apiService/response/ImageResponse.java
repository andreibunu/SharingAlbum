package andreibunu.projects.apiService.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageResponse {
    @SerializedName("mykey")
    public List<Integer> result;


    public ImageResponse(List<Integer> result) {
        this.result = result;
    }

    public List<Integer> getResult() {
        return result;
    }

    public void setResult(List<Integer> result) {
        this.result = result;
    }
}
