package com.pontec.vagas.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "Corpo padrão das respostas de erro da API")
public class ErroResposta {

    @Schema(description = "Momento do erro", example = "2026-11-12T10:30:00")
    private final LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Código HTTP", example = "404")
    private final int status;

    @Schema(description = "Mensagem do erro", example = "Vaga com id 99 não foi encontrada")
    private final String mensagem;

    @Schema(description = "Erros de validação por campo (apenas em 400)")
    private final Map<String, String> campos;

    public ErroResposta(int status, String mensagem, Map<String, String> campos) {
        this.status = status;
        this.mensagem = mensagem;
        this.campos = campos;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getMensagem() { return mensagem; }
    public Map<String, String> getCampos() { return campos; }
}
