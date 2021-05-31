import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BingSynchronous {
    public static void main(String[] args) {
        try {
            System.out.println("--------------run::entry---------");
            new BingSynchronous().run();
            System.out.println("--------------run::exit---------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private OkHttpClient httpClient;

    private void run() throws IOException {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            public void log(String message) {
                System.out.println(String.format(
                        "[ID%d]: %s", Thread.currentThread().getId(), message));
            }
        });
        logging.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BASIC);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Request request = new Request.Builder()
                .get()
                .url("https://www.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&mkt=en-US")
                .build();


        final long startTime = System.nanoTime();

        try (Response response = httpClient.newCall(request).execute()) {
            String json = response.body().string();
            System.out.println(json);
            Gson gson = new Gson();
            BingResponse bingResponse = gson.fromJson(json, BingResponse.class);
            for (BingImage bingImage : bingResponse.images) {
                String fullUrl = "https://bing.com/" + bingImage.partOfUrl;
                downloadImage(fullUrl);
            }
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
        final long endTime = System.nanoTime();
        final long duration = endTime - startTime;
        System.out.println(String.format("Image time: %.1fms", (double) duration / 1000000.0));
    }
}