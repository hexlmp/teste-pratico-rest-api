package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>
{
    /*@Query("SELECT p FROM Pessoa p WHERE p.nome = :nome")
    List<Pessoa> buscarPorNome(String nome);*/

    List<Pessoa> findByNomeOrderByNomeAsc(String nome);
    List<Pessoa> findByNomeContainingIgnoreCase(String nome);

    List<Pessoa> findAllByOrderByNomeAsc();

    Page<Pessoa> findAllByOrderByNomeAsc(Pageable pageable);

    // Busca paginada por nome (case-insensitive)
    //@Query("SELECT p FROM Pessoa p WHERE LOWER(p.nome) LIKE LOWER(concat('%', :nome, '%')) ORDER BY p.nome")
    Page<Pessoa> findByNomeContainingIgnoreCaseOrderByNomeAsc(@Param("nome") String nome, Pageable pageable);
}
