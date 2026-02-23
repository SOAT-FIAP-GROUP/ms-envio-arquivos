package br.com.ms_envio_arquivos.controller;

import br.com.ms_envio_arquivos.useCase.IEnvioUseCase;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class EnvioController {

    private final IEnvioUseCase iEnvioUseCase;

    public EnvioController(IEnvioUseCase iEnvioUseCase) {
        this.iEnvioUseCase = iEnvioUseCase;
    }

    public void uploadDoArquivo(MultipartFile file) throws IOException {
        iEnvioUseCase.upload(file);
    }

    public byte[] downloadDoArquivo(String filename) {
        return iEnvioUseCase.download(filename);
    }

    public String getPresignedURL(String filename){return iEnvioUseCase.getPresignedURL(filename); }
}
