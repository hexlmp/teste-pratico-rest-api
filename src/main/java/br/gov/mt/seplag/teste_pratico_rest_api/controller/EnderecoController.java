package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.EnderecoCreateDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Cidade;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.service.CidadeService;
import br.gov.mt.seplag.teste_pratico_rest_api.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/protected/api/enderecos")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    CidadeService cidadeService;


    @GetMapping
    public List<Endereco> listarTodos() {
        return enderecoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Endereco> buscarPorId(@PathVariable Long id) {
        Endereco endereco = enderecoService.buscarPorId(id);
        return ResponseEntity.ok(endereco);
    }

    @GetMapping("/por-cidade/{cidadeId}")
    public List<Endereco> buscarPorCidade(@PathVariable Long cidadeId) {
        return enderecoService.buscarPorCidade(cidadeId);
    }

    @PostMapping
    public ResponseEntity<Endereco> criar(@RequestBody EnderecoCreateDTO enderecoDTO) {
        Endereco endereco = new Endereco();
        Cidade cidade = cidadeService.buscarPorId(enderecoDTO.getCidade());
        endereco.setCidade(cidade);
        return ResponseEntity.ok(enderecoService.salvar( enderecoDTO.toEndereco(endereco)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Endereco> atualizar(@PathVariable Long id, @RequestBody EnderecoCreateDTO enderecoDTO) {
        Endereco endereco = enderecoService.findById(id);
        Cidade cidade = cidadeService.buscarPorId(enderecoDTO.getCidade());
        endereco.setCidade(cidade);
        Endereco atualizado = enderecoService.atualizar(id, endereco);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        enderecoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Endereco>> buscarEnderecos(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(enderecoService.buscarEnderecos(query, limit));
    }
}