package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.ServidorTemporarioDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivo;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorTemporario;
import br.gov.mt.seplag.teste_pratico_rest_api.service.ServidorTemporarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/protected/api/servidores-temporarios")
public class ServidorTemporarioController {

    @Autowired
    private ServidorTemporarioService servidorTemporarioService;

    // Endpoint para ExtJS com paginação
    @GetMapping(params = {"_dc", "page", "start", "limit"})
    public ResponseEntity<Map<String, Object>> listarPaginadoExtJS(
            @RequestParam long _dc,
            @RequestParam int page,
            @RequestParam int start,
            @RequestParam int limit) {

        Page<ServidorTemporario> result = servidorTemporarioService.listarPaginado(PageRequest.of(start / limit, limit));

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    @GetMapping
    public ResponseEntity<Page<ServidorTemporario>> listarTodos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<ServidorTemporario> result = servidorTemporarioService.listarTodos(PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar-por-pessoa")
    public ResponseEntity<Page<ServidorTemporario>> buscarPorPessoa(
            @RequestParam Long pessoaId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<ServidorTemporario> result = servidorTemporarioService.buscarPorPessoa(pessoaId, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar-por-periodo")
    public ResponseEntity<Page<ServidorTemporario>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataFim,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<ServidorTemporario> result = servidorTemporarioService
                .buscarPorPeriodoAdmissao(dataInicio, dataFim, PageRequest.of(page, size));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/buscar")
    public ResponseEntity<ServidorTemporario> buscarPorPessoaEDataAdmissao(
            @RequestParam Long pessoaId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataAdmissao) {

        Optional<ServidorTemporario> result = servidorTemporarioService
                .buscarPorPessoaEDataAdmissao(pessoaId, dataAdmissao);

        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ServidorTemporario> criar(@RequestBody ServidorTemporarioDTO dto) {
        ServidorTemporario novoServidor = servidorTemporarioService.criar(dto);
        return ResponseEntity.ok(novoServidor);
    }

    @PutMapping
    public ResponseEntity<ServidorTemporario> atualizar(@RequestBody ServidorTemporarioDTO dto) {
        ServidorTemporario servidorAtualizado = servidorTemporarioService.atualizar(dto);
        return ResponseEntity.ok(servidorAtualizado);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar(
            @RequestParam Long pessoaId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date dataAdmissao) {

        servidorTemporarioService.deletar(pessoaId, dataAdmissao);
        return ResponseEntity.noContent().build();
    }
}