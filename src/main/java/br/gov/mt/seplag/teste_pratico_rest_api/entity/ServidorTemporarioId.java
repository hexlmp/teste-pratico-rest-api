package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ServidorTemporarioId implements Serializable {
    private Long pessoa;
    private Date dataAdmissao;

    // Construtores
    public ServidorTemporarioId() {
    }

    public ServidorTemporarioId(Long pessoa, Date dataAdmissao) {
        this.pessoa = pessoa;
        this.dataAdmissao = dataAdmissao;
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

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServidorTemporarioId that = (ServidorTemporarioId) o;
        return Objects.equals(pessoa, that.pessoa) &&
                Objects.equals(dataAdmissao, that.dataAdmissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pessoa, dataAdmissao);
    }
}