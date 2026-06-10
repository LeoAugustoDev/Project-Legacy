# Plano de Implementacao do MVP LEGACY

Este documento transforma o contexto oficial de `LEGACY_SDD_CONTEXT.md` em um guia pratico para implementar o MVP ponta a ponta, respeitando a estrategia SDD, o escopo do MVP e a arquitetura recomendada.

## 1. Estado Atual do Projeto

O projeto esta praticamente no inicio.

- Backend Spring Boot em `src/main/java/br/com/legacy`.
- Existe apenas `LegacyApplication.java` como aplicacao principal.
- Existe `Usuario.java`, mas a entidade ainda esta vazia.
- Nao existem controllers, services, repositories, seguranca, JWT, perfil, chat, dashboard ou analise.
- Nao existe frontend Next.js ainda.
- Nao existe `docker-compose.yml`.
- O `pom.xml` esta usando Spring Boot `4.0.6`, mas o documento oficial exige Spring Boot 3.
- O `application.yaml` possui credenciais do banco hardcoded, o que deve ser ajustado para variaveis de ambiente.

## 2. Direcao Recomendada

Implementar o MVP em ordem, sem pular fases.

1. Fase 1: Cadastro, login e JWT.
2. Fase 2: Perfil do usuario.
3. Fase 3: Dashboard.
4. Fase 4: Chat IA.
5. Fase 5: Historico de conversas.
6. Fase 6: Memoria de contexto.
7. Fase 7: Upload de imagens.
8. Fase 8: Analise visual com IA.

A recomendacao e comecar pelo backend. Depois que o backend estiver estavel nas fases principais, criar o frontend Next.js consumindo a API.

## 3. Preparacao Tecnica Inicial

Antes da Fase 1, fazer uma preparacao minima do projeto.

- Ajustar o projeto para Spring Boot 3, conforme o documento oficial.
- Remover dependencias desnecessarias no MVP, como Anthropic e OpenFeign, se nao forem usadas.
- Adicionar dependencia de JWT.
- Criar `docker-compose.yml` com PostgreSQL.
- Ajustar `application.yaml` para usar variaveis de ambiente.
- Criar os pacotes base do monolito modular.

Estrutura base recomendada:

```txt
br.com.legacy
├── LegacyApplication.java
├── usuario
├── autenticacao
├── perfil
├── dashboard
├── chat
├── analise
├── configuracao
└── common
```

Organizacao recomendada por modulo:

```txt
modulo
├── application
│   ├── api
│   ├── service
│   └── dto
├── domain
├── infrastructure
│   └── repository
└── exception
```

## 4. Fase 1: Cadastro, Login e JWT

Objetivo: permitir que usuarios criem conta, facam login e recebam token JWT para acessar areas protegidas.

### Entregas

- Entidade `Usuario`.
- Cadastro de usuario.
- Criptografia de senha com BCrypt.
- Login.
- Geracao de JWT.
- Validacao de JWT.
- Rotas publicas e protegidas.
- Configuracao do Spring Security.

### Arquivos Principais

```txt
usuario/domain/Usuario.java
usuario/domain/Role.java
usuario/infrastructure/repository/UsuarioRepository.java
autenticacao/application/api/AutenticacaoController.java
autenticacao/application/dto/CadastroRequest.java
autenticacao/application/dto/LoginRequest.java
autenticacao/application/dto/LoginResponse.java
autenticacao/application/service/AutenticacaoService.java
configuracao/security/SecurityConfig.java
configuracao/security/JwtService.java
configuracao/security/JwtAuthenticationFilter.java
```

### Endpoints

```txt
POST /api/auth/cadastro
POST /api/auth/login
GET  /api/usuarios/me
```

### Regras

- Senha nunca deve ser salva em texto puro.
- Usar BCrypt para hash de senha.
- E-mail deve ser unico.
- JWT deve ser enviado no header `Authorization: Bearer <token>`.
- Rotas publicas devem permitir apenas cadastro, login e recursos publicos.
- Rotas privadas devem exigir JWT valido.
- Nenhum DTO de resposta deve retornar senha.

### DTOs Sugeridos

`CadastroRequest`:

```json
{
  "nome": "Joao Silva",
  "email": "joao@email.com",
  "senha": "senha123"
}
```

`LoginRequest`:

```json
{
  "email": "joao@email.com",
  "senha": "senha123"
}
```

`LoginResponse`:

```json
{
  "token": "jwt-token",
  "tipo": "Bearer"
}
```

## 5. Fase 2: Perfil

Objetivo: coletar informacoes pessoais e preferencias iniciais do usuario para personalizar a experiencia.

### Entidade

```txt
Perfil
- id
- usuario
- profissao
- objetivoImagem
- estiloDesejado
- contextoUso
- coresPreferidas
- restricoes
- criadoEm
- atualizadoEm
```

### Endpoints

```txt
GET /api/perfil/me
PUT /api/perfil/me
```

### Regras

- Cada usuario pode ter um perfil.
- O perfil deve estar vinculado ao usuario autenticado.
- O perfil pode ser criado ou atualizado pelo mesmo endpoint.
- Usuario nao pode acessar perfil de outro usuario.
- Nao retornar entidade diretamente, apenas DTO.

### DTO Sugerido

```json
{
  "profissao": "Advogado",
  "objetivoImagem": "Transmitir autoridade e elegancia",
  "estiloDesejado": "Classico moderno",
  "contextoUso": "Audiencias, reunioes e eventos sociais",
  "coresPreferidas": "Azul marinho, cinza, branco",
  "restricoes": "Evitar roupas muito chamativas"
}
```

## 6. Fase 3: Dashboard

Objetivo: entregar uma tela inicial com resumo da experiencia do usuario.

### Endpoint

```txt
GET /api/dashboard
```

### Resposta Sugerida

```json
{
  "nome": "Joao Silva",
  "resumoPerfil": "Advogado buscando uma imagem classica moderna.",
  "statusJornada": "Perfil preenchido",
  "ultimaConversa": "Como me vestir melhor para audiencias?",
  "proximasRecomendacoes": [
    "Completar analise visual",
    "Revisar combinacoes para ambiente profissional"
  ]
}
```

### Regras

- Evitar criar entidade nova para dashboard no MVP.
- Montar a resposta a partir de `Usuario`, `Perfil`, `Conversa` e `Mensagem`.
- Usuario deve ver apenas dados proprios.

## 7. Fase 4: Chat IA

Objetivo: permitir conversa com uma IA consultora de imagem masculina.

### Endpoint Inicial

```txt
POST /api/chat/mensagens
```

### Request

```json
{
  "mensagem": "Como posso me vestir melhor para audiencias?"
}
```

### Entregas

- Endpoint de envio de mensagem.
- Integracao com OpenAI via Spring AI.
- Prompt base do consultor LEGACY.
- Resposta textual da IA.

### Regras

- Somente usuarios autenticados podem conversar com a IA.
- Usar `OPENAI_API_KEY` via variavel de ambiente.
- A IA deve responder como consultora premium de imagem masculina.
- O tom deve ser elegante, profissional, direto e sofisticado.
- Evitar respostas genericas quando houver contexto disponivel.

### Prompt Base Sugerido

```txt
Voce e o consultor LEGACY, uma inteligencia artificial especializada em imagem masculina, elegancia, presenca executiva e comunicacao nao verbal.

Responda com tom elegante, profissional, direto e sofisticado.

Seu objetivo e ajudar homens a se apresentarem melhor no ambiente profissional, social e pessoal.

Evite respostas genericas. Sempre que houver contexto sobre profissao, objetivo de imagem, estilo desejado ou preferencias do usuario, use essas informacoes para personalizar a resposta.

Nunca seja ofensivo, constrangedor ou depreciativo.
```

## 8. Fase 5: Historico de Conversas

Objetivo: salvar conversas entre usuario e IA.

### Entidades

```txt
Conversa
- id
- usuario
- titulo
- criadoEm
- atualizadoEm
```

```txt
Mensagem
- id
- conversa
- autor
- conteudo
- criadoEm
```

### Endpoints

```txt
GET  /api/conversas
GET  /api/conversas/{id}
POST /api/conversas
POST /api/conversas/{id}/mensagens
```

### Regras

- Toda conversa deve pertencer a um usuario.
- Usuario so pode acessar suas proprias conversas.
- Mensagens devem indicar autor `USUARIO` ou `IA`.
- O envio de mensagem deve salvar a mensagem do usuario e a resposta da IA.
- O historico sera usado futuramente para memoria de contexto.

## 9. Fase 6: Memoria de Contexto

Objetivo: permitir que a IA utilize informacoes anteriores do usuario para responder melhor.

### Entidade

```txt
MemoriaContexto
- id
- usuario
- resumo
- atualizadoEm
```

### Uso no Prompt

```txt
Perfil do usuario:
[dados do perfil]

Memoria conhecida:
[resumo persistido]

Ultimas mensagens relevantes:
[historico resumido]

Mensagem atual:
[mensagem do usuario]
```

### Regras

- Nao implementar RAG nesta fase.
- Nao usar embeddings nesta fase.
- Usar apenas resumo persistido, perfil e ultimas mensagens.
- Atualizar memoria depois de interacoes relevantes ou quando o perfil mudar.
- A memoria nunca deve expor dados de outros usuarios.

## 10. Fase 7: Upload de Imagens

Objetivo: permitir que o usuario envie fotos para analise visual.

### Entidade

```txt
ImagemAnalise
- id
- usuario
- nomeArquivo
- urlArquivo
- contentType
- tamanho
- criadoEm
```

### Endpoints

```txt
POST /api/imagens
GET  /api/imagens
GET  /api/imagens/{id}
```

### Regras

- Somente usuario autenticado pode enviar imagens.
- Aceitar apenas formatos como `jpg`, `jpeg`, `png` e possivelmente `webp`.
- Definir tamanho maximo, por exemplo `5MB`.
- Armazenar localmente no MVP.
- Associar imagem ao usuario autenticado.
- Nao permitir que um usuario acesse imagem de outro usuario.

## 11. Fase 8: Analise Visual

Objetivo: usar IA com visao para analisar foto do usuario e gerar recomendacoes de imagem.

### Entidade

```txt
AnaliseVisual
- id
- usuario
- imagemAnalise
- resultado
- criadoEm
```

### Endpoints

```txt
POST /api/analises-visuais
GET  /api/analises-visuais
GET  /api/analises-visuais/{id}
```

### Resposta Ideal da IA

```json
{
  "resumo": "A composicao geral transmite formalidade e sobriedade.",
  "pontosFortes": [
    "Boa escolha de cores neutras",
    "Visual adequado para contexto profissional"
  ],
  "ajustesPrioritarios": [
    "Melhorar caimento da camisa",
    "Ajustar contraste entre paleto e camisa"
  ],
  "recomendacoes": [
    "Priorizar tons de azul marinho e cinza medio",
    "Usar sapatos de couro em tom cafe escuro"
  ],
  "cores": [
    "Azul marinho",
    "Cinza",
    "Branco"
  ],
  "postura": "A postura pode ser mais ereta para transmitir mais autoridade.",
  "proximosPassos": [
    "Testar uma combinacao classica com blazer azul marinho",
    "Fazer nova analise com foto em ambiente profissional"
  ]
}
```

### Regras

- Integrar com OpenAI Vision.
- Analisar imagem com foco em estilo, postura, harmonia visual e recomendacoes de melhoria.
- A analise deve ser respeitosa e nao ofensiva.
- Salvar historico de analises.
- Usuario so pode acessar as proprias analises.

## 12. Frontend

Como ainda nao existe frontend, criar depois que o backend base estiver estavel.

Estrutura sugerida:

```txt
frontend/
```

Stack:

```txt
Next.js
TypeScript
Tailwind CSS
Shadcn UI
```

### Telas Minimas do MVP

- Cadastro.
- Login.
- Dashboard.
- Perfil.
- Chat IA.
- Historico de conversas.
- Upload de imagem.
- Resultado de analise visual.

### Fluxo

- Login salva JWT.
- Todas as chamadas privadas enviam `Authorization: Bearer <token>`.
- Usuario sem token e redirecionado para login.
- Dashboard chama `/api/dashboard`.
- Chat chama endpoints de conversas/mensagens.
- Upload chama `/api/imagens`.
- Analise chama `/api/analises-visuais`.

## 13. Testes Manuais no Postman

Criar uma colecao Postman com a seguinte sequencia:

1. Cadastrar usuario.
2. Tentar cadastrar e-mail duplicado.
3. Fazer login.
4. Acessar rota protegida sem token.
5. Acessar rota protegida com token.
6. Criar ou editar perfil.
7. Consultar dashboard.
8. Enviar mensagem para IA.
9. Consultar historico.
10. Testar memoria de contexto.
11. Fazer upload de imagem valida.
12. Testar upload invalido.
13. Gerar analise visual.
14. Verificar se outro usuario nao acessa dados alheios.

## 14. Estrategia de Commits

Usar commits pequenos, claros e profissionais.

Sugestoes:

```txt
feat: adiciona cadastro de usuario
feat: implementa login com jwt
feat: cria perfil de usuario
feat: adiciona dashboard inicial
feat: integra chat com ia
feat: salva historico de conversas
feat: adiciona memoria de contexto
feat: implementa upload de imagens
feat: adiciona analise visual
fix: corrige validacao de email duplicado
refactor: organiza pacote de autenticacao
chore: configura docker compose postgres
```

Branches sugeridas:

```txt
feature/fase-1-auth-jwt
feature/fase-2-perfil
feature/fase-3-dashboard
feature/fase-4-chat-ia
feature/fase-5-historico-conversas
feature/fase-6-memoria-contexto
feature/fase-7-upload-imagens
feature/fase-8-analise-visual
```

## 15. Criterio de Pronto do MVP

O MVP estara pronto quando:

- Usuario conseguir se cadastrar.
- Usuario conseguir fazer login.
- JWT proteger rotas privadas.
- Usuario conseguir preencher perfil.
- Dashboard exibir informacoes principais.
- Usuario conseguir conversar com a IA.
- Conversas ficarem salvas.
- IA usar contexto basico do usuario.
- Usuario conseguir enviar imagem.
- IA conseguir gerar analise visual.
- Sistema estiver demonstravel com frontend e backend funcionando.

## 16. Proxima Decisao Recomendada

A recomendacao e implementar primeiro a Fase 1 no backend, validar com Postman e depois seguir fase por fase.

Decisao sugerida:

```txt
Implementar backend fase por fase primeiro.
Depois criar o frontend Next.js consumindo a API pronta.
```

Isso reduz complexidade, protege o escopo do MVP e cria uma base backend solida antes da interface.
