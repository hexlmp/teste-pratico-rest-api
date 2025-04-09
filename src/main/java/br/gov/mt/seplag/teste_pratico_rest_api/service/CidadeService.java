package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Cidade;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CidadeService {

    @Autowired
    private CidadeRepository cidadeRepository;

    @Transactional(readOnly = true)
    public List<Cidade> listarTodos() {
        return cidadeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cidade buscarPorId(Long id) {
        Optional<Cidade> cidade = cidadeRepository.findById(id);
        return cidade.orElseThrow(() -> new RuntimeException("Cidade não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cidade> buscarPorUf(String uf) {
        return cidadeRepository.findByUf(uf);
    }

    @Transactional
    public Cidade salvar(Cidade cidade) {
        return cidadeRepository.save(cidade);
    }

    @Transactional
    public Cidade atualizar(Long id, Cidade cidade) {
        Cidade cidadeExistente = buscarPorId(id);
        cidade.setId(cidadeExistente.getId());
        return cidadeRepository.save(cidade);
    }

    @Transactional
    public void deletar(Long id) {
        Cidade cidade = buscarPorId(id);
        cidadeRepository.delete(cidade);
    }
}