package br.gov.mt.seplag.teste_pratico_rest_api.repository;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Cidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CidadeRepository extends JpaRepository<Cidade, Long> {
    // Consultas customizadas podem ser adicionadas aqui
    List<Cidade> findByUf(String uf);
}