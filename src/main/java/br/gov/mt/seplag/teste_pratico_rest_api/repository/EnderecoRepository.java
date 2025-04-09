package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    // Consultas customizadas podem ser adicionadas aqui
    List<Endereco> findByCidadeId(Long cidadeId);

    /**
     * Busca endereços por termo de pesquisa em logradouro, bairro ou cidade
     * @param termoPesquisa Termo a ser buscado (com wildcards)
     * @param limit Limite de resultados
     * @return Lista de endereços correspondentes
     */
    @Query("SELECT e FROM Endereco e " +
            "WHERE LOWER(e.logradouro) LIKE LOWER(:termoPesquisa) " +
            "OR LOWER(e.bairro) LIKE LOWER(:termoPesquisa) " +
            "OR LOWER(e.cidade.nome) LIKE LOWER(:termoPesquisa) " +
            "ORDER BY e.logradouro ASC")
    List<Endereco> findBySearchTerm(@Param("termoPesquisa") String termoPesquisa,
                                    @Param("limit") int limit);
}