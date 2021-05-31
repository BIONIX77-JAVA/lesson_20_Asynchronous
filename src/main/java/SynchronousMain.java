import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class SynchronousMain {
    public static void main(String[] args) {
        try {
            System.out.println("--------------run::entry---------");
            new SynchronousMain().run();
            System.out.println("--------------run::exit---------");
        } catch (IOException e) {
            System.out.println("Failed to made request" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void run() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request =
                new Request.Builder()
                        .url("https://google.com")
                        .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}