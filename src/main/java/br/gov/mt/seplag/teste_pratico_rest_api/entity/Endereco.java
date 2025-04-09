package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "end_id")
    private Long id;

    @Column(name = "end_tipo_logradouro")
    private String tipoLogradouro;

    @Column(name = "end_logradouro")
    private String logradouro;

    @Column(name = "end_numero")
    private String numero;  // Alterado para aceitar S/N

    @Column(name = "end_bairro")
    private String bairro;

    @ManyToOne
    @JoinColumn(name = "cid_id", referencedColumnName = "cid_id")
    private Cidade cidade;

    @ManyToMany(mappedBy = "enderecos") // "enderecos" é o nome do atributo em Pessoa
    @JsonBackReference // Indica que este lado NÃO será serializado
    private List<Pessoa> pessoas;

    @ManyToMany(mappedBy = "enderecos") // "enderecos" é o nome do atributo em Unidade
    @JsonBackReference // Indica que este lado NÃO será serializado
    private List<Unidade> unidades;

    public Endereco()
    {}
    // {"id":"","tipoLogradouro":"Avenida","logradouro":"loh","numero":"87","bairro":"nnn","cidade":1}

    @JsonCreator
    public Endereco(
            @JsonProperty("id") Long id,
            @JsonProperty("tipoLogradouro") String tipoLogradouro,
            @JsonProperty("logradouro") String logradouro,
            @JsonProperty("numero") String numero,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("cidade") Cidade cidade)
    {
        this.id = id;
        this.tipoLogradouro = tipoLogradouro;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
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

    public String getTipoLogradouro()
    {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(String tipoLogradouro)
    {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouro()
    {
        return logradouro;
    }

    public void setLogradouro(String logradouro)
    {
        this.logradouro = logradouro;
    }

    public String getNumero()
    {
        return numero;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public String getBairro()
    {
        return bairro;
    }

    public void setBairro(String bairro)
    {
        this.bairro = bairro;
    }

    public Cidade getCidade()
    {
        return cidade;
    }

    public void setCidade(Cidade cidade)
    {
        this.cidade = cidade;
    }

    public List<Pessoa> getPessoas()
    {
        return pessoas;
    }

    public void setPessoas(List<Pessoa> pessoas)
    {
        this.pessoas = pessoas;
    }

    public String getLogradouroCompleto()
    {
        return logradouro + ", " + numero + " - " + bairro + ", " + cidade.getNome() + "/" +cidade.getUf();
    }

    public List<Unidade> getUnidades()
    {
        return unidades;
    }

    public void setUnidades(List<Unidade> unidades)
    {
        this.unidades = unidades;
    }
}
