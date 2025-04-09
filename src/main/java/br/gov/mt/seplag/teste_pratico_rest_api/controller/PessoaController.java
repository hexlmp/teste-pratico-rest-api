package br.gov.mt.seplag.teste_pratico_rest_api.controller;

import br.gov.mt.seplag.teste_pratico_rest_api.dto.PessoaCreateDTO;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Endereco;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.FotoPessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.entity.Pessoa;
import br.gov.mt.seplag.teste_pratico_rest_api.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/protected/api/pessoas")
public class PessoaController {

    // Min.IO
    @Autowired
    private MinioService minioService;

    @Autowired
    private PessoaService pessoaService;

    @Autowired
    FotoPessoaService fotoPessoaService;

    @Autowired
    EnderecoService enderecoService;

    @Autowired
    private UploadTemporarioService uploadTemporarioService;


    // _dc=1743552429803&search=Luciano M.j&page=1&start=0&limit=10
    // Endpoint principal para ExtJS (com parâmetros específicos)
    @GetMapping(params = {"_dc", "search", "page", "start", "limit"})
    public ResponseEntity<Map<String, Object>> searchPaginadoExtJS(
            @RequestParam long _dc,
            @RequestParam String search,
            @RequestParam int page,
            @RequestParam int start,
            @RequestParam int limit) {

        Page<Pessoa> result = pessoaService.findByNamePaginado(PageRequest.of(start / limit, limit), search);

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    // Endpoint principal para ExtJS (com parâmetros específicos)
    @GetMapping(params = {"_dc", "page", "start", "limit"})
    public ResponseEntity<Map<String, Object>> listarPaginadoExtJS(
            @RequestParam long _dc,
            @RequestParam int page,
            @RequestParam int start,
            @RequestParam int limit) {

        Page<Pessoa> result = pessoaService.listarPaginado(
                PageRequest.of(start / limit, limit));

        return ResponseEntity.ok(Map.of(
                "data", result.getContent(),
                "total", result.getTotalElements(),
                "success", true
        ));
    }

    @GetMapping
    public List<Pessoa> listarTodos(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        return pessoaService.listarTodos(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pessoa> buscarPorId(@PathVariable Long id) {
        Pessoa pessoa = pessoaService.buscarPorId(id);
        return ResponseEntity.ok(pessoa);
    }

    /*@PostMapping
    public Pessoa criar(@RequestBody Pessoa pessoa) {
        return pessoaService.salvar(pessoa);
    }*/

    /*@PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long id, @RequestBody Pessoa pessoa) {
        Pessoa atualizada = pessoaService.atualizar(id, pessoa);
        return ResponseEntity.ok(atualizada);
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pessoaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar-por-nome")
    public List<Pessoa> buscarPorNome(@RequestParam String nome) {
        return pessoaService.buscarPorNome(nome);
    }

    @GetMapping("/unidade/{unidId}")
    public List<Pessoa> buscarPessoasPorUnidade(@PathVariable Long unidId) {
        return pessoaService.buscarPessoasPorUnidade(unidId);
    }

    @GetMapping("/list")
    public List<Pessoa> listarPessoas() {
        return pessoaService.listarPessoas();
    }

    /*
        ###########
        Min.IO
        ###########
    */
    @PostMapping("/{id}/fotos")
    public ResponseEntity<FotoPessoa> uploadFoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws Exception {

        Pessoa pessoa = pessoaService.buscarPorId(id);

        // Gera um nome único para o arquivo
        String objectName = "pessoa-" + id + "-" + System.currentTimeMillis() +
                getFileExtension(file.getOriginalFilename());

        // Faz upload para o Min.IO
        minioService.uploadFile(objectName, file);

        // Cria registro da foto no banco de dados
        FotoPessoa foto = new FotoPessoa();
        foto.setPessoa(pessoa);
        foto.setData(new Date());
        foto.setBucket(minioService.getBucketName());
        foto.setHash(objectName);

        // Salva a foto (você precisará criar um serviço/repositório para FotoPessoa)
        foto = fotoPessoaService.salvar(foto);

        return ResponseEntity.ok(foto);
    }

    @GetMapping("/{id}/fotos")
    public ResponseEntity<List<Map<String, String>>> listarFotos(@PathVariable Long id) throws Exception {
        Pessoa pessoa = pessoaService.buscarPorId(id);
        List<FotoPessoa> fotos = fotoPessoaService.buscarPorPessoa(pessoa.getId());

        List<Map<String, String>> response = new ArrayList<>();

        for (FotoPessoa foto : fotos) {
            Map<String, String> fotoMap = new HashMap<>();
            fotoMap.put("id", foto.getId().toString());
            fotoMap.put("url", minioService.getPresignedUrl(foto.getHash()));
            fotoMap.put("data", foto.getData().toString());
            response.add(fotoMap);
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/fotos/{fotoId}")
    public ResponseEntity<Void> deletarFoto(@PathVariable Long fotoId) throws Exception {
        FotoPessoa foto = fotoPessoaService.buscarPorId(fotoId);
        minioService.deleteFile(foto.getHash());
        fotoPessoaService.deletar(fotoId);
        return ResponseEntity.noContent().build();
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    // Endpoint para upload temporário
    @PostMapping("/temp-upload")
    public ResponseEntity<Map<String, String>> uploadTemporario(@RequestParam("file") MultipartFile file) throws Exception {
        String tempObjectName = uploadTemporarioService.uploadTemporario(file);
        String tempUrl = uploadTemporarioService.getTempUrl(tempObjectName);

        return ResponseEntity.ok(Map.of(
                "tempId", tempObjectName,
                "url", tempUrl
        ));
    }

    // Endpoint para remover upload temporário
    @DeleteMapping("/temp-upload/{tempId}")
    public ResponseEntity<Void> removerTemporario(@PathVariable String tempId) throws Exception {
        uploadTemporarioService.removerTemporario(tempId);
        return ResponseEntity.noContent().build();
    }

    // Atualizar o endpoint de criação de pessoa
    @PostMapping
    public ResponseEntity<Pessoa> criar(@RequestBody PessoaCreateDTO pessoaDTO) throws Exception {
        Pessoa pessoa = pessoaService.salvar(pessoaDTO.toPessoa());

        // Mover fotos temporárias para definitivas
        for (String tempId : pessoaDTO.getTempFotoIds()) {
            String objectName = "pessoa-" + pessoa.getId() + "-" + UUID.randomUUID();
            uploadTemporarioService.moverParaDefinitivo(tempId, objectName);

            // Criar registro da foto no banco
            FotoPessoa foto = new FotoPessoa();
            foto.setPessoa(pessoa);
            foto.setData(new Date());
            foto.setBucket(minioService.getBucketName());
            foto.setHash(objectName);
            fotoPessoaService.salvar(foto);
        }

        // Vincula endereços
        pessoaDTO.getEnderecos().forEach(enderecoId -> {
            Endereco endereco = enderecoService.findById(enderecoId);
            if (endereco == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Endereço não encontrado com ID: " + enderecoId
                );
            }
            pessoa.getEnderecos().add(endereco);
        });

        pessoaService.atualizar(pessoa.getId(), pessoa);

        return ResponseEntity.ok(pessoa);
    }

    // Atualizar o endpoint de criação de pessoa
    @PutMapping("/{id}")
    public ResponseEntity<Pessoa> atualizar(@PathVariable Long id, @RequestBody PessoaCreateDTO pessoaDTO) throws Exception {
        Pessoa pessoa = pessoaService.buscarPorId(id);

        if (pessoa != null)
        {
            pessoaService.salvar(pessoaDTO.toPessoa(pessoa));

            // Mover fotos temporárias para definitivas
            for (String tempId : pessoaDTO.getTempFotoIds())
            {
                if (!StringUtils.isEmpty(tempId))
                {
                    String objectName = "pessoa-" + pessoa.getId() + "-" + UUID.randomUUID();
                    uploadTemporarioService.moverParaDefinitivo(tempId, objectName);

                    // Criar registro da foto no banco
                    FotoPessoa foto = new FotoPessoa();
                    foto.setPessoa(pessoa);
                    foto.setData(new Date());
                    foto.setBucket(minioService.getBucketName());
                    foto.setHash(objectName);
                    fotoPessoaService.salvar(foto);
                }
            }

            // Vincula endereços
            pessoaDTO.getEnderecos().forEach(enderecoId -> {
                Endereco endereco = enderecoService.findById(enderecoId);
                if (endereco == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Endereço não encontrado com ID: " + enderecoId
                    );
                }
                if (!pessoa.getEnderecos().contains(endereco)) pessoa.getEnderecos().add(endereco);
            });

            pessoaService.atualizar(pessoa.getId(), pessoa);

        }

        return ResponseEntity.ok(pessoa);
    }

    /*############
    Endereço
    ############*/
    // Operações com Endereços
    @PostMapping("/{pessoaId}/enderecos/{enderecoId}")
    public Pessoa adicionarEndereco(@PathVariable Long pessoaId, @PathVariable Long enderecoId) {
        return pessoaService.adicionarEndereco(pessoaId, enderecoId);
    }

    @DeleteMapping("/{pessoaId}/enderecos/{enderecoId}")
    public Pessoa removerEndereco(@PathVariable Long pessoaId, @PathVariable Long enderecoId) {
        return pessoaService.removerEndereco(pessoaId, enderecoId);
    }

    @GetMapping("/{pessoaId}/enderecos")
    public List<Endereco> listarEnderecos(@PathVariable Long pessoaId) {
        return pessoaService.listarEnderecosDaPessoa(pessoaId);
    }

}
