package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.FotoPessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FotoPessoaRepository extends JpaRepository<FotoPessoa, Long> {
    List<FotoPessoa> findByPessoaId(Long pessoaId);
}