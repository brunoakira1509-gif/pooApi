package com.pontec.vagas.model;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Schema(description = "Vaga de emprego publicada no portal")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único da vaga (gerado automaticamente)",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    @Schema(description = "Cargo / título da vaga", example = "Desenvolvedor(a) Back-end Pleno")
    private String titulo;

    @NotBlank(message = "A empresa é obrigatória")
    @Size(max = 120, message = "A empresa deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    @Schema(description = "Nome da empresa contratante", example = "iFood")
    private String empresa;

    @NotBlank(message = "A área é obrigatória")
    @Size(max = 60, message = "A área deve ter no máximo 60 caracteres")
    @Column(nullable = false, length = 60)
    @Schema(description = "Área de atuação", example = "Tecnologia")
    private String area;

    @NotBlank(message = "A cidade é obrigatória")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    @Schema(description = "Cidade ou localidade da vaga", example = "Sorocaba, SP")
    private String cidade;

    @NotBlank(message = "A modalidade é obrigatória")
    @Pattern(regexp = "Presencial|Híbrido|Remoto",
             message = "A modalidade deve ser Presencial, Híbrido ou Remoto")
    @Column(nullable = false, length = 20)
    @Schema(description = "Modalidade de trabalho", example = "Híbrido",
            allowableValues = {"Presencial", "Híbrido", "Remoto"})
    private String modalidade;

    @NotBlank(message = "O tipo de contratação é obrigatório")
    @Pattern(regexp = "CLT|PJ|Estágio|Temporário",
             message = "O tipo de contratação deve ser CLT, PJ, Estágio ou Temporário")
    @Column(nullable = false, length = 20)
    @Schema(description = "Tipo de contratação", example = "CLT",
            allowableValues = {"CLT", "PJ", "Estágio", "Temporário"})
    private String tipoContratacao;

    @Size(max = 80, message = "O salário deve ter no máximo 80 caracteres")
    @Column(length = 80)
    @Schema(description = "Faixa salarial (texto livre)", example = "R$ 10.000 – 13.500")
    private String salario;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
    @Column(nullable = false, length = 2000)
    @Schema(description = "Responsabilidades, requisitos e benefícios",
            example = "Criar APIs em Java para entregas e pagamentos.")
    private String descricao;

    public Vaga() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getModalidade() { return modalidade; }
    public void setModalidade(String modalidade) { this.modalidade = modalidade; }

    public String getTipoContratacao() { return tipoContratacao; }
    public void setTipoContratacao(String tipoContratacao) { this.tipoContratacao = tipoContratacao; }

    public String getSalario() { return salario; }
    public void setSalario(String salario) { this.salario = salario; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
