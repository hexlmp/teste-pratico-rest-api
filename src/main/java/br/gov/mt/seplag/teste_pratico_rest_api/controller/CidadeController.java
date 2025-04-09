package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.Cidade;
import br.gov.mt.seplag.teste_pratico_rest_api.service.CidadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protected/api/cidades")
public class CidadeController {

    @Autowired
    private CidadeService cidadeService;

    @GetMapping
    public List<Cidade> listarTodos() {
        return cidadeService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cidade> buscarPorId(@PathVariable Long id) {
        Cidade cidade = cidadeService.buscarPorId(id);
        return ResponseEntity.ok(cidade);
    }

    @GetMapping("/por-uf/{uf}")
    public List<Cidade> buscarPorUf(@PathVariable String uf) {
        return cidadeService.buscarPorUf(uf);
    }

    @PostMapping
    public Cidade criar(@RequestBody Cidade cidade) {
        return cidadeService.salvar(cidade);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cidade> atualizar(@PathVariable Long id, @RequestBody Cidade cidade) {
        Cidade atualizada = cidadeService.atualizar(id, cidade);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cidadeService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}