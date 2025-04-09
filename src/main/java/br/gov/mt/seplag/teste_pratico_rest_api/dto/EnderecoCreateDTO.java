package br.gov.mt.seplag.teste_pratico_rest_api.dto;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;


public class EnderecoCreateDTO
{
    private Long id;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;  // Alterado para aceitar S/N
    private String bairro;
    private Long cidade;

    public EnderecoCreateDTO(Long id, String tipoLogradouro, String logradouro, String numero, String bairro, Long cidade)
    {
        this.id = id;
        this.tipoLogradouro = tipoLogradouro;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
    }

    public Endereco toEndereco(Endereco endereco)
    {
        endereco.setTipoLogradouro(this.tipoLogradouro);
        endereco.setLogradouro(this.logradouro);
        endereco.setNumero(this.numero);
        endereco.setBairro(this.bairro);
        return endereco;
    }

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

    public Long getCidade()
    {
        return cidade;
    }

    public void setCidade(Long cidade)
    {
        this.cidade = cidade;
    }
}
