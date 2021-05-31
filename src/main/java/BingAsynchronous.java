import com.google.gson.Gson;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BingAsynchronous {

    private OkHttpClient httpClient;
    private long startTime;

    public static void main(String[] args) {

        new BingAsynchronous().run();
    }

    private void run() {
        startTime = System.nanoTime();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            public void log(String message) {
                System.out.println(String.format(
                        "[ID%d]: %s", Thread.currentThread().getId(), message));
            }
        });
        logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BASIC);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
//                .dispatcher(new Dispatcher(Executors.newSingleThreadScheduledExecutor()))
                .build();

        Request request = new Request.Builder()
                .get()
                .url("https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=8&mkt=en-US")
                .build();


        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleBingResponseWithImages(response);
            }
        });
    }

    private void handleBingResponseWithImages(Response response) throws IOException {
        String json = response.body().string();
        System.out.println(json);
        Gson gson = new Gson();
        BingResponse bingResponse = gson.fromJson(json, BingResponse.class);
        for (BingImage bingImage : bingResponse.images) {
            String fullUrl = "https://bing.com/" + bingImage.partOfUrl;
            downloadImage(fullUrl);
        }
        final long endTime = System.nanoTime();
        final long duration = endTime - startTime;
        System.out.printf("Full time: %.1fms", (double) duration / 1000000.0);
    }


    private void downloadImage(String imageUrl) throws IOException {
        final long startTime = System.nanoTime();
        Request request = new Request.Builder()
                .get()
                .url(imageUrl)
                .build();

        String imageFile = request.url().queryParameter("id");

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                handleImageResponse(response,imageFile);
            }
        });
    }

    private void handleImageResponse(Response response, String imageFile) throws IOException {

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
