package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.FotoPessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.FotoPessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FotoPessoaService {

    @Autowired
    private FotoPessoaRepository fotoPessoaRepository;

    @Transactional(readOnly = true)
    public List<FotoPessoa> listarTodos() {
        return fotoPessoaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public FotoPessoa buscarPorId(Long id) {
        Optional<FotoPessoa> foto = fotoPessoaRepository.findById(id);
        return foto.orElseThrow(() -> new RuntimeException("Foto não encontrada com o ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<FotoPessoa> buscarPorPessoa(Long pessoaId) {
        return fotoPessoaRepository.findByPessoaId(pessoaId);
    }

    @Transactional
    public FotoPessoa salvar(FotoPessoa fotoPessoa) {
        return fotoPessoaRepository.save(fotoPessoa);
    }

    @Transactional
    public FotoPessoa atualizar(Long id, FotoPessoa fotoPessoa) {
        FotoPessoa fotoExistente = buscarPorId(id);
        fotoPessoa.setId(fotoExistente.getId());
        return fotoPessoaRepository.save(fotoPessoa);
    }

    @Transactional
    public void deletar(Long id) {
        FotoPessoa foto = buscarPorId(id);
        fotoPessoaRepository.delete(foto);
    }
}