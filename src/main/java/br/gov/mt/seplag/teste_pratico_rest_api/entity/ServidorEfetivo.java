package br.gov.mt.seplag.teste_pratico_rest_api.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "servidor_efetivo")
@IdClass(ServidorEfetivoPK.class)
public class ServidorEfetivo {

    /*@Id
    @ManyToOne
    @JoinColumn(name = "pes_id", referencedColumnName = "pes_id", nullable = false)
    private Pessoa pessoa;*/

    @Id
    @ManyToOne/*(fetch = FetchType.LAZY)*/
    @JoinColumn(name = "pes_id", referencedColumnName = "pes_id", nullable = false)
    @MapsId("pesId")
    //@JsonBackReference // Indica que este lado NÃO será serializado
    private Pessoa pessoa;


    @Id
    @Column(name = "se_matricula", nullable = false, length = 20)
    private String matricula;

    // Getters and setters
    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}