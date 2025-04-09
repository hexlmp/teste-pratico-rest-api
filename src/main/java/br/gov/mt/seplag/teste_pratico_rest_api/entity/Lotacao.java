package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "lotacao")
public class Lotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lot_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pes_id", referencedColumnName = "pes_id")
    private Pessoa pessoa;

    @ManyToOne
    @JoinColumn(name = "unid_id", referencedColumnName = "unid_id")
    private Unidade unidade;

    @Column(name = "lot_data_lotacao")
    @Temporal(TemporalType.DATE)
    private Date dataLotacao;

    @Column(name = "lot_data_remocao")
    @Temporal(TemporalType.DATE)
    private Date dataRemocao;

    @Column(name = "lot_portaria")
    private String portaria;


    // Getters and setters

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Pessoa getPessoa()
    {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa)
    {
        this.pessoa = pessoa;
    }

    public Unidade getUnidade()
    {
        return unidade;
    }

    public void setUnidade(Unidade unidade)
    {
        this.unidade = unidade;
    }

    public Date getDataLotacao()
    {
        return dataLotacao;
    }

    public void setDataLotacao(Date dataLotacao)
    {
        this.dataLotacao = dataLotacao;
    }

    public Date getDataRemocao()
    {
        return dataRemocao;
    }

    public void setDataRemocao(Date dataRemocao)
    {
        this.dataRemocao = dataRemocao;
    }

    public String getPortaria()
    {
        return portaria;
    }

    public void setPortaria(String portaria)
    {
        this.portaria = portaria;
    }
}
