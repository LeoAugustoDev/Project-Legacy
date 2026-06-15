package br.com.legacy.imagem.api;

import br.com.legacy.analise.api.AnaliseVisualRequest;
import br.com.legacy.analise.api.AnaliseVisualResponse;
import br.com.legacy.analise.application.AnaliseVisualService;
import br.com.legacy.imagem.application.ImagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/imagens")
@RequiredArgsConstructor
public class ImagemController {

    private final ImagemService imagemService;
    private final AnaliseVisualService analiseVisualService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ImagemResponse enviaImagem(
            Authentication authentication,
            @RequestParam("arquivo") MultipartFile arquivo
    ) {
        return imagemService.enviaImagem(authentication.getName(), arquivo);
    }

    @GetMapping
    public List<ImagemResponse> listaImagens(Authentication authentication) {
        return imagemService.listaImagens(authentication.getName());
    }

    @GetMapping("/{idImagem}")
    public ImagemResponse buscaImagem(
            Authentication authentication,
            @PathVariable UUID idImagem
    ) {
        return imagemService.buscaImagem(authentication.getName(), idImagem);
    }

    @GetMapping("/{idImagem}/arquivo")
    public ResponseEntity<Resource> buscaArquivo(
            Authentication authentication,
            @PathVariable UUID idImagem
    ) {
        ImagemArquivoResponse response = imagemService.buscaArquivo(authentication.getName(), idImagem);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .contentLength(response.getTamanho())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + response.getNomeArquivo() + "\"")
                .body(response.getArquivo());
    }

    @PostMapping("/{idImagem}/analise")
    public AnaliseVisualResponse geraAnalise(
            Authentication authentication,
            @PathVariable UUID idImagem,
            @Valid @RequestBody(required = false) AnaliseVisualRequest request
    ) {
        return analiseVisualService.geraAnalise(authentication.getName(), idImagem, request);
    }

    @GetMapping("/{idImagem}/analises")
    public List<AnaliseVisualResponse> listaAnalisesDaImagem(
            Authentication authentication,
            @PathVariable UUID idImagem
    ) {
        return analiseVisualService.listaAnalisesDaImagem(authentication.getName(), idImagem);
    }
}
