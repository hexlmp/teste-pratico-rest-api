package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Lotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LotacaoRepository extends JpaRepository<Lotacao, Long> {
    // Buscar as lotações de uma unidade
    List<Lotacao> findByUnidadeId(Long unidadeId);
    List<Lotacao> findByPessoaId(Long pessoaId);
}