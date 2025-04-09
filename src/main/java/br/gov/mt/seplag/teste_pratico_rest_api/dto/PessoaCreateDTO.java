package br.gov.mt.seplag.teste_pratico_rest_api.dto;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import java.util.Date;
import java.util.List;

public class PessoaCreateDTO
{
    private Long id;
    private String nome;
    private Date dataNascimento;
    private String sexo;
    private String mae;
    private String pai;
    private List<String> tempFotoIds;
    private List<Long> enderecos;


    // Getters e Setters

    public Pessoa toPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setNome(nome);
        pessoa.setDataNascimento(dataNascimento);
        pessoa.setSexo(sexo);
        pessoa.setMae(mae);
        pessoa.setPai(pai);
        return pessoa;
    }

    public Pessoa toPessoa(Pessoa pessoa) {
        pessoa.setNome(nome);
        pessoa.setDataNascimento(dataNascimento);
        pessoa.setSexo(sexo);
        pessoa.setMae(mae);
        pessoa.setPai(pai);
        return pessoa;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getNome()
    {
        return nome;
    }

    public void setNome(String nome)
    {
        this.nome = nome;
    }

    public Date getDataNascimento()
    {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento)
    {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo()
    {
        return sexo;
    }

    public void setSexo(String sexo)
    {
        this.sexo = sexo;
    }

    public String getMae()
    {
        return mae;
    }

    public void setMae(String mae)
    {
        this.mae = mae;
    }

    public String getPai()
    {
        return pai;
    }

    public void setPai(String pai)
    {
        this.pai = pai;
    }

    public List<String> getTempFotoIds()
    {
        return tempFotoIds;
    }

    public void setTempFotoIds(List<String> tempFotoIds)
    {
        this.tempFotoIds = tempFotoIds;
    }

    public List<Long> getEnderecos()
    {
        return enderecos;
    }

    public void setEnderecos(List<Long> enderecos)
    {
        this.enderecos = enderecos;
    }
}