package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivo;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivoPK;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.ServidorEfetivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServidorEfetivoService {

    @Autowired
    private ServidorEfetivoRepository repository;

    @Transactional(readOnly = true)
    public Page<ServidorEfetivo> listarPaginado(PageRequest pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ServidorEfetivo> listarTodos(PageRequest pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public ServidorEfetivo buscarPorId(ServidorEfetivoPK id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servidor não encontrado"));
    }

    @Transactional(readOnly = true)
    public ServidorEfetivo buscarPorPessoaEMatricula(Long pessoaId, String matricula) {
        return repository.findByPessoaIdAndMatricula(pessoaId, matricula)
                .orElseThrow(() -> new RuntimeException("Servidor não encontrado"));
    }

    @Transactional
    public ServidorEfetivo salvar(ServidorEfetivo servidorEfetivo) {
        return repository.save(servidorEfetivo);
    }

    @Transactional
    public ServidorEfetivo atualizar(String matriculaAntiga, Long pessoaId, ServidorEfetivo servidorEfetivo) {
        // Verifica se existe
        buscarPorPessoaEMatricula(pessoaId, matriculaAntiga);

        // Se a matricula foi alterada, precisamos deletar o antigo e criar um novo
        if (!matriculaAntiga.equals(servidorEfetivo.getMatricula())) {
            repository.deleteByPessoaIdAndMatricula(pessoaId, matriculaAntiga);
        }

        return repository.save(servidorEfetivo);
    }

    @Transactional
    public void deletar(Long pessoaId, String matricula) {
        repository.deleteByPessoaIdAndMatricula(pessoaId, matricula);
    }

    @Transactional(readOnly = true)
    public boolean existeServidorComChave(Long pessoaId, String matricula) {
        return repository.existsByPessoaIdAndMatricula(pessoaId, matricula);
    }

    @Transactional
    public void deletarPorPessoaEMatricula(Long pessoaId, String matricula) {
        repository.deleteByPessoaIdAndMatricula(pessoaId, matricula);
    }

    @Transactional(readOnly = true)
    public List<ServidorEfetivo> buscarPorMatricula(String matricula) {
        return repository.findByMatriculaContainingIgnoreCase(matricula);
    }
}