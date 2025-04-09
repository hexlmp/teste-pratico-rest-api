package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivo;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivoPK;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ServidorEfetivoRepository extends JpaRepository<ServidorEfetivo, ServidorEfetivoPK> {

    @Query("SELECT s FROM ServidorEfetivo s WHERE s.pessoa.id = :pessoaId AND s.matricula = :matricula")
    Optional<ServidorEfetivo> findByPessoaIdAndMatricula(@Param("pessoaId") Long pessoaId, @Param("matricula") String matricula);

    @Query("SELECT s FROM ServidorEfetivo s WHERE LOWER(s.matricula) LIKE LOWER(CONCAT('%', :matricula, '%'))")
    List<ServidorEfetivo> findByMatriculaContainingIgnoreCase(@Param("matricula") String matricula);

    @Query("SELECT s FROM ServidorEfetivo s JOIN FETCH s.pessoa")
    Page<ServidorEfetivo> findAllWithPessoa(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END " +
            "FROM ServidorEfetivo s WHERE s.pessoa.id = :pessoaId AND s.matricula = :matricula")
    boolean existsByPessoaIdAndMatricula(@Param("pessoaId") Long pessoaId,
                                         @Param("matricula") String matricula);

    void deleteByPessoaIdAndMatricula(Long pessoaId, String matricula);
}