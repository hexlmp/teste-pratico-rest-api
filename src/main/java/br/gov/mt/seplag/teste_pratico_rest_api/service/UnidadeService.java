package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Unidade;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UnidadeService {

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Autowired
    private EnderecoService enderecoService;

    @Transactional(readOnly = true)
    public List<Unidade> listarTodos() {
        return unidadeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Unidade> listarPaginado(PageRequest pageable) {
        return unidadeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Unidade buscarPorId(Long id) {
        Optional<Unidade> unidade = unidadeRepository.findById(id);
        return unidade.orElseThrow(() -> new RuntimeException("Unidade não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Unidade> buscarPorNome(String nome) {
        return unidadeRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Unidade salvar(Unidade unidade) {
        return unidadeRepository.save(unidade);
    }

    @Transactional
    public Unidade atualizar(Long id, Unidade unidade) {
        Unidade unidadeExistente = buscarPorId(id);
        unidade.setId(unidadeExistente.getId());
        return unidadeRepository.save(unidade);
    }

    @Transactional
    public void deletar(Long id) {
        Unidade unidade = buscarPorId(id);
        unidadeRepository.delete(unidade);
    }

    // Operações específicas para Endereços
    @Transactional
    public Unidade adicionarEndereco(Long unidadeId, Long enderecoId) {
        Unidade unidade = buscarPorId(unidadeId);
        Endereco endereco = enderecoService.findById(enderecoId);

        unidade.getEnderecos().add(endereco);
        return unidadeRepository.save(unidade);
    }

    @Transactional
    public Unidade removerEndereco(Long unidadeId, Long enderecoId) {
        Unidade unidade = buscarPorId(unidadeId);
        Endereco endereco = enderecoService.findById(enderecoId);

        unidade.getEnderecos().remove(endereco);
        return unidadeRepository.save(unidade);
    }

    public List<Endereco> listarEnderecosDaUnidade(Long unidadeId) {
        Unidade unidade = buscarPorId(unidadeId);
        return unidade.getEnderecos();
    }
}