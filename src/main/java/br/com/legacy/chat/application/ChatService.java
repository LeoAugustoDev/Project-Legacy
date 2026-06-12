package br.com.legacy.chat.application;

import br.com.legacy.chat.api.ChatRequest;
import br.com.legacy.chat.api.ChatResponse;
import br.com.legacy.chat.api.ConversaRequest;
import br.com.legacy.chat.api.ConversaResponse;
import br.com.legacy.chat.api.ConversaResumoResponse;
import br.com.legacy.chat.domain.AutorMensagem;
import br.com.legacy.chat.domain.Conversa;
import br.com.legacy.chat.domain.Mensagem;
import br.com.legacy.chat.infra.ConversaRepository;
import br.com.legacy.chat.infra.MensagemRepository;
import br.com.legacy.handler.APIException;
import br.com.legacy.memoria.domain.MemoriaContexto;
import br.com.legacy.memoria.infra.MemoriaContextoRepository;
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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String DEFAULT_GROQ_BASE_URL = "https://api.groq.com/openai/v1";
    private static final String DEFAULT_GROQ_MODEL = "openai/gpt-oss-120b";
    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";
    private static final String TITULO_PADRAO = "Nova conversa";
    private static final int LIMITE_MEMORIA = 2000;

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final MemoriaContextoRepository memoriaContextoRepository;
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

    @Transactional
    public ChatResponse enviaMensagem(String email, ChatRequest request) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        Conversa conversa = novaConversa(usuario, tituloDaMensagem(request.getMensagem()));

        return enviaMensagem(conversa, usuario, request.getMensagem());
    }

    @Transactional(readOnly = true)
    public List<ConversaResumoResponse> listaConversas(String email) {
        Usuario usuario = buscaUsuarioPorEmail(email);

        return conversaRepository.findByUsuarioOrderByAtualizadoEmDesc(usuario).stream()
                .map(ConversaResumoResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ConversaResponse buscaConversa(String email, UUID idConversa) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        Conversa conversa = buscaConversaDoUsuario(idConversa, usuario);

        return montaConversaResponse(conversa);
    }

    @Transactional
    public ConversaResponse criaConversa(String email, ConversaRequest request) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        Conversa conversa = novaConversa(usuario, tituloDaRequest(request));

        return montaConversaResponse(conversa);
    }

    @Transactional
    public ChatResponse enviaMensagemNaConversa(String email, UUID idConversa, ChatRequest request) {
        Usuario usuario = buscaUsuarioPorEmail(email);
        Conversa conversa = buscaConversaDoUsuario(idConversa, usuario);

        return enviaMensagem(conversa, usuario, request.getMensagem());
    }

    private ChatResponse enviaMensagem(Conversa conversa, Usuario usuario, String mensagem) {
        Optional<Perfil> perfil = perfilRepository.findByUsuario(usuario);
        Optional<MemoriaContexto> memoria = memoriaContextoRepository.findByUsuario(usuario);
        List<Mensagem> ultimasMensagens = buscaUltimasMensagens(conversa);

        if (TITULO_PADRAO.equals(conversa.getTitulo())) {
            conversa.atualizarTitulo(tituloDaMensagem(mensagem));
        }

        mensagemRepository.save(Mensagem.builder()
                .conversa(conversa)
                .autor(AutorMensagem.USUARIO)
                .conteudo(mensagem)
                .build());

        String resposta = chamaGroq(promptSistema(), promptUsuario(usuario, perfil, memoria, ultimasMensagens, mensagem));

        mensagemRepository.save(Mensagem.builder()
                .conversa(conversa)
                .autor(AutorMensagem.IA)
                .conteudo(resposta)
                .build());

        conversa.marcarAtualizada();
        conversaRepository.save(conversa);
        atualizaMemoria(usuario, mensagem, resposta);

        return new ChatResponse(resposta);
    }

    private Conversa novaConversa(Usuario usuario, String titulo) {
        return conversaRepository.save(Conversa.builder()
                .usuario(usuario)
                .titulo(titulo)
                .build());
    }

    private ConversaResponse montaConversaResponse(Conversa conversa) {
        List<Mensagem> mensagens = mensagemRepository.findByConversaOrderByCriadoEmAsc(conversa);

        return new ConversaResponse(conversa, mensagens);
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

    private String promptUsuario(
            Usuario usuario,
            Optional<Perfil> perfil,
            Optional<MemoriaContexto> memoria,
            List<Mensagem> ultimasMensagens,
            String mensagem
    ) {
        return perfil
                .map(perfilEncontrado -> promptComPerfil(usuario, perfilEncontrado, memoria, ultimasMensagens, mensagem))
                .orElseGet(() -> promptSemPerfil(usuario, memoria, ultimasMensagens, mensagem));
    }

    private String promptComPerfil(
            Usuario usuario,
            Perfil perfil,
            Optional<MemoriaContexto> memoria,
            List<Mensagem> ultimasMensagens,
            String mensagem
    ) {
        return """
                Usuário: %s

                Perfil:
                Profissão: %s
                Objetivo de imagem: %s
                Estilo desejado: %s
                Contexto de uso: %s
                Cores preferidas: %s
                Restrições: %s

                Memória conhecida:
                %s

                Últimas mensagens relevantes:
                %s

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
                memoriaResumida(memoria),
                historicoCurto(ultimasMensagens),
                mensagem
        );
    }

    private String promptSemPerfil(
            Usuario usuario,
            Optional<MemoriaContexto> memoria,
            List<Mensagem> ultimasMensagens,
            String mensagem
    ) {
        return """
                Usuário: %s

                O usuário ainda não preencheu o perfil de imagem.
                Responda com orientação geral e, se útil, sugira preencher o perfil para recomendações mais precisas.

                Memória conhecida:
                %s

                Últimas mensagens relevantes:
                %s

                Mensagem do usuário:
                %s
                """.formatted(
                usuario.getNome(),
                memoriaResumida(memoria),
                historicoCurto(ultimasMensagens),
                mensagem
        );
    }

    private List<Mensagem> buscaUltimasMensagens(Conversa conversa) {
        return mensagemRepository.findTop5ByConversaOrderByCriadoEmDesc(conversa).stream()
                .sorted(Comparator.comparing(Mensagem::getCriadoEm))
                .toList();
    }

    private String memoriaResumida(Optional<MemoriaContexto> memoria) {
        return memoria
                .map(MemoriaContexto::getResumo)
                .filter(StringUtils::hasText)
                .orElse("Nenhuma memória persistida ainda.");
    }

    private String historicoCurto(List<Mensagem> mensagens) {
        if (mensagens.isEmpty()) {
            return "Nenhuma mensagem anterior nesta conversa.";
        }

        return mensagens.stream()
                .map(mensagem -> mensagem.getAutor() + ": " + limitaTexto(mensagem.getConteudo(), 300))
                .toList()
                .toString();
    }

    private void atualizaMemoria(Usuario usuario, String mensagemUsuario, String respostaIa) {
        Optional<MemoriaContexto> memoriaExistente = memoriaContextoRepository.findByUsuario(usuario);
        String novaEntrada = "Interação recente: usuário perguntou \"%s\". Orientação dada: \"%s\"."
                .formatted(
                        limitaTexto(mensagemUsuario, 220),
                        limitaTexto(respostaIa, 360)
                );

        String resumoAtual = memoriaExistente
                .map(MemoriaContexto::getResumo)
                .orElse("");

        String resumoAtualizado = limitaMemoria(
                StringUtils.hasText(resumoAtual) ? resumoAtual + "\n" + novaEntrada : novaEntrada
        );

        memoriaExistente.ifPresentOrElse(
                memoria -> memoria.atualizarResumo(resumoAtualizado),
                () -> memoriaContextoRepository.save(MemoriaContexto.builder()
                        .usuario(usuario)
                        .resumo(resumoAtualizado)
                        .build())
        );
    }

    private String limitaMemoria(String valor) {
        if (valor.length() <= LIMITE_MEMORIA) {
            return valor;
        }

        return valor.substring(valor.length() - LIMITE_MEMORIA).trim();
    }

    private String limitaTexto(String valor, int limite) {
        String texto = valor.trim().replaceAll("\\s+", " ");

        if (texto.length() <= limite) {
            return texto;
        }

        return texto.substring(0, limite - 3) + "...";
    }

    private String tituloDaRequest(ConversaRequest request) {
        if (request != null && StringUtils.hasText(request.getTitulo())) {
            return limitaTitulo(request.getTitulo().trim());
        }

        return TITULO_PADRAO;
    }

    private String tituloDaMensagem(String mensagem) {
        String titulo = mensagem.trim().replaceAll("\\s+", " ");

        return limitaTitulo(titulo);
    }

    private String limitaTitulo(String titulo) {
        if (titulo.length() <= 100) {
            return titulo;
        }

        return titulo.substring(0, 97) + "...";
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

    private Conversa buscaConversaDoUsuario(UUID idConversa, Usuario usuario) {
        return conversaRepository.findByIdConversaAndUsuario(idConversa, usuario)
                .orElseThrow(() -> APIException.build(
                        HttpStatus.NOT_FOUND,
                        "Conversa não encontrada."
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
