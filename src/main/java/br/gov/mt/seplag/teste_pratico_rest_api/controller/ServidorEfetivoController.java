package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.ServidorEfetivoDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivo;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.ServidorEfetivoPK;
import br.gov.mt.seplag.teste_pratico_rest_api.service.PessoaService;
import br.gov.mt.seplag.teste_pratico_rest_api.service.ServidorEfetivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/protected/api/servidores-efetivos")
public class ServidorEfetivoController {

    @Autowired
    private ServidorEfetivoService servidorEfetivoService;
    @Autowired
    private PessoaService pessoaService;

    // Endpoint para ExtJS com paginação
    @GetMapping(params = {"_dc", "page", "start", "limit"})
    public ResponseEntity<Map<String, Object>> listarPaginadoExtJS(
            @RequestParam long _dc,
            @RequestParam int page,
            @RequestParam int start,
            @RequestParam int limit) {

        Page<ServidorEfetivo> result = servidorEfetivoService.listarPaginado(
                PageRequest.of(start / limit, limit));

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listarTodos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        Page<ServidorEfetivo> result = servidorEfetivoService.listarTodos(
                PageRequest.of(page, size));

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServidorEfetivo> buscarPorId(@PathVariable Long id, @PathVariable String matricula) {

        ServidorEfetivoPK servidorEfetivoPK = new ServidorEfetivoPK(id, matricula);
        ServidorEfetivo servidor = servidorEfetivoService.buscarPorId(servidorEfetivoPK);
        return ResponseEntity.ok(servidor);
    }

    @PostMapping
    public ResponseEntity<ServidorEfetivo> criar(@RequestBody ServidorEfetivoDTO dto) {
        ServidorEfetivo servidorEfetivo = new ServidorEfetivo();
        servidorEfetivo.setMatricula(dto.getMatricula());
        Pessoa pessoa = pessoaService.buscarPorId(dto.getPessoa());
        servidorEfetivo.setPessoa(pessoa);

        ServidorEfetivo novo = servidorEfetivoService.salvar(servidorEfetivo);
        return ResponseEntity.ok(novo);
    }

    @PutMapping
    public ResponseEntity<ServidorEfetivo> atualizar(@RequestBody ServidorEfetivoDTO dto) {
        // Validação dos dados
        if (dto.getMatriculaAntiga() == null || dto.getPessoaAntiga() == null) {
            throw new IllegalArgumentException("Matrícula antiga e pessoa antiga são obrigatórias para atualização");
        }

        // Busca o servidor existente usando a chave antiga
        ServidorEfetivo servidorExistente = servidorEfetivoService.buscarPorPessoaEMatricula(dto.getPessoaAntiga(), dto.getMatriculaAntiga());

        // Verifica se está tentando alterar a chave primária
        boolean alterouChave = !dto.getMatriculaAntiga().equals(dto.getMatricula()) ||
                !dto.getPessoaAntiga().equals(dto.getPessoa());

        if (alterouChave) {
            // Verifica se a nova chave já existe
            if (servidorEfetivoService.existeServidorComChave(dto.getPessoa(), dto.getMatricula())) {
                throw new RuntimeException("Já existe um servidor com esta matrícula e pessoa");
            }

            // Remove o registro antigo e cria um novo
            servidorEfetivoService.deletarPorPessoaEMatricula(dto.getPessoaAntiga(), dto.getMatriculaAntiga());
            servidorExistente = new ServidorEfetivo(); // Cria nova instância
        }

        // Atualiza os campos
        servidorExistente.setMatricula(dto.getMatricula());
        servidorExistente.setPessoa(pessoaService.buscarPorId(dto.getPessoa()));

        ServidorEfetivo atualizado = servidorEfetivoService.salvar(servidorExistente);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletar(
            @RequestParam String matricula,
            @RequestParam Long pessoaId) {
        servidorEfetivoService.deletar(pessoaId, matricula);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-matricula")
    public ResponseEntity<List<ServidorEfetivo>> buscarPorMatricula(
            @RequestParam String matricula) {

        List<ServidorEfetivo> result = servidorEfetivoService.buscarPorMatricula(matricula);
        return ResponseEntity.ok(result);
    }
}