package com.pontec.vagas.controller;

import com.pontec.vagas.exception.ErroResposta;
import com.pontec.vagas.exception.RecursoNaoEncontradoException;
import com.pontec.vagas.model.Vaga;
import com.pontec.vagas.repository.VagaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/vagas", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*")
@Tag(name = "Vagas", description = "Operações CRUD de vagas de emprego")
public class VagaController {

    private final VagaRepository repository;

    public VagaController(VagaRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Cadastrar vaga", description = "Insere uma nova vaga de emprego no portal.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vaga criada com sucesso",
                    content = @Content(schema = @Schema(implementation = Vaga.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class)))
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vaga> criar(@Valid @RequestBody Vaga vaga) {
        vaga.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(vaga));
    }

    @Operation(summary = "Listar vagas", description = "Retorna todas as vagas cadastradas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de vagas retornada com sucesso",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Vaga.class))))
    })
    @GetMapping
    public List<Vaga> listar() {
        return repository.findAll();
    }

    @Operation(summary = "Buscar vaga por id", description = "Retorna uma única vaga pelo seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vaga encontrada",
                    content = @Content(schema = @Schema(implementation = Vaga.class))),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class)))
    })
    @GetMapping("/{id}")
    public Vaga buscarPorId(@Parameter(description = "Id da vaga", example = "1") @PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException(id));
    }

    @Operation(summary = "Atualizar vaga", description = "Atualiza todas as informações de uma vaga existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vaga atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = Vaga.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class))),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class)))
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Vaga atualizar(@Parameter(description = "Id da vaga", example = "1") @PathVariable Long id,
                          @Valid @RequestBody Vaga dados) {
        Vaga vaga = repository.findById(id).orElseThrow(() -> new RecursoNaoEncontradoException(id));
        vaga.setTitulo(dados.getTitulo());
        vaga.setEmpresa(dados.getEmpresa());
        vaga.setArea(dados.getArea());
        vaga.setCidade(dados.getCidade());
        vaga.setModalidade(dados.getModalidade());
        vaga.setTipoContratacao(dados.getTipoContratacao());
        vaga.setSalario(dados.getSalario());
        vaga.setDescricao(dados.getDescricao());
        return repository.save(vaga);
    }

    @Operation(summary = "Excluir vaga", description = "Remove uma vaga pelo seu identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vaga excluída com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Vaga não encontrada",
                    content = @Content(schema = @Schema(implementation = ErroResposta.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@Parameter(description = "Id da vaga", example = "1") @PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException(id);
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
