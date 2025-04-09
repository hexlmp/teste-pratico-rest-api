package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.UnidadeCreateDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Unidade;
import br.gov.mt.seplag.teste_pratico_rest_api.service.EnderecoService;
import br.gov.mt.seplag.teste_pratico_rest_api.service.UnidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/protected/api/unidades")
public class UnidadeController {

    @Autowired
    private UnidadeService unidadeService;
    @Autowired
    private EnderecoService enderecoService;

    /*@GetMapping
    public ResponseEntity<Page<Unidade>> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size) {
        return ResponseEntity.ok(unidadeService.listarPaginado(PageRequest.of(page, size)));
    }*/

    @GetMapping(params = {"_dc", "page", "start", "limit"})
    public ResponseEntity<Map<String, Object>> listarPaginadoExtJS(
            @RequestParam long _dc,
            @RequestParam int page,
            @RequestParam int start,
            @RequestParam int limit) {

        Page<Unidade> result = unidadeService.listarPaginado(PageRequest.of(start / limit, limit));

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<Unidade>> listarTodos() {
        return ResponseEntity.ok(unidadeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @GetMapping("/por-nome")
    public ResponseEntity<List<Unidade>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(unidadeService.buscarPorNome(nome));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody UnidadeCreateDTO dto) {
        if (dto != null)
        {
            // cria a Unidade
            Unidade unidade = new Unidade();
            unidade.setNome(dto.getNome());
            unidade.setSigla(dto.getSigla());

            // Vincula endereços
            dto.getEnderecos().forEach(enderecoId -> {
                Endereco endereco = enderecoService.findById(enderecoId);
                if (endereco == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Endereço não encontrado com ID: " + enderecoId
                    );
                }
                unidade.getEnderecos().add(endereco);
            });

            return ResponseEntity.ok(unidadeService.salvar(unidade));
        } else {
            return ResponseEntity.badRequest().body("Dados não enviados corretamente");
        }
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<Unidade> atualizar(@PathVariable Long id, @RequestBody Unidade unidade) {
        return ResponseEntity.ok(unidadeService.atualizar(id, unidade));
    }*/
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody UnidadeCreateDTO dto) {
        if (dto != null)
        {
            // cria a Unidade
            Unidade unidade = dto.toUnidade(unidadeService.buscarPorId(dto.getId()));

            // Vincula endereços
            dto.getEnderecos().forEach(enderecoId -> {
                Endereco endereco = enderecoService.findById(enderecoId);
                if (endereco == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Endereço não encontrado com ID: " + enderecoId
                    );
                }
                if (!unidade.getEnderecos().contains(endereco)) unidade.getEnderecos().add(endereco);
            });

            return ResponseEntity.ok(unidadeService.atualizar(dto.getId(), unidade));
        } else {
            return ResponseEntity.badRequest().body("Dados não enviados corretamente");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        unidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{unidadeId}/enderecos/{enderecoId}")
    public ResponseEntity<Unidade> adicionarEndereco(
            @PathVariable Long unidadeId,
            @PathVariable Long enderecoId) {
        return ResponseEntity.ok(unidadeService.adicionarEndereco(unidadeId, enderecoId));
    }

    @DeleteMapping("/{unidadeId}/enderecos/{enderecoId}")
    public ResponseEntity<Unidade> removerEndereco(
            @PathVariable Long unidadeId,
            @PathVariable Long enderecoId) {
        return ResponseEntity.ok(unidadeService.removerEndereco(unidadeId, enderecoId));
    }

    @GetMapping("/{unidadeId}/enderecos")
    public ResponseEntity<List<Endereco>> listarEnderecosDaUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(unidadeService.listarEnderecosDaUnidade(unidadeId));
    }
}