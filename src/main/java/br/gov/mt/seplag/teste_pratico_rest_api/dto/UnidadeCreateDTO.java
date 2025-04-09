package br.gov.mt.seplag.teste_pratico_rest_api.dto;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Unidade;

import java.util.List;

public class UnidadeCreateDTO {
    private Long id;
    private String nome;
    private String sigla;
    private List<Long> enderecos;

    public UnidadeCreateDTO(){

    }

    public UnidadeCreateDTO(Long id, String nome, String sigla, List<Long> enderecos)
    {
        this.id = id;
        this.nome = nome;
        this.sigla = sigla;
        this.enderecos = enderecos;
    }

    public Unidade toUnidade(Unidade unidade)
    {
        unidade.setNome(this.nome);
        unidade.setSigla(this.sigla);
        return unidade;
    }

    // Getters e Setters
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

    public String getSigla()
    {
        return sigla;
    }

    public void setSigla(String sigla)
    {
        this.sigla = sigla;
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