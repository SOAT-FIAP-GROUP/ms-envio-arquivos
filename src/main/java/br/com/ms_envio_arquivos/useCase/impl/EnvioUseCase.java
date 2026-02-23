package br.com.ms_envio_arquivos.useCase.impl;

import br.com.ms_envio_arquivos.service.EnvioService;
import br.com.ms_envio_arquivos.useCase.IEnvioUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

public class EnvioUseCase implements IEnvioUseCase {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private final EnvioService envioService;

    public EnvioUseCase(S3Client s3Client, EnvioService envioService, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.envioService = envioService;
        this.s3Presigner = s3Presigner;
    }

    @Value("${aws.bucket.name}")
    private String bucketName;

    public PresignedPutObjectRequest generatePresignedUrl(
            String fileName,
            String contentType
    ) {

        String key = "videos/" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .putObjectRequest(objectRequest)
                        .build();

        return s3Presigner.presignPutObject(presignRequest);
    }


    @Override
    public String upload(MultipartFile multipartFile) throws IOException {

        PresignedPutObjectRequest presigned =
                generatePresignedUrl(
                        multipartFile.getOriginalFilename(),
                        multipartFile.getContentType()
                );

        URL url = presigned.url();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", multipartFile.getContentType());

        try (InputStream is = multipartFile.getInputStream();
             OutputStream os = connection.getOutputStream()) {
            is.transferTo(os);
        }

        int responseCode = connection.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("Erro ao enviar arquivo: " + responseCode);
        }

        connection.disconnect();

        return presigned.url().toString();
    }

    @Override public byte[] download(String filename) {
        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .build());
        return objectAsBytes.asByteArray();
    }

    @Override
    public String getPresignedURL(String filename) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
//                .contentType("application/pdf") // opcional
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(5))
                        .putObjectRequest(putObjectRequest)
                        .build();

        PresignedPutObjectRequest presignedRequest =
                s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
