package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.LotacaoCreateDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Lotacao;
import br.gov.mt.seplag.teste_pratico_rest_api.service.LotacaoService;
import br.gov.mt.seplag.teste_pratico_rest_api.service.PessoaService;
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
@RequestMapping("/protected/api/lotacoes")
public class LotacaoController {

    @Autowired
    private LotacaoService lotacaoService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    private UnidadeService unidadeService;

    @GetMapping(params = {"_dc", "page", "start", "limit"})
    public ResponseEntity<Map<String, Object>> listarPaginadoExtJS(
            @RequestParam long _dc,
            @RequestParam int page,
            @RequestParam int start,
            @RequestParam int limit) {

        Page<Lotacao> result = lotacaoService.listarPaginado(PageRequest.of(start / limit, limit));

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    @GetMapping("/todas")
    public ResponseEntity<List<Lotacao>> listarTodos() {
        return ResponseEntity.ok(lotacaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lotacao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(lotacaoService.buscarPorId(id));
    }

    @GetMapping("/por-pessoa/{pessoaId}")
    public ResponseEntity<List<Lotacao>> buscarPorPessoa(@PathVariable Long pessoaId) {
        return ResponseEntity.ok(lotacaoService.buscarPorPessoa(pessoaId));
    }

    @GetMapping("/por-unidade/{unidadeId}")
    public ResponseEntity<List<Lotacao>> buscarPorUnidade(@PathVariable Long unidadeId) {
        return ResponseEntity.ok(lotacaoService.buscarPorUnidade(unidadeId));
    }

    @PostMapping
    public ResponseEntity<Lotacao> criar(@RequestBody LotacaoCreateDTO dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados não enviados corretamente");
        }

        Lotacao lotacao = new Lotacao();
        lotacao.setPessoa(pessoaService.buscarPorId(dto.getPessoaId()));
        lotacao.setUnidade(unidadeService.buscarPorId(dto.getUnidadeId()));
        lotacao.setDataLotacao(dto.getDataLotacao());
        lotacao.setDataRemocao(dto.getDataRemocao());
        lotacao.setPortaria(dto.getPortaria());

        return ResponseEntity.ok(lotacaoService.salvar(lotacao));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Lotacao> atualizar(@PathVariable Long id, @RequestBody LotacaoCreateDTO dto) {
        Lotacao lotacaoExistente = lotacaoService.buscarPorId(id);
        lotacaoExistente.setPessoa(pessoaService.buscarPorId(dto.getPessoaId()));
        lotacaoExistente.setUnidade(unidadeService.buscarPorId(dto.getUnidadeId()));
        lotacaoExistente.setDataLotacao(dto.getDataLotacao());
        lotacaoExistente.setDataRemocao(dto.getDataRemocao());
        lotacaoExistente.setPortaria(dto.getPortaria());

        return ResponseEntity.ok(lotacaoService.atualizar(id, lotacaoExistente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        lotacaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}