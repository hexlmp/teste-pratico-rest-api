package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import java.io.Serializable;

public class ServidorEfetivoPK implements Serializable {
    private Long pessoa;
    private String matricula;

    // Constructors, getters, setters, equals and hashCode
    public ServidorEfetivoPK() {
    }

    public ServidorEfetivoPK(Long pessoa, String matricula) {
        this.pessoa = pessoa;
        this.matricula = matricula;
    }

    // Getters and setters
    public Long getPessoa() {
        return pessoa;
    }

    public void setPessoa(Long pessoa) {
        this.pessoa = pessoa;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServidorEfetivoPK that = (ServidorEfetivoPK) o;

        if (!pessoa.equals(that.pessoa)) return false;
        return matricula.equals(that.matricula);
    }

    @Override
    public int hashCode() {
        int result = pessoa.hashCode();
        result = 31 * result + matricula.hashCode();
        return result;
    }
}