# LEGACY - SDD de IA com Groq + Spring AI e Custo Minimo

Este documento define a estrategia oficial para implementar IA no LEGACY usando Groq, integrado ao backend Spring Boot com Spring AI, mantendo o menor custo possivel durante o MVP.

## 1. Objetivo

Implementar as fases de IA do MVP com uma API externa simples, controlada e barata, sem aumentar a complexidade com LLM local, n8n, RAG ou infraestrutura propria.

Objetivos especificos:

- Implementar chat textual na Fase 4.
- Usar Groq via Spring AI.
- Controlar gasto com limite de tokens.
- Evitar prompts grandes.
- Evitar historico completo antes da Fase 5/6.
- Manter a chave de API fora do codigo.
- Preparar o backend para trocar de provedor futuramente, se necessario.

## 2. Decisao Tecnica

Para as Fases 4, 5 e 6 do MVP, usar:

```txt
Spring Boot -> Spring AI -> API Groq -> resposta textual
```

Decisao atual:

```txt
Usar Groq como provedor de IA principal do MVP.
```

Nao usar agora:

```txt
Ollama
n8n
LLM local
OpenAI API
Outro provedor de IA externo
RAG
Embeddings
pgvector
```

## 3. Motivo da Escolha

Groq + Spring AI e uma boa escolha para o MVP porque:

- Menor complexidade que LLM local.
- Nao exige maquina forte.
- Nao exige n8n.
- A integracao fica direta no backend.
- Spring AI facilita trocar provedor futuramente.
- A qualidade tende a ser melhor e mais estavel que modelos locais leves.

Trade-offs:

- Existe custo por uso de API.
- Precisa criar conta na Groq.
- Precisa configurar API key.
- O custo precisa ser controlado com limites tecnicos e budget.

## 4. O Que Precisa Comprar ou Configurar

Antes de implementar a Fase 4, e necessario:

1. Criar conta na plataforma da Groq.
2. Ativar acesso a API da Groq.
3. Adicionar credito baixo inicialmente, se a plataforma exigir credito pre-pago.
4. Criar uma API key.
5. Configurar a API key como variavel de ambiente no IntelliJ.

Variavel recomendada:

```txt
GROQ_API_KEY=sua-chave-aqui
```

Nao me envie a chave. Configure apenas localmente.

## 5. Orcamento Inicial Recomendado

Para desenvolvimento e demonstracao inicial:

```txt
US$ 5 a US$ 10
```

Faixa aproximada:

```txt
R$ 25 a R$ 50
```

Comecar baixo e acompanhar consumo.

Nao colocar valores altos no inicio.

## 6. Como Evitar Gasto Alto

Regras obrigatorias:

- Usar modelo economico, se disponivel.
- Limitar resposta com `max-tokens`.
- Usar prompt base curto.
- Enviar apenas perfil essencial.
- Nao enviar historico completo.
- Nao chamar IA em testes automatizados.
- Nao implementar Vision antes da Fase 8.
- Validar tamanho da mensagem do usuario.
- Nao chamar IA se a mensagem estiver vazia.

Limite recomendado para a mensagem do usuario:

```txt
1000 caracteres
```

Limite inicial recomendado para resposta:

```txt
500 tokens
```

## 7. Dependencia Spring AI

Como a API da Groq segue padrao compativel com OpenAI, a integracao inicial pode usar o starter OpenAI do Spring AI apontando para a base URL da Groq.

Adicionar no `pom.xml`:

```xml
<properties>
    <java.version>21</java.version>
    <spring-ai.version>2.0.0-RC2</spring-ai.version>
</properties>
```

Dependencia:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

Dependency management:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>${spring-ai.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Observacao:

```txt
Se a versao do Spring AI usada no projeto tiver suporte nativo a Groq, preferir o starter nativo.
Caso contrario, usar o client OpenAI-compatible com base-url da Groq.
```

## 8. Configuracao Recomendada

Adicionar no `application.yaml`:

```yaml
spring:
  ai:
    openai:
      api-key: ${GROQ_API_KEY}
      base-url: ${GROQ_BASE_URL:https://api.groq.com/openai/v1}
      chat:
        options:
          model: ${GROQ_MODEL:llama-3.1-8b-instant}
          temperature: 0.7
          max-tokens: 500
```

Variaveis opcionais:

```txt
GROQ_API_KEY=sua-chave-aqui
GROQ_BASE_URL=https://api.groq.com/openai/v1
GROQ_MODEL=llama-3.1-8b-instant
```

Se o modelo economico disponivel na sua conta tiver outro nome, ajustar apenas `GROQ_MODEL`.

## 9. Arquitetura da Fase 4

Fluxo:

```txt
Usuario autenticado
-> POST /chat/mensagens
-> ChatController
-> ChatService
-> Spring AI ChatClient
-> Groq API
-> ChatResponse
```

No MVP, o backend chama diretamente a Groq via Spring AI.

Nao ha n8n nessa versao.

Nao ha Ollama nessa versao.

## 10. Modulo Chat

Criar:

```txt
src/main/java/br/com/legacy/chat/api/ChatController.java
src/main/java/br/com/legacy/chat/api/ChatRequest.java
src/main/java/br/com/legacy/chat/api/ChatResponse.java
src/main/java/br/com/legacy/chat/application/ChatService.java
```

Endpoint:

```txt
POST /chat/mensagens
```

Request:

```json
{
  "mensagem": "Como posso me vestir melhor para uma reuniao com clientes importantes?"
}
```

Response:

```json
{
  "resposta": "..."
}
```

## 11. Regras da Fase 4

- Endpoint protegido por JWT.
- Usuario precisa estar autenticado.
- Backend deve buscar perfil do usuario, se existir.
- Backend deve montar prompt curto.
- Backend deve chamar Groq via Spring AI.
- Backend deve retornar resposta textual.
- Nao salvar historico ainda.
- Nao criar entidades `Conversa` e `Mensagem` ainda.
- Nao usar memoria persistida ainda.
- Nao usar RAG.
- Nao usar Vision.

## 12. Prompt Base Economico

Prompt base recomendado:

```txt
Voce e o consultor LEGACY, especialista em imagem masculina, elegancia e presenca executiva.

Responda com tom profissional, direto e sofisticado.

Use o perfil do usuario quando disponivel.

Evite respostas genericas.

Nao seja ofensivo, depreciativo ou constrangedor.

Responda em no maximo 8 linhas, com recomendacoes praticas.
```

Contexto permitido na Fase 4:

```txt
Prompt base
Perfil resumido
Mensagem atual
```

Nao enviar:

```txt
Historico completo
Memoria longa
Dados sensiveis
Senha
JWT
Documentos
Imagens
```

## 13. Perfil no Prompt

Se o usuario tiver perfil, enviar apenas campos uteis:

```txt
Profissao: Advogado
Objetivo de imagem: Transmitir autoridade e elegancia
Estilo desejado: Classico moderno
Contexto de uso: Audiencias e reunioes profissionais
Cores preferidas: Azul marinho, cinza, branco
Restricoes: Evitar roupas muito chamativas
```

Se o usuario nao tiver perfil, o chat deve funcionar mesmo assim, mas com resposta mais generica.

## 14. Fase 5 - Historico de Conversas

Na Fase 5, implementar persistencia no PostgreSQL.

Entidades previstas:

```txt
Conversa
Mensagem
AutorMensagem
```

Fluxo:

```txt
1. Usuario envia mensagem.
2. Backend salva mensagem do usuario.
3. Backend chama Groq.
4. Backend recebe resposta.
5. Backend salva mensagem da IA.
6. Backend retorna resposta.
```

Endpoints futuros:

```txt
GET  /conversas
GET  /conversas/{id}
POST /conversas
POST /conversas/{id}/mensagens
```

Regra de custo:

```txt
Salvar historico no banco nao significa enviar todo historico para a IA.
```

## 15. Fase 6 - Memoria de Contexto

Na Fase 6, implementar memoria curta e economica.

Entidade prevista:

```txt
MemoriaContexto
```

Campos:

```txt
id
usuario
resumo
atualizadoEm
```

Contexto enviado para IA:

```txt
Perfil resumido
Memoria resumida
Ultimas 3 a 5 mensagens relevantes
Mensagem atual
```

Nao enviar conversa inteira.

Nao usar embeddings no MVP.

Nao usar RAG no MVP.

## 16. Tratamento de Erros

### API Groq indisponivel

Retornar:

```txt
503 Service Unavailable
```

Mensagem:

```txt
Servico de IA temporariamente indisponivel.
```

### Resposta vazia da IA

Retornar:

```txt
502 Bad Gateway
```

Mensagem:

```txt
Resposta invalida do servico de IA.
```

### API key ausente

A aplicacao pode falhar ao subir ou a chamada pode falhar.

Mitigacao:

```txt
Configurar GROQ_API_KEY no IntelliJ antes de iniciar a aplicacao.
```

## 17. Validacoes no Backend

`ChatRequest` deve ter:

```java
@NotBlank
@Size(max = 1000)
String mensagem;
```

Regras:

- Mensagem vazia retorna 400.
- Mensagem muito longa retorna 400.
- Endpoint sem token retorna 401/403.
- Token valido permite chamada.
- Falha externa da IA deve ser tratada.

## 18. Seguranca

- Nunca colocar `GROQ_API_KEY` no codigo.
- Nunca colocar `GROQ_API_KEY` no frontend.
- Nunca commitar `.env` com segredo.
- Nao logar API key.
- Nao logar JWT.
- Evitar logar prompt completo se tiver dados pessoais.
- Usar variavel de ambiente no IntelliJ.

## 19. Teste Manual da Fase 4

1. Configurar `GROQ_API_KEY` no IntelliJ.
2. Subir a aplicacao.
3. Fazer login.
4. Copiar token.
5. Chamar endpoint:

```txt
POST http://localhost:8080/chat/mensagens
Authorization: Bearer SEU_TOKEN
```

Body:

```json
{
  "mensagem": "Como devo me vestir para uma reuniao importante com clientes?"
}
```

Resposta esperada:

```json
{
  "resposta": "..."
}
```

## 20. Criterio de Pronto da Fase 4

A Fase 4 estara pronta quando:

- Spring AI estiver configurado.
- Groq responder via API.
- `POST /chat/mensagens` exigir JWT.
- Usuario autenticado conseguir enviar mensagem.
- Backend usar perfil quando disponivel.
- Resposta vier curta, elegante e objetiva.
- Chave estiver apenas em variavel de ambiente.
- `max-tokens` estiver configurado.
- Teste manual no Postman estiver validado.

## 21. Decisao Final Atualizada

Decisao oficial:

```txt
Para as Fases 4, 5 e 6, usar Groq via Spring AI, com prompt curto, resposta limitada e controle de custo por configuracao.
```

Configuracao inicial recomendada:

```txt
Provider: Groq
Integracao: Spring AI
Base URL: https://api.groq.com/openai/v1
Modelo: llama-3.1-8b-instant, ou modelo economico disponivel na conta
max-tokens: 500
temperature: 0.7
Historico completo: nao enviar
RAG/embeddings: fora do MVP
Vision: somente na Fase 8
```

## 22. Sequencia Antes de Codar

Antes de implementar o backend da Fase 4:

1. Criar conta na Groq.
2. Ativar API da Groq.
3. Criar API key.
4. Configurar `GROQ_API_KEY` no IntelliJ.
5. Confirmar nome do modelo disponivel na sua conta.
6. Definir `GROQ_MODEL` se necessario.
7. So depois implementar `POST /chat/mensagens`.

Nao iniciar a implementacao real da Fase 4 sem API key configurada, porque a aplicacao pode falhar ao subir ou o endpoint nao podera ser testado.
