import okhttp3.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class AsynchronousMain {
    public static void main(String[] args) {
        System.out.println("--------------run::entry---------");
        new AsynchronousMain().run();
        System.out.println("--------------run::exit---------");
    }

    private void run() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .get()
                .url("https://google.com")
                .build();

        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Callback::onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String answer = response.body().string();
                try (PrintWriter writer = new PrintWriter(
                        new FileOutputStream("SomeThingAsync.txt"))) {
                    writer.print(answer);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(answer);
            }
        };

        client.newCall(request).enqueue(callback);
    }
}