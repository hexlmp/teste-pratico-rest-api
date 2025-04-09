package br.gov.mt.seplag.teste_pratico_rest_api.dto;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Lotacao;

import java.util.Date;

public class LotacaoCreateDTO {
    private Long id;
    private Long pessoaId;
    private Long unidadeId;
    private Date dataLotacao;
    private Date dataRemocao;
    private String portaria;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public Date getDataLotacao() {
        return dataLotacao;
    }

    public void setDataLotacao(Date dataLotacao) {
        this.dataLotacao = dataLotacao;
    }

    public Date getDataRemocao() {
        return dataRemocao;
    }

    public void setDataRemocao(Date dataRemocao) {
        this.dataRemocao = dataRemocao;
    }

    public String getPortaria() {
        return portaria;
    }

    public void setPortaria(String portaria) {
        this.portaria = portaria;
    }

    // Método para converter DTO em entidade
    public Lotacao toLotacao(Lotacao lotacao) {
        lotacao.setDataLotacao(this.dataLotacao);
        lotacao.setDataRemocao(this.dataRemocao);
        lotacao.setPortaria(this.portaria);
        return lotacao;
    }
}