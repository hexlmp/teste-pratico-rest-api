package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivo;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorTemporario;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorTemporarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ServidorTemporarioRepository extends JpaRepository<ServidorTemporario, ServidorTemporarioId> {

    @Query("SELECT st FROM ServidorTemporario st WHERE st.pessoa.id = :pessoaId AND st.dataAdmissao = :dataAdmissao")
    Optional<ServidorTemporario> findByPessoaIdAndDataAdmissao(@Param("pessoaId") Long pessoaId,
                                                               @Param("dataAdmissao") Date dataAdmissao);

    @Query("SELECT st FROM ServidorTemporario st WHERE st.pessoa.id = :pessoaId")
    Page<ServidorTemporario> findByPessoaId(@Param("pessoaId") Long pessoaId, Pageable pageable);

    @Query("SELECT st FROM ServidorTemporario st WHERE st.dataAdmissao BETWEEN :dataInicio AND :dataFim")
    Page<ServidorTemporario> findByPeriodoAdmissao(@Param("dataInicio") Date dataInicio,
                                                   @Param("dataFim") Date dataFim,
                                                   Pageable pageable);

    @Query("SELECT s FROM ServidorTemporario s JOIN FETCH s.pessoa")
    Page<ServidorEfetivo> findAllWithPessoa(Pageable pageable);

    @Modifying
    @Query("DELETE FROM ServidorTemporario st WHERE st.pessoa.id = :pessoaId AND st.dataAdmissao = :dataAdmissao")
    void deleteByPessoaIdAndDataAdmissao(@Param("pessoaId") Long pessoaId,
                                         @Param("dataAdmissao") Date dataAdmissao);
}