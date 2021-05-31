import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BingMain {
    public static void main(String[] args) {
        try {
            new BingMain().run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OkHttpClient httpClient;

    private void run() throws IOException {
        httpClient = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=en-US")
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String json = response.body().string();
            System.out.println(json);
            Gson gson = new Gson();
            BingResponse bingResponse = gson.fromJson(json, BingResponse.class);
            BingImage bingImage = bingResponse.images.get(0);

             System.out.println(bingImage.partOfUrl);
            String fullUrl = "https://bing.com/" + bingImage.partOfUrl;
            downloadImage(fullUrl);
        }
    }

    private void downloadImage(String imageUrl) throws IOException {

        Request request = new Request.Builder()
                .get()
                .url(imageUrl)
                .build();

        String imageFile = request.url().queryParameter("id");

        try (Response response = httpClient.newCall(request).execute()) {
            InputStream inputStream = response.body().byteStream();


            try (OutputStream outputStream = new FileOutputStream(imageFile)) {
                while (true) {
                    int readByte = inputStream.read();
                    if (readByte == -1) {
                        break;
                    }
                    outputStream.write(readByte);
                }
            }
        }
    }
}