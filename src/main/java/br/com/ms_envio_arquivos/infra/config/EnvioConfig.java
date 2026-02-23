package br.com.ms_envio_arquivos.infra.config;

import br.com.ms_envio_arquivos.controller.EnvioController;
import br.com.ms_envio_arquivos.service.EnvioService;
import br.com.ms_envio_arquivos.useCase.IEnvioUseCase;
import br.com.ms_envio_arquivos.useCase.impl.EnvioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class EnvioConfig {

    @Bean
    EnvioController envioController(IEnvioUseCase iEnvioUseCase){
        return new EnvioController(iEnvioUseCase);
    }

    @Bean
    EnvioUseCase envioUseCase(S3Client s3Client, EnvioService envioService, S3Presigner s3Presigner){
        return new EnvioUseCase(s3Client, envioService, s3Presigner);
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
