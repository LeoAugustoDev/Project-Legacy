package br.com.legacy.chat.application;

import br.com.legacy.chat.api.ChatRequest;
import br.com.legacy.chat.api.ChatResponse;
import br.com.legacy.handler.APIException;
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

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String DEFAULT_GROQ_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String DEFAULT_GROQ_MODEL = "openai/gpt-oss-120b";
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.groq.com/openai/v1}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.model:openai/gpt-oss-120b}")
    private String model;

    @Value("${spring.ai.openai.chat.temperature:0.7}")
    private Double temperature;

    @Value("${spring.ai.openai.chat.max-tokens:500}")
    private Integer maxTokens;

    @Transactional(readOnly = true)
    public ChatResponse enviaMensagem(String email, ChatRequest request) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        Optional<Perfil> perfil = perfilRepository.findByUsuario(usuario);

        String resposta = chamaGroq(promptSistema(), promptUsuario(usuario, perfil, request.getMensagem()));

        return new ChatResponse(resposta);
    }

    private String chamaGroq(String promptSistema, String promptUsuario) {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("model", modeloLimpo());
        request.put("messages", List.of(
                Map.of("role", "system", "content", promptSistema),
                Map.of("role", "user", "content", promptUsuario)
        ));
        request.put("temperature", temperature);
        request.put("max_tokens", maxTokens);
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
                                "Erro ao chamar Groq: " + mensagemErroGroq(errorBody)
                        );
                    })
                    .body(String.class);

            return extraiResposta(responseBody);
        } catch (APIException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw APIException.build(
                    HttpStatus.BAD_GATEWAY,
                    "Não foi possível gerar resposta da IA no momento.",
                    ex
            );
        }
    }

    private String promptSistema() {
        return """
                Você é o consultor de imagem masculina do LEGACY.
                Responda em português do Brasil.
                Seja elegante, direto, profissional e prático.
                Ajude com estilo, presença, imagem pessoal e adequação ao contexto.
                Não invente dados sobre o usuário.
                Não faça diagnóstico médico, psicológico ou jurídico.
                Dê recomendações aplicáveis e objetivas.
                """;
    }

    private String promptUsuario(Usuario usuario, Optional<Perfil> perfil, String mensagem) {
        return perfil
                .map(perfilEncontrado -> promptComPerfil(usuario, perfilEncontrado, mensagem))
                .orElseGet(() -> promptSemPerfil(usuario, mensagem));
    }

    private String promptComPerfil(Usuario usuario, Perfil perfil, String mensagem) {
        return """
                Usuário: %s

                Perfil:
                Profissão: %s
                Objetivo de imagem: %s
                Estilo desejado: %s
                Contexto de uso: %s
                Cores preferidas: %s
                Restrições: %s

                Mensagem do usuário:
                %s
                """.formatted(
                usuario.getNome(),
                perfil.getProfissao(),
                perfil.getObjetivoImagem(),
                perfil.getEstiloDesejado(),
                perfil.getContextoUso(),
                valorOuNaoInformado(perfil.getCoresPreferidas()),
                valorOuNaoInformado(perfil.getRestricoes()),
                mensagem
        );
    }

    private String promptSemPerfil(Usuario usuario, String mensagem) {
        return """
                Usuário: %s

                O usuário ainda não preencheu o perfil de imagem.
                Responda com orientação geral e, se útil, sugira preencher o perfil para recomendações mais precisas.

                Mensagem do usuário:
                %s
                """.formatted(usuario.getNome(), mensagem);
    }

    private String valorOuNaoInformado(String valor) {
        return StringUtils.hasText(valor) ? valor : "Não informado";
    }

    private String extraiResposta(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String resposta = root.path("choices").path(0).path("message").path("content").asText(null);

            if (!StringUtils.hasText(resposta)) {
                throw APIException.build(
                        HttpStatus.BAD_GATEWAY,
                        "Resposta inválida do serviço de IA."
                );
            }

            return resposta;
        } catch (JsonProcessingException ex) {
            throw APIException.build(
                    HttpStatus.BAD_GATEWAY,
                    "Resposta inválida do serviço de IA.",
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

    private String modeloLimpo() {
        String modelo = valorLimpo(model);

        int separador = modelo.indexOf(';');
        if (separador >= 0) {
            modelo = modelo.substring(0, separador).trim();
        }

        return StringUtils.hasText(modelo) ? modelo : DEFAULT_GROQ_MODEL;
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

    private Usuario buscaUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Usuário não encontrado."
                ));
    }
}
