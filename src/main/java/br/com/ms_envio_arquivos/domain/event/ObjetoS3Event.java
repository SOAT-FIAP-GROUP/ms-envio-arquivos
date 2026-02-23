package br.com.ms_envio_arquivos.domain.event;

import java.time.Instant;


public record ObjetoS3Event(
        String videoId,
        String url,
        StatusProcessamento status,
        Instant processadoEm,
        String usuarioId
) {}
