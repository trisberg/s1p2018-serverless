package com.example.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ImageSource {

	@Value("${channel.url:http://pictures-channel.default.svc.cluster.local}")
	String channelUrl;

	@Bean
	public Function<String, String> publish() {
		return (pic) -> {
			URL url = null;
			try {
				url = new URL(pic);
			} catch (MalformedURLException e) {
				throw new IllegalStateException("Invalid URL: " + pic, e);
			}
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			URLConnection conn = null;
			try {
				conn = url.openConnection();
			} catch (IOException e) {
				throw new IllegalStateException("Failed opening URL: " + pic, e);
			}
			byte[] img = new byte[0];
			try (InputStream inputStream = conn.getInputStream()) {
				int n = 0;
				byte[] buffer = new byte[1024];
				while (-1 != (n = inputStream.read(buffer))) {
					output.write(buffer, 0, n);
				}
				img = output.toByteArray();
				output.close();
			} catch (IOException e) {
				throw new IllegalStateException("Failed reading URL: " + pic, e);
			}
			String base64Img = Base64.getEncoder().encodeToString(img);
			String name = pic.substring(pic.lastIndexOf("/") + 1);
			postMessage(name, base64Img);
			return "Publishing: " + name + " [" + base64Img.length() + "]";
		};
	}

	private void postMessage(String name, String data) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("content-type", "text/plain");
		headers.set("ce-image-name", name);
		HttpEntity<String> request = new HttpEntity<>(data, headers);
		RestTemplate rt = new RestTemplate();
		rt.postForObject(channelUrl, request, String.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(ImageSource.class, args);
	}
}
