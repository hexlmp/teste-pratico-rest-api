package br.gov.mt.seplag.teste_pratico_rest_api.dto;

public class ServidorEfetivoDTO {
    private Long pessoa;
    private String matricula;

    private String matriculaAntiga;
    private Long pessoaAntiga;


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

    public String getMatriculaAntiga()
    {
        return matriculaAntiga;
    }

    public void setMatriculaAntiga(String matriculaAntiga)
    {
        this.matriculaAntiga = matriculaAntiga;
    }

    public Long getPessoaAntiga()
    {
        return pessoaAntiga;
    }

    public void setPessoaAntiga(Long pessoaAntiga)
    {
        this.pessoaAntiga = pessoaAntiga;
    }
}