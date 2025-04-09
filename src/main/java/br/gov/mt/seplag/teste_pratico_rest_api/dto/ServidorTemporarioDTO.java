package br.gov.mt.seplag.teste_pratico_rest_api.dto;

import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ServidorTemporarioDTO {
    @NotNull(message = "Pessoa é obrigatória")
    private Long pessoa;

    @NotNull(message = "Data de admissão é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dataAdmissao;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dataDemissao;

    @NotNull(message = "Pessoa original é obrigatória para atualização")
    private Long pessoaAntiga;

    @NotNull(message = "Data de admissão original é obrigatória para atualização")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dataAdmissaoAntiga;

    // Getters e Setters
    public Long getPessoaAntiga() {
        return pessoaAntiga;
    }

    public void setPessoaAntiga(Long pessoaAntiga) {
        this.pessoaAntiga = pessoaAntiga;
    }

    public Date getDataAdmissaoAntiga() {
        return dataAdmissaoAntiga;
    }

    public void setDataAdmissaoAntiga(Date dataAdmissaoAntiga) {
        this.dataAdmissaoAntiga = dataAdmissaoAntiga;
    }

    // Getters e Setters
    public Long getPessoa() {
        return pessoa;
    }

    public void setPessoa(Long pessoa) {
        this.pessoa = pessoa;
    }

    public Date getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(Date dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public Date getDataDemissao() {
        return dataDemissao;
    }

    public void setDataDemissao(Date dataDemissao) {
        this.dataDemissao = dataDemissao;
    }
}