package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "unidade")
public class Unidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unid_id")
    private Long id;

    @Column(name = "unid_nome")
    private String nome;

    @Column(name = "unid_sigla")
    private String sigla;

    @OneToMany(mappedBy = "unidade")
    private List<Lotacao> lotacoes;

    @ManyToMany
    @JoinTable(
            name = "unidade_endereco",
            joinColumns = @JoinColumn(name = "unid_id"),
            inverseJoinColumns = @JoinColumn(name = "end_id")
    )
    @JsonManagedReference // Indica que este lado será serializado
    private List<Endereco> enderecos = new ArrayList<>();


    // Getters and setters

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

    public List<Lotacao> getLotacoes()
    {
        return lotacoes;
    }

    public void setLotacoes(List<Lotacao> lotacoes)
    {
        this.lotacoes = lotacoes;
    }

    public List<Endereco> getEnderecos()
    {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos)
    {
        this.enderecos = enderecos;
    }
}

