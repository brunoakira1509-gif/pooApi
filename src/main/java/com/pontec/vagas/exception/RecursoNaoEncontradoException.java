package com.pontec.vagas.exception;

public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(Long id) {
        super("Vaga com id " + id + " não foi encontrada");
    }
}
