package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.PessoaCreateDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Lotacao;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.LotacaoRepository;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PessoaService {

    @Autowired
    private LotacaoRepository lotacaoRepository;
    @Autowired
    private PessoaRepository pessoaRepository;
    @Autowired
    private EnderecoService enderecoService;

    @Transactional(readOnly = true)
    public Pessoa findById(Long id) {
        return pessoaRepository.findById(id).orElseThrow(() -> new RuntimeException("Pessoa não encontrada"));
    }

    @Transactional(readOnly = true)
    public Page<Pessoa> findByNamePaginado(PageRequest pageable, String nome) {
        return pessoaRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(nome, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Pessoa> listarPaginado(PageRequest pageable) {
        return pessoaRepository.findAllByOrderByNomeAsc(pageable);
    }

    @Transactional(readOnly = true)
    public List<Pessoa> buscarPessoasPorUnidade(Long unidId) {
        List<Lotacao> lotacoes = lotacaoRepository.findByUnidadeId(unidId);
        List<Pessoa> pessoas = new ArrayList<>();

        for (Lotacao lotacao : lotacoes) {
            pessoas.add(lotacao.getPessoa());
        }

        return pessoas;
    }

    @Transactional(readOnly = true)
    public List<Pessoa> listarPessoas() {
        return pessoaRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public List<Pessoa> listarTodos(int page, int size) {
        Page<Pessoa> pessoas = pessoaRepository.findAllByOrderByNomeAsc(PageRequest.of(page, size));
        return pessoas.getContent();
    }

    @Transactional(readOnly = true)
    public Pessoa buscarPorId(Long id) {
        Optional<Pessoa> pessoa = pessoaRepository.findById(id);
        return pessoa.orElseThrow(() -> new RuntimeException("Pessoa não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Pessoa> buscarPorNome(String nome) {
        return pessoaRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Pessoa salvar(Pessoa pessoa) {
        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public Pessoa atualizar(Long id, Pessoa pessoa) {
        Pessoa pessoaExistente = buscarPorId(id);
        pessoa.setId(pessoaExistente.getId());
        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public void deletar(Long id) {
        Pessoa pessoa = buscarPorId(id);
        pessoaRepository.delete(pessoa);
    }

    // Operações específicas para Endereços
    @Transactional
    public Pessoa adicionarEndereco(Long pessoaId, Long enderecoId) {
        Pessoa pessoa = findById(pessoaId);
        Endereco endereco = enderecoService.findById(enderecoId);

        pessoa.getEnderecos().add(endereco); // JPA gerencia a tabela de junção automaticamente
        return pessoaRepository.save(pessoa);
    }

    @Transactional
    public Pessoa removerEndereco(Long pessoaId, Long enderecoId) {
        Pessoa pessoa = findById(pessoaId);
        Endereco endereco = enderecoService.findById(enderecoId);

        pessoa.getEnderecos().remove(endereco); // Remove a associação
        return pessoaRepository.save(pessoa);
    }

    public List<Endereco> listarEnderecosDaPessoa(Long pessoaId) {
        Pessoa pessoa = findById(pessoaId);
        return pessoa.getEnderecos();
    }
}
