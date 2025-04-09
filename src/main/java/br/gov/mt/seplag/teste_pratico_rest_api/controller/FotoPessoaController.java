package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.entity.FotoPessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.service.FotoPessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protected/api/fotos-pessoa")
public class FotoPessoaController {

    @Autowired
    private FotoPessoaService fotoPessoaService;

    @GetMapping
    public List<FotoPessoa> listarTodos() {
        return fotoPessoaService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FotoPessoa> buscarPorId(@PathVariable Long id) {
        FotoPessoa foto = fotoPessoaService.buscarPorId(id);
        return ResponseEntity.ok(foto);
    }

    @GetMapping("/por-pessoa/{pessoaId}")
    public List<FotoPessoa> buscarPorPessoa(@PathVariable Long pessoaId) {
        return fotoPessoaService.buscarPorPessoa(pessoaId);
    }

    @PostMapping
    public FotoPessoa criar(@RequestBody FotoPessoa fotoPessoa) {
        return fotoPessoaService.salvar(fotoPessoa);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FotoPessoa> atualizar(@PathVariable Long id, @RequestBody FotoPessoa fotoPessoa) {
        FotoPessoa atualizada = fotoPessoaService.atualizar(id, fotoPessoa);
        return ResponseEntity.ok(atualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        fotoPessoaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}