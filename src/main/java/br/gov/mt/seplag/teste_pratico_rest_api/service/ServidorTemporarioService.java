package br.gov.mt.seplag.teste_pratico_rest_api.service;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.ServidorTemporarioDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivo;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorTemporario;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorTemporarioId;
import br.gov.mt.seplag.teste_pratico_rest_api.repository.ServidorTemporarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class ServidorTemporarioService {

    @Autowired
    private ServidorTemporarioRepository repository;

    @Autowired
    private PessoaService pessoaService;

    @Transactional(readOnly = true)
    public Page<ServidorTemporario> listarPaginado(PageRequest pageable) {
        return repository.findAll(pageable);
    }


    public Page<ServidorTemporario> listarTodos(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<ServidorTemporario> buscarPorPessoaEDataAdmissao(Long pessoaId, Date dataAdmissao) {
        return repository.findByPessoaIdAndDataAdmissao(pessoaId, dataAdmissao);
    }

    public Page<ServidorTemporario> buscarPorPessoa(Long pessoaId, Pageable pageable) {
        return repository.findByPessoaId(pessoaId, pageable);
    }

    public Page<ServidorTemporario> buscarPorPeriodoAdmissao(Date dataInicio, Date dataFim, Pageable pageable) {
        return repository.findByPeriodoAdmissao(dataInicio, dataFim, pageable);
    }

    @Transactional
    public ServidorTemporario criar(ServidorTemporarioDTO dto) {
        Pessoa pessoa = pessoaService.buscarPorId(dto.getPessoa());

        ServidorTemporario servidor = new ServidorTemporario();
        servidor.setPessoa(pessoa);
        servidor.setDataAdmissao(dto.getDataAdmissao());
        servidor.setDataDemissao(dto.getDataDemissao());

        return repository.save(servidor);
    }

    @Transactional
    public ServidorTemporario atualizar(ServidorTemporarioDTO dto) {
        // Busca o servidor existente
        ServidorTemporario servidorExistente = repository
                .findByPessoaIdAndDataAdmissao(dto.getPessoaAntiga(), dto.getDataAdmissaoAntiga())
                .orElseThrow(() -> new RuntimeException("Servidor temporário não encontrado"));

        Pessoa pessoa = pessoaService.buscarPorId(dto.getPessoa());

        // Verifica se está alterando a chave primária
        boolean alterouChave = !dto.getPessoaAntiga().equals(dto.getPessoa()) ||
                !dto.getDataAdmissaoAntiga().equals(dto.getDataAdmissao());

        if (alterouChave) {
            // Verifica se a nova chave já existe
            if (repository.findByPessoaIdAndDataAdmissao(dto.getPessoa(), dto.getDataAdmissao()).isPresent()) {
                throw new RuntimeException("Já existe um servidor temporário com esta pessoa e data de admissão");
            }

            // Remove o registro antigo
            repository.delete(servidorExistente);

            // Cria um novo registro
            ServidorTemporario novoServidor = new ServidorTemporario();
            novoServidor.setPessoa(pessoa);
            novoServidor.setDataAdmissao(dto.getDataAdmissao());
            novoServidor.setDataDemissao(dto.getDataDemissao());
            return repository.save(novoServidor);
        } else {
            // Atualiza apenas os campos não-chave
            servidorExistente.setDataDemissao(dto.getDataDemissao());
            return repository.save(servidorExistente);
        }
    }

    @Transactional
    public void deletar(Long pessoaId, Date dataAdmissao) {
        repository.deleteByPessoaIdAndDataAdmissao(pessoaId, dataAdmissao);
    }
}