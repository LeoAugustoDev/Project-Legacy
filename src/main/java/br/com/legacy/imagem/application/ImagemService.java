package br.com.legacy.imagem.application;

import br.com.legacy.handler.APIException;
import br.com.legacy.imagem.api.ImagemArquivoResponse;
import br.com.legacy.imagem.api.ImagemResponse;
import br.com.legacy.imagem.domain.ImagemAnalise;
import br.com.legacy.imagem.infra.ImagemAnaliseRepository;
import br.com.legacy.usuario.domain.Usuario;
import br.com.legacy.usuario.infra.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImagemService {

    private static final Set<String> CONTENT_TYPES_PERMITIDOS = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> EXTENSOES_PERMITIDAS = Set.of(
            "jpg",
            "jpeg",
            "png",
            "webp"
    );

    private final UsuarioRepository usuarioRepository;
    private final ImagemAnaliseRepository imagemAnaliseRepository;

    @Value("${legacy.upload.imagens.dir:uploads/imagens}")
    private String diretorioImagens;

    @Value("${legacy.upload.imagens.max-size-bytes:5242880}")
    private Long tamanhoMaximoBytes;

    @Transactional
    public ImagemResponse enviaImagem(String email, MultipartFile arquivo) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        validaArquivo(arquivo);

        String nomeOriginal = nomeArquivoSeguro(arquivo.getOriginalFilename());
        String extensao = extensao(nomeOriginal);
        String nomeArmazenado = UUID.randomUUID() + "." + extensao;
        Path destino = diretorioBase().resolve(nomeArmazenado).normalize();

        salvaArquivo(arquivo, destino);

        ImagemAnalise imagem = imagemAnaliseRepository.save(ImagemAnalise.builder()
                .usuario(usuario)
                .nomeArquivo(nomeOriginal)
                .nomeArquivoArmazenado(nomeArmazenado)
                .caminhoArquivo(destino.toString())
                .contentType(arquivo.getContentType())
                .tamanho(arquivo.getSize())
                .build());

        return new ImagemResponse(imagem);
    }

    @Transactional(readOnly = true)
    public List<ImagemResponse> listaImagens(String email) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        return imagemAnaliseRepository.findByUsuarioOrderByCriadoEmDesc(usuario).stream()
                .map(ImagemResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ImagemResponse buscaImagem(String email, UUID idImagem) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        ImagemAnalise imagem = buscaImagemDoUsuario(idImagem, usuario);

        return new ImagemResponse(imagem);
    }

    @Transactional(readOnly = true)
    public ImagemArquivoResponse buscaArquivo(String email, UUID idImagem) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        ImagemAnalise imagem = buscaImagemDoUsuario(idImagem, usuario);
        Path caminho = Path.of(imagem.getCaminhoArquivo()).normalize();

        try {
            Resource resource = new UrlResource(caminho.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Arquivo de imagem não encontrado."
                );
            }

            return new ImagemArquivoResponse(
                    resource,
                    imagem.getNomeArquivo(),
                    imagem.getContentType(),
                    imagem.getTamanho()
            );
        } catch (MalformedURLException ex) {
            throw APIException.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível carregar o arquivo de imagem.",
                    ex
            );
        }
    }

    private void validaArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw APIException.build(
                    HttpStatus.BAD_REQUEST,
                    "Arquivo de imagem é obrigatório."
            );
        }

        if (arquivo.getSize() > tamanhoMaximoBytes) {
            throw APIException.build(
                    HttpStatus.BAD_REQUEST,
                    "Arquivo excede o tamanho máximo permitido."
            );
        }

        String contentType = arquivo.getContentType();
        if (!StringUtils.hasText(contentType) || !CONTENT_TYPES_PERMITIDOS.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw APIException.build(
                    HttpStatus.BAD_REQUEST,
                    "Formato de imagem não permitido."
            );
        }

        String extensao = extensao(nomeArquivoSeguro(arquivo.getOriginalFilename()));
        if (!EXTENSOES_PERMITIDAS.contains(extensao)) {
            throw APIException.build(
                    HttpStatus.BAD_REQUEST,
                    "Extensão de imagem não permitida."
            );
        }
    }

    private void salvaArquivo(MultipartFile arquivo, Path destino) {
        try {
            Files.createDirectories(destino.getParent());
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw APIException.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível salvar a imagem.",
                    ex
            );
        }
    }

    private Path diretorioBase() {
        return Path.of(diretorioImagens).toAbsolutePath().normalize();
    }

    private String nomeArquivoSeguro(String nomeArquivo) {
        String nome = Path.of(StringUtils.hasText(nomeArquivo) ? nomeArquivo : "imagem").getFileName().toString();

        return nome.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String extensao(String nomeArquivo) {
        int indice = nomeArquivo.lastIndexOf('.');

        if (indice < 0 || indice == nomeArquivo.length() - 1) {
            return "";
        }

        return nomeArquivo.substring(indice + 1).toLowerCase(Locale.ROOT);
    }

    private ImagemAnalise buscaImagemDoUsuario(UUID idImagem, Usuario usuario) {
        return imagemAnaliseRepository.findByIdImagemAndUsuario(idImagem, usuario)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Imagem não encontrada."
                ));
    }

    private Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }
}
