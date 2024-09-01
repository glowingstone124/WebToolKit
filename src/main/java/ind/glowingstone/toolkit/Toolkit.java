package ind.glowingstone.toolkit;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Toolkit {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String genRandomUsername() {
        return randomString(6, 16);
    }
    public static String genRandomPassword() {
        return randomString(10, 32) + "@";
    }

    public static String genRandomEmail() {
        return randomString(6, 16) + "@" + randomString(3, 5) + ".com";
    }

    public static String randomString(int minLength, int maxLength) {
        Random random = new Random();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public static <T> Response POST(String url, Map<String, T> params) throws IOException, InterruptedException {
        StringBuilder param = new StringBuilder();
        params.forEach((k, v) -> {
            if (param.length() > 0) {
                param.append("&");
            }
            param.append(encode(k)).append("=").append(encode(v.toString()));
        });

        return sendPostRequest(url, param.toString());
    }

    public static Response POST(String url, String body) throws IOException, InterruptedException {
        return sendPostRequest(url, body);
    }

    private static Response sendPostRequest(String url, String requestBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        return new Response(requestBody, response.statusCode(), extractCookies(response));
    }

    public static <T> Response GET(String url, Map<String, T> params) throws IOException, InterruptedException {
        StringBuilder param = new StringBuilder();
        params.forEach((k, v) -> {
            if (param.length() > 0) {
                param.append("&");
            }
            param.append(encode(k)).append("=").append(encode(v.toString()));
        });

        URI uri = URI.create(url + "?" + param.toString());
        return sendGetRequest(uri);
    }

    public static Response GET(String url) throws IOException, InterruptedException {
        URI uri = URI.create(url);
        return sendGetRequest(uri);
    }

    private static Response sendGetRequest(URI uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Response Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

        return new Response("", response.statusCode(), extractCookies(response));
    }

    private static String extractCookies(HttpResponse<String> response) {
        Map<String, List<String>> headers = response.headers().map();
        StringBuilder cookies = new StringBuilder();

        if (headers.containsKey("set-cookie")) {
            List<String> cookiesList = headers.get("set-cookie");
            System.out.println("Cookies: ");
            for (String cookie : cookiesList) {
                cookies.append(cookie).append("; ");
            }
        }
        return cookies.toString();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
    public static class Response {
        String requestBody;
        int responseCode;
        String cookie;

        public Response(String requestBody, int responseCode, String cookie) {
            this.requestBody = requestBody;
            this.responseCode = responseCode;
            this.cookie = cookie;
        }

        @Override
        public String toString() {
            return "Response{" +
                    "requestBody='" + requestBody + '\'' +
                    ", responseCode=" + responseCode +
                    ", cookie='" + cookie + '\'' +
                    '}';
        }
    }
    public enum Method {
        GET, POST
    }
    public static <T> int StressTest(Method method, String url, Map<String, T> params) throws IOException, InterruptedException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        long start = System.currentTimeMillis();
        long end = start + 10000;
        AtomicInteger count = new AtomicInteger();

        for (int i = 0; i < 10000; i++) {
            executor.execute(() -> {
                while (System.currentTimeMillis() < end) {
                    try {
                        if (method == Method.GET) {
                            GET(url, params);
                        } else if (method == Method.POST) {
                            POST(url, params);
                        }
                        count.getAndIncrement();
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(12, TimeUnit.SECONDS);
        return count.get();
    }


}
