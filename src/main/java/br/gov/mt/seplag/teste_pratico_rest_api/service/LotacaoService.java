package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Lotacao;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.LotacaoRepository;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.PessoaRepository;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.UnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LotacaoService {

    @Autowired
    private LotacaoRepository lotacaoRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private UnidadeRepository unidadeRepository;

    @Transactional(readOnly = true)
    public List<Lotacao> listarTodos() {
        return lotacaoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Lotacao> listarPaginado(PageRequest pageable) {
        return lotacaoRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Lotacao buscarPorId(Long id) {
        Optional<Lotacao> lotacao = lotacaoRepository.findById(id);
        return lotacao.orElseThrow(() -> new RuntimeException("Lotação não encontrada com o ID: " + id));
    }

    @Transactional
    public Lotacao salvar(Lotacao lotacao) {
        return lotacaoRepository.save(lotacao);
    }

    @Transactional
    public Lotacao atualizar(Long id, Lotacao lotacao) {
        Lotacao lotacaoExistente = buscarPorId(id);
        lotacao.setId(lotacaoExistente.getId());
        return lotacaoRepository.save(lotacao);
    }

    @Transactional
    public void deletar(Long id) {
        Lotacao lotacao = buscarPorId(id);
        lotacaoRepository.delete(lotacao);
    }

    @Transactional(readOnly = true)
    public List<Lotacao> buscarPorPessoa(Long pessoaId) {
        return lotacaoRepository.findByPessoaId(pessoaId);
    }

    @Transactional(readOnly = true)
    public List<Lotacao> buscarPorUnidade(Long unidadeId) {
        return lotacaoRepository.findByUnidadeId(unidadeId);
    }
}