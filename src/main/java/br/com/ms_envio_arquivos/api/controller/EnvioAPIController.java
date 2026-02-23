package br.com.ms_envio_arquivos.api.controller;


import br.com.ms_envio_arquivos.controller.EnvioController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/envio")
public class EnvioAPIController {

    private final EnvioController envioController;

    public EnvioAPIController(EnvioController envioController) {
        this.envioController = envioController;
    }
    @Operation(
            summary = "Enviar Vidio",
            description = "Rota responsável pela entrada do vídeo para processamento"
    )
    @PostMapping("/v1/upload")
    public ResponseEntity<String> uploadV1(@RequestParam("file") MultipartFile file) throws IOException {
        envioController.uploadDoArquivo(file);
        return ResponseEntity.ok("File uploaded successfully");
    }

    @PostMapping("/v2/upload")
    public ResponseEntity<String> uploadV2(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok( envioController.getPresignedURL(file.getOriginalFilename()));
    }


    @Operation(
            summary = "Baixar vídeo",
            description = "Rota responsável pelo download do vídeo"
    )
    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename){
        byte[] data = envioController.downloadDoArquivo(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(data);
    }
}
