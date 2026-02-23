package br.com.ms_envio_arquivos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Objects;

@Service
public class EnvioService {

    private final S3Client s3Client;
    private final S3Presigner presigner;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public EnvioService(
            S3Client s3Client,
            S3Presigner presigner
    ) {
        this.s3Client = s3Client;
        this.presigner = presigner;
    }


    public URL generatePutUrl(String key, String contentType) {

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest =
                PutObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(15))
                        .putObjectRequest(putRequest)
                        .build();

        return presigner.presignPutObject(presignRequest).url();
    }


    @Autowired
    private WebClient.Builder webClientBuilder;

    public void uploadFileWithPresigned(MultipartFile file) throws Exception {

        String key = "videos/" + file.getOriginalFilename();

        URL presignedUrl = generatePutUrl(
                key,
                file.getContentType()
        );

        webClientBuilder.build()
                .put()
                .uri(presignedUrl.toURI())
                .header("Content-Type", Objects.requireNonNull(file.getContentType()))
                .header("Content-Length", String.valueOf(file.getSize()))
                .bodyValue(file.getBytes())
                .retrieve()
                .toBodilessEntity()
                .block();
    }


}
