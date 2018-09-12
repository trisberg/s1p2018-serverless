package com.example.demo;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.function.Function;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import com.google.protobuf.ByteString;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class CatFunction {

	@Bean
	public ImageAnnotatorSettings imageAnnotatorSettings(
			CredentialsProvider credentialsProvider) throws IOException {
		return ImageAnnotatorSettings.newBuilder()
				.setCredentialsProvider(credentialsProvider).build();
	}

	@Bean
	public ImageAnnotatorClient imageAnnotatorClient(
			ImageAnnotatorSettings settings) throws IOException {
		return ImageAnnotatorClient.create(settings);
	}

	@Bean
	public Function<Message<String>, String> catnotcat(ImageAnnotatorClient client) {
		return (in) -> {
			String image = in.getPayload();
			System.out.println("HEADERS: " + in.getHeaders());
			// Decode the Base64 encoded input into bytes.
			byte[] bytes = Base64.getDecoder().decode(image);

			// Make a Vision API request to detect labels
			BatchAnnotateImagesResponse response = client
					.batchAnnotateImages(Collections.singletonList(
							AnnotateImageRequest.newBuilder()
									.setImage(Image.newBuilder()
											.setContent(ByteString.copyFrom(bytes)))
									.addFeatures(Feature.newBuilder()
											.setType(Feature.Type.LABEL_DETECTION))
									.build()));

			// For debugging purposes :)
			//System.out.println(response.toString());

			// If any label matches "cat" with score >= 90%,
			// then return "cat", otherwise return "not cat"
			return response.getResponses(0).getLabelAnnotationsList()
					.stream().anyMatch(
							label -> "cat".equals(label.getDescription())
									&& label.getScore() >= 0.90f) ?
					"cat" : "not cat";

		};
	}

	public static void main(String[] args) {
		SpringApplication.run(CatFunction.class, args);
	}
}
