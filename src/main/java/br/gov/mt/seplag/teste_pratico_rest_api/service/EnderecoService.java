package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Transactional(readOnly = true)
    public List<Endereco> listarTodos() {
        return enderecoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Endereco buscarPorId(Long id) {
        Optional<Endereco> endereco = enderecoRepository.findById(id);
        return endereco.orElseThrow(() -> new RuntimeException("Endereço não encontrado com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Endereco> buscarPorCidade(Long cidadeId) {
        return enderecoRepository.findByCidadeId(cidadeId);
    }

    @Transactional
    public Endereco salvar(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    @Transactional
    public Endereco atualizar(Long id, Endereco endereco) {
        Endereco enderecoExistente = buscarPorId(id);
        endereco.setId(enderecoExistente.getId());
        return enderecoRepository.save(endereco);
    }

    @Transactional
    public void deletar(Long id) {
        Endereco endereco = buscarPorId(id);
        enderecoRepository.delete(endereco);
    }

    /**
     * Busca endereços por termo de pesquisa com limite de resultados
     * @param query Termo de pesquisa (pode ser parte do logradouro, bairro ou cidade)
     * @param limit Número máximo de resultados a retornar
     * @return Lista de endereços que correspondem à pesquisa
     */
    public List<Endereco> buscarEnderecos(String query, int limit) {
        // Normaliza o termo de pesquisa removendo espaços extras e convertendo para minúsculas
        String termoPesquisa = "%" + query.trim().toLowerCase() + "%";

        // Busca por logradouro, bairro ou nome da cidade
        return enderecoRepository.findBySearchTerm(termoPesquisa, limit);
    }

    public Endereco findById(Long id) {
        return enderecoRepository.findById(id).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

    public Endereco save(Endereco endereco) {
        return enderecoRepository.save(endereco);
    }

    public void delete(Long id) {
        enderecoRepository.deleteById(id);
    }
}