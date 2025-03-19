package Task2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String URL = "https://api.nasa.gov/planetary/apod?api_key=JfSmt3lADyCUgI4QNjfJoTEKbemmkIfbhugePNh0";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build()
        ) {
            HttpGet request = new HttpGet(URL);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
            NASAResponse nasaResponse;

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                byte[] bytes = response.getEntity().getContent().readAllBytes();
                nasaResponse = mapper.readValue(bytes, NASAResponse.class);
            }

            HttpGet secondRequest = new HttpGet(nasaResponse.getUrl());
            secondRequest.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            try (CloseableHttpResponse secondResponse = httpClient.execute(secondRequest)) {
                byte[] secondResponseBytes = secondResponse.getEntity().getContent().readAllBytes();

                try (FileOutputStream fos = new FileOutputStream(new File("image.jpg"))) {
                    fos.write(secondResponseBytes);
                }
            }
        }
    }
}