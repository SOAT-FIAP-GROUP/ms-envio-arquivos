package br.com.ms_envio_arquivos.useCase;

import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;

public interface IEnvioUseCase {

    PresignedPutObjectRequest generatePresignedUrl(String fileName,
                                                   String contentType);

    String upload(MultipartFile file) throws IOException;

    byte[] download(String filename);

    String getPresignedURL(String filename);
}
