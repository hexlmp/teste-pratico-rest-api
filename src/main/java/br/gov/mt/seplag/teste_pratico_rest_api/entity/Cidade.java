package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "cidade")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid_id")
    private Long id;

    @Column(name = "cid_nome")
    private String nome;

    @Column(name = "cid_uf")
    private String uf;

    @OneToMany(mappedBy = "cidade")
    @JsonIgnore // Ignora
    private List<Endereco> enderecos;

    public Cidade()
    {}

    /*public Cidade(Long id)
    {
        this.id = id;
    }*/

    @JsonCreator
    public Cidade(@JsonProperty("id") Long id) {
        this.id = id;
    }


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

    public String getUf()
    {
        return uf;
    }

    public void setUf(String uf)
    {
        this.uf = uf;
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

