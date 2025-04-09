package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "pessoa")
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pes_id")
    private Long id;

    @Column(name = "pes_nome")
    private String nome;

    @Column(name = "pes_data_nascimento")
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date dataNascimento;

    @Column(name = "pes_sexo")
    private String sexo;

    @Column(name = "pes_mae")
    private String mae;

    @Column(name = "pes_pai")
    private String pai;

    @OneToMany(mappedBy = "pessoa")
    private List<FotoPessoa> fotos;

    //@OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "pessoa")
    @JsonIgnore // Ignora completamente a lista de servidores
    private List<ServidorTemporario> servidoresTemporarios;

    /*@OneToMany(mappedBy = "pessoa")
    @JsonIgnore // Ignora completamente a lista de servidores
    private List<ServidorEfetivo> servidoresEfetivos;*/

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    //@JsonManagedReference // Indica que este lado será serializado
    private Set<ServidorEfetivo> matriculas = new HashSet<>();

    @OneToMany(mappedBy = "pessoa")
    private List<Lotacao> lotacoes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "pessoa_endereco",
            joinColumns = @JoinColumn(name = "pes_id"),
            inverseJoinColumns = @JoinColumn(name = "end_id")
    )
    @JsonManagedReference // Indica que este lado será serializado
    private List<Endereco> enderecos = new ArrayList<>();


    // métodos utilitários para manter a consistência bidirecional
    public void adicionarMatricula(ServidorEfetivo matricula) {
        matriculas.add(matricula);
        matricula.setPessoa(this);
    }

    public void removerMatricula(ServidorEfetivo matricula) {
        matriculas.remove(matricula);
        matricula.setPessoa(null);
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

    public List<FotoPessoa> getFotos()
    {
        return fotos;
    }

    public void setFotos(List<FotoPessoa> fotos)
    {
        this.fotos = fotos;
    }

    public List<ServidorTemporario> getServidoresTemporarios()
    {
        return servidoresTemporarios;
    }

    public void setServidoresTemporarios(List<ServidorTemporario> servidoresTemporarios)
    {
        this.servidoresTemporarios = servidoresTemporarios;
    }

    public Set<ServidorEfetivo> getMatriculas()
    {
        return matriculas;
    }

    public void setMatriculas(Set<ServidorEfetivo> matriculas)
    {
        this.matriculas = matriculas;
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

