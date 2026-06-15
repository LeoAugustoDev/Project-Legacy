package br.com.legacy.analise.application;

import br.com.legacy.analise.api.AnaliseVisualRequest;
import br.com.legacy.analise.api.AnaliseVisualResponse;
import br.com.legacy.analise.domain.AnaliseVisual;
import br.com.legacy.analise.infra.AnaliseVisualRepository;
import br.com.legacy.handler.APIException;
import br.com.legacy.imagem.domain.ImagemAnalise;
import br.com.legacy.imagem.infra.ImagemAnaliseRepository;
import br.com.legacy.perfil.domain.Perfil;
import br.com.legacy.perfil.infra.PerfilRepository;
import br.com.legacy.usuario.domain.Usuario;
import br.com.legacy.usuario.infra.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnaliseVisualService {

    private static final String DEFAULT_GROQ_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String DEFAULT_GROQ_VISION_MODEL = "meta-llama/llama-4-scout-17b-16e-instruct";
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final ImagemAnaliseRepository imagemAnaliseRepository;
    private final AnaliseVisualRepository analiseVisualRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    @Value("${legacy.ia.groq.vision-model:meta-llama/llama-4-scout-17b-16e-instruct}")
    private String visionModel;

    @Value("${legacy.ia.groq.vision-temperature:0.4}")
    private Double visionTemperature;

    @Value("${legacy.ia.groq.vision-max-tokens:700}")
    private Integer visionMaxTokens;

    @Value("${legacy.analise-visual.max-image-size-bytes:3145728}")
    private Long tamanhoMaximoAnaliseBytes;

    @Transactional
    public AnaliseVisualResponse geraAnalise(String email, UUID idImagem, AnaliseVisualRequest request) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        ImagemAnalise imagem = buscaImagemDoUsuario(idImagem, usuario);
        Optional<Perfil> perfil = perfilRepository.findByUsuario(usuario);

        validaImagemParaAnalise(imagem);

        String resultado = chamaGroqVision(imagem, perfil, objetivoDaRequest(request));

        AnaliseVisual analise = analiseVisualRepository.save(AnaliseVisual.builder()
                .usuario(usuario)
                .imagemAnalise(imagem)
                .resultado(resultado)
                .build());

        return new AnaliseVisualResponse(analise);
    }

    @Transactional(readOnly = true)
    public List<AnaliseVisualResponse> listaAnalises(String email) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        return analiseVisualRepository.findByUsuarioOrderByCriadoEmDesc(usuario).stream()
                .map(AnaliseVisualResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AnaliseVisualResponse> listaAnalisesDaImagem(String email, UUID idImagem) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        ImagemAnalise imagem = buscaImagemDoUsuario(idImagem, usuario);

        return analiseVisualRepository.findByImagemAnaliseAndUsuarioOrderByCriadoEmDesc(imagem, usuario).stream()
                .map(AnaliseVisualResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnaliseVisualResponse buscaAnalise(String email, UUID idAnalise) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        AnaliseVisual analise = analiseVisualRepository.findByIdAnaliseAndUsuario(idAnalise, usuario)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Análise visual não encontrada."
                ));

        return new AnaliseVisualResponse(analise);
    }

    private String chamaGroqVision(ImagemAnalise imagem, Optional<Perfil> perfil, String objetivo) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("model", modeloVisionLimpo());
        request.put("messages", List.of(Map.of(
                "role", "user",
                "content", List.of(
                        Map.of("type", "text", "text", promptAnaliseVisual(perfil, objetivo)),
                        Map.of("type", "image_url", "image_url", Map.of("url", imagemBase64(imagem)))
                )
        )));
        request.put("temperature", visionTemperature);
        request.put("max_completion_tokens", visionMaxTokens);
        request.put("n", 1);

        try {
            String responseBody = RestClient.builder()
                    .baseUrl(baseUrlLimpa())
                    .build()
                    .post()
                    .uri(CHAT_COMPLETIONS_PATH)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKeyLimpa())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (requestSpec, response) -> {
                        String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                        throw APIException.build(
                                HttpStatus.BAD_GATEWAY,
                                "Erro ao chamar Groq Vision: " + mensagemErroGroq(errorBody)
                        );
                    })
                    .body(String.class);

            return extraiResposta(responseBody);
        } catch (APIException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw APIException.build(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível gerar análise visual no momento.",
                    ex
            );
        }
    }

    private String promptAnaliseVisual(Optional<Perfil> perfil, String objetivo) {
        return """
                Você é o consultor visual do LEGACY, especialista em imagem masculina, elegância e presença executiva.
                Analise a imagem com respeito e foco em estilo, postura, caimento, harmonia visual, cores e adequação ao contexto.
                Não identifique pessoa, idade, etnia, saúde, atratividade ou características sensíveis.
                Responda em português do Brasil, em JSON válido, com as chaves: resumo, pontosFortes, ajustesPrioritarios, recomendacoes, cores, postura, proximosPassos.
                Use listas curtas e recomendações práticas.

                Perfil do usuário:
                %s

                Objetivo informado para esta análise:
                %s
                """.formatted(perfilResumido(perfil), valorOuNaoInformado(objetivo));
    }

    private String perfilResumido(Optional<Perfil> perfil) {
        return perfil
                .map(perfilEncontrado -> """
                        Profissão: %s
                        Objetivo de imagem: %s
                        Estilo desejado: %s
                        Contexto de uso: %s
                        Cores preferidas: %s
                        Restrições: %s
                        """.formatted(
                        perfilEncontrado.getProfissao(),
                        perfilEncontrado.getObjetivoImagem(),
                        perfilEncontrado.getEstiloDesejado(),
                        perfilEncontrado.getContextoUso(),
                        valorOuNaoInformado(perfilEncontrado.getCoresPreferidas()),
                        valorOuNaoInformado(perfilEncontrado.getRestricoes())
                ))
                .orElse("Perfil ainda não preenchido.");
    }

    private String imagemBase64(ImagemAnalise imagem) {
        try {
            byte[] bytes = Files.readAllBytes(Path.of(imagem.getCaminhoArquivo()).normalize());
            String base64 = Base64.getEncoder().encodeToString(bytes);

            return "data:" + imagem.getContentType() + ";base64," + base64;
        } catch (IOException ex) {
            throw APIException.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Não foi possível ler a imagem para análise visual.",
                    ex
            );
        }
    }

    private void validaImagemParaAnalise(ImagemAnalise imagem) {
        if (imagem.getTamanho() > tamanhoMaximoAnaliseBytes) {
            throw APIException.build(
                    HttpStatus.BAD_REQUEST,
                    "Imagem excede o tamanho máximo para análise visual."
            );
        }

        if (!Files.exists(Path.of(imagem.getCaminhoArquivo()).normalize())) {
            throw APIException.build(
                    HttpStatus.NOT_FOUND,
                    "Arquivo de imagem não encontrado."
            );
        }
    }

    private String extraiResposta(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String resposta = root.path("choices").path(0).path("message").path("content").asText(null);

            if (!StringUtils.hasText(resposta)) {
                throw APIException.build(
                        HttpStatus.BAD_GATEWAY,
                        "Resposta inválida do serviço de análise visual."
                );
            }

            return resposta;
        } catch (JsonProcessingException ex) {
            throw APIException.build(
                    HttpStatus.BAD_GATEWAY,
                    "Resposta inválida do serviço de análise visual.",
                    ex
            );
        }
    }

    private String mensagemErroGroq(String responseBody) {
        try {
            JsonNode error = objectMapper.readTree(responseBody).path("error");
            String mensagem = error.path("message").asText();
            String codigo = error.path("code").asText();

            if (StringUtils.hasText(codigo)) {
                return mensagem + " (" + codigo + ")";
            }

            return StringUtils.hasText(mensagem) ? mensagem : "erro não identificado.";
        } catch (JsonProcessingException ex) {
            return "erro não identificado.";
        }
    }

    private String apiKeyLimpa() {
        String chave = valorLimpo(apiKey);

        if (chave.toLowerCase().startsWith("bearer ")) {
            chave = chave.substring("bearer ".length()).trim();
        }

        int separador = chave.indexOf(';');
        if (separador >= 0) {
            chave = chave.substring(0, separador).trim();
        }

        if (!StringUtils.hasText(chave)) {
            throw APIException.build(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "GROQ_API_KEY não configurada."
            );
        }

        return chave;
    }

    private String baseUrlLimpa() {
        String url = valorLimpo(baseUrl);

        int separador = url.indexOf(';');
        if (separador >= 0) {
            url = url.substring(0, separador).trim();
        }

        if (!StringUtils.hasText(url)) {
            url = DEFAULT_GROQ_BASE_URL;
        }

        if (url.endsWith(CHAT_COMPLETIONS_PATH)) {
            url = url.substring(0, url.length() - CHAT_COMPLETIONS_PATH.length());
        }

        while (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        return url;
    }

    private String modeloVisionLimpo() {
        String modelo = valorLimpo(visionModel);

        int separador = modelo.indexOf(';');
        if (separador >= 0) {
            modelo = modelo.substring(0, separador).trim();
        }

        return StringUtils.hasText(modelo) ? modelo : DEFAULT_GROQ_VISION_MODEL;
    }

    private String valorLimpo(String valor) {
        if (valor == null) {
            return "";
        }

        String valorLimpo = valor.trim();
        if ((valorLimpo.startsWith("\"") && valorLimpo.endsWith("\""))
                || (valorLimpo.startsWith("'") && valorLimpo.endsWith("'"))) {
            return valorLimpo.substring(1, valorLimpo.length() - 1).trim();
        }

        return valorLimpo;
    }

    private String objetivoDaRequest(AnaliseVisualRequest request) {
        if (request != null && StringUtils.hasText(request.getObjetivo())) {
            return request.getObjetivo().trim();
        }

        return "Análise geral de imagem pessoal masculina.";
    }

    private String valorOuNaoInformado(String valor) {
        return StringUtils.hasText(valor) ? valor : "Não informado";
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
