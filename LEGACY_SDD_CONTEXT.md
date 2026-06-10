# LEGACY — Contexto Oficial para Desenvolvimento Orientado a SDD

## 1. Identidade do Projeto

**Nome oficial:** LEGACY  
**Tipo de produto:** SaaS premium  
**Categoria:** Consultoria de imagem masculina com Inteligência Artificial  
**Objetivo principal:** ajudar homens a se tornarem mais elegantes, confiantes, respeitados, sofisticados e estratégicos na forma como se apresentam ao mundo.

O LEGACY não deve ser tratado como um projeto simples de estudo. Ele é o principal projeto de portfólio, laboratório profissional de backend e potencial startup SaaS futura.

O projeto surgiu a partir de um pedido direto do pai do fundador. Após a conclusão do MVP, o produto deverá ser apresentado a ele para validação de interesse, continuidade, custos futuros e possível evolução como negócio real.

---

## 2. Visão do Produto

O LEGACY é uma plataforma web/SaaS de consultoria de imagem, elegância e estilo masculino baseada em IA.

O sistema combina:

- Consultoria de imagem masculina
- Inteligência Artificial generativa
- Personal Stylist digital
- Planejamento de guarda-roupa
- Consultoria de elegância
- Comunicação não verbal
- Desenvolvimento pessoal
- Presença executiva
- Etiqueta social
- Compras inteligentes futuramente

O produto não deve ser tratado como um simples app de moda. A proposta é entregar percepção de valor premium, sofisticação e transformação pessoal.

---

## 3. Público-Alvo

O LEGACY é voltado para homens que desejam melhorar sua imagem pessoal, profissional e social.

Públicos principais:

- Advogados
- Juízes
- Promotores
- Procuradores
- Empresários
- Executivos
- Médicos
- Engenheiros
- Corretores
- Consultores
- Líderes religiosos
- Universitários
- Jovens profissionais
- Homens maduros

---

## 4. Princípio Central de Desenvolvimento

Não construir tudo de uma vez.

A prioridade obrigatória deve ser:

1. MVP
2. Escalabilidade futura
3. Boas práticas
4. Aprendizado backend
5. Arquitetura limpa
6. Experiência profissional de demonstração

Toda decisão técnica deve proteger o MVP contra excesso de escopo.

Funcionalidades como comunidade, marketplace, gamificação, cursos, armário digital, personal shopper e RAG avançado só devem ser consideradas após o MVP estar concluído.

---

## 5. Stack Técnica Oficial

### Frontend

- Next.js
- TypeScript
- Tailwind CSS
- Shadcn UI

### Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Spring AI

### Banco de Dados

- PostgreSQL

### IA

- OpenAI
- OpenAI Embeddings
- OpenAI Vision

### RAG Futuro

- pgvector

### Cloud Futuro

- AWS

### Ferramentas de Desenvolvimento

- IntelliJ IDEA
- Git
- GitHub
- Docker
- Docker Compose
- Postman
- DBeaver ou pgAdmin

---

## 6. Arquitetura Inicial

A arquitetura inicial será um **Monólito Modular**.

Microsserviços não devem ser usados no início.

Justificativa:

- Menor complexidade inicial
- Mais rápido para desenvolver o MVP
- Mais fácil de testar
- Mais fácil para um desenvolvedor solo manter
- Ainda permite boa organização por módulos
- Pode evoluir para microsserviços futuramente, se houver necessidade real

### Pacote Raiz

```txt
br.com.legacy
```

### Módulos Iniciais

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

---

## 7. Organização Recomendada por Módulo

Cada módulo deve seguir uma organização próxima a:

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

Exemplo para usuário:

```txt
usuario
├── application
│   ├── api
│   │   └── UsuarioController.java
│   ├── dto
│   │   ├── UsuarioRequest.java
│   │   └── UsuarioResponse.java
│   └── service
│       ├── UsuarioService.java
│       └── UsuarioApplicationService.java
├── domain
│   └── Usuario.java
├── infrastructure
│   └── repository
│       └── UsuarioRepository.java
└── exception
    └── UsuarioNaoEncontradoException.java
```

---

## 8. Roadmap Oficial do MVP

O MVP do LEGACY possui apenas:

1. Cadastro
2. Login
3. JWT
4. Perfil de usuário
5. Dashboard
6. Chat IA
7. Histórico de conversas
8. Memória de contexto
9. Análise de fotos

Nada além disso deve ser implementado antes da conclusão do MVP.

---

## 9. Fases Obrigatórias

### Fase 1 — Cadastro, Login e JWT

Objetivo: permitir que usuários criem conta, façam login e recebam token JWT para acessar áreas protegidas.

Entregas:

- Entidade Usuario
- Cadastro de usuário
- Criptografia de senha
- Login
- Geração de JWT
- Validação de JWT
- Rotas públicas e protegidas
- Configuração do Spring Security

### Fase 2 — Perfil

Objetivo: coletar informações pessoais e preferências iniciais do usuário para personalizar a experiência.

Entregas:

- Perfil vinculado ao usuário
- Dados básicos de estilo
- Objetivo de imagem
- Profissão ou contexto social
- Preferências iniciais

### Fase 3 — Dashboard

Objetivo: entregar uma tela inicial com resumo da experiência do usuário.

Entregas:

- Resumo do perfil
- Status da jornada
- Última conversa com IA
- Próximas recomendações

### Fase 4 — Chat IA

Objetivo: permitir conversa com uma IA consultora de imagem masculina.

Entregas:

- Endpoint de envio de mensagem
- Integração com OpenAI via Spring AI
- Prompt base do consultor LEGACY
- Resposta textual da IA

### Fase 5 — Histórico de Conversas

Objetivo: salvar conversas entre usuário e IA.

Entregas:

- Entidade Conversa
- Entidade Mensagem
- Histórico por usuário
- Listagem de conversas
- Consulta de conversa específica

### Fase 6 — Memória de Contexto

Objetivo: permitir que a IA utilize informações anteriores do usuário para responder melhor.

Entregas:

- Resumo de contexto do usuário
- Preferências persistidas
- Histórico relevante
- Injeção de contexto no prompt

### Fase 7 — Upload de Imagens

Objetivo: permitir que o usuário envie fotos para análise visual.

Entregas:

- Upload de imagem
- Validação de tipo e tamanho
- Armazenamento inicial local ou em bucket futuro
- Associação da imagem ao usuário

### Fase 8 — Análise Visual

Objetivo: usar IA com visão para analisar foto do usuário e gerar recomendações de imagem.

Entregas:

- Integração com OpenAI Vision
- Prompt de análise visual
- Resposta estruturada
- Histórico de análises

---

## 10. Funcionalidades Proibidas Antes do MVP

Não implementar antes da conclusão da Fase 8:

- Rede social
- Gamificação
- Marketplace
- Comunidade
- Cursos
- Armário digital
- Looks inteligentes
- Personal shopper
- RAG avançado
- Planos pagos completos
- Sistema complexo de assinatura
- Área administrativa avançada

Essas funcionalidades pertencem ao pós-MVP.

---

## 11. Regras de Negócio do MVP

### Usuário

- Um usuário deve possuir nome, e-mail e senha.
- O e-mail deve ser único.
- A senha nunca deve ser salva em texto puro.
- Usuários autenticados acessam recursos protegidos.
- Usuários não autenticados só podem acessar cadastro, login e rotas públicas.

### Autenticação

- O login deve validar e-mail e senha.
- Após login válido, o sistema deve gerar um JWT.
- O JWT deve ser enviado no header Authorization usando Bearer Token.
- Rotas protegidas devem exigir JWT válido.

### Perfil

- Cada usuário pode ter um perfil.
- O perfil deve conter informações úteis para personalização da IA.
- O perfil deve ser editável pelo usuário autenticado.
- O perfil não deve ser acessível por outro usuário.

### Chat IA

- Somente usuários autenticados podem conversar com a IA.
- Cada mensagem enviada deve estar vinculada ao usuário.
- A IA deve responder como consultora premium de imagem masculina.
- O tom da IA deve ser elegante, profissional, direto e sofisticado.
- O sistema deve evitar respostas genéricas sempre que houver contexto disponível.

### Histórico

- Toda conversa deve pertencer a um usuário.
- Usuários só podem acessar suas próprias conversas.
- Mensagens devem indicar autor: USUARIO ou IA.
- O histórico deve ser usado futuramente para memória de contexto.

### Memória

- A memória deve guardar informações relevantes para personalização.
- A memória não deve expor dados de outros usuários.
- A memória deve ajudar a IA a lembrar preferências, profissão, objetivos e estilo desejado.

### Upload e Análise de Fotos

- O usuário autenticado pode enviar imagens.
- O sistema deve validar formato e tamanho do arquivo.
- A imagem deve ser associada ao usuário.
- A IA deve analisar a imagem com foco em estilo, postura, harmonia visual e recomendações de melhoria.
- A análise deve ser respeitosa e não ofensiva.

---

## 12. Regras de Segurança

- Nunca salvar senha em texto puro.
- Usar BCrypt para hash de senha.
- Proteger endpoints sensíveis com Spring Security.
- Validar dados de entrada com Bean Validation.
- Não expor stack trace para o usuário final.
- Não retornar senha em nenhum DTO.
- Não permitir que um usuário acesse dados de outro usuário.
- Separar DTOs de entrada e saída.
- Usar variáveis de ambiente para segredos.
- Nunca versionar chaves de API.

---

## 13. Entidades Iniciais Previstas

### Usuario

Responsável por autenticação e identidade.

Campos sugeridos:

- id
- nome
- email
- senha
- role
- ativo
- criadoEm
- atualizadoEm

### Perfil

Responsável por personalização.

Campos sugeridos:

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

### Conversa

Responsável por agrupar mensagens.

Campos sugeridos:

- id
- usuario
- titulo
- criadoEm
- atualizadoEm

### Mensagem

Responsável por armazenar mensagens do chat.

Campos sugeridos:

- id
- conversa
- autor
- conteudo
- criadoEm

### MemoriaContexto

Responsável por guardar resumo contextual do usuário.

Campos sugeridos:

- id
- usuario
- resumo
- atualizadoEm

### ImagemAnalise

Responsável por armazenar referência da imagem enviada.

Campos sugeridos:

- id
- usuario
- nomeArquivo
- urlArquivo
- contentType
- tamanho
- criadoEm

### AnaliseVisual

Responsável por armazenar resultado da análise de imagem.

Campos sugeridos:

- id
- usuario
- imagemAnalise
- resultado
- criadoEm

---

## 14. Convenções de Código

### Nomes de Pacotes

Usar nomes em português ou inglês de forma consistente. Para este projeto, manter os módulos principais em português conforme arquitetura inicial, mas o nome do produto é LEGACY.

Exemplo:

```txt
br.com.legacy.usuario
br.com.legacy.autenticacao
br.com.legacy.perfil
br.com.legacy.chat
br.com.legacy.analise
```

### Controllers

Controllers devem:

- Receber requisições HTTP
- Validar entrada com DTOs
- Chamar services
- Retornar responses
- Não conter regra de negócio pesada

### Services

Services devem:

- Conter regras de negócio
- Orquestrar operações
- Validar permissões
- Chamar repositories
- Controlar transações quando necessário

### Repositories

Repositories devem:

- Acessar banco de dados
- Usar Spring Data JPA
- Não conter regra de negócio

### DTOs

DTOs devem:

- Separar entrada e saída
- Não expor senha
- Evitar retornar entidades diretamente

---

## 15. Estratégia SDD — Spec Driven Development

Antes de codar qualquer funcionalidade, seguir a ordem:

1. Escrever a especificação da funcionalidade
2. Definir regras de negócio
3. Definir endpoints
4. Definir DTOs
5. Definir entidades afetadas
6. Definir fluxo da aplicação
7. Implementar código
8. Testar no Postman
9. Revisar arquitetura
10. Fazer commit no Git

Modelo de prompt para desenvolvimento:

```txt
Jarvis, estamos na Fase [número] do LEGACY.

Funcionalidade: [nome da funcionalidade]

Antes de codar, crie a especificação SDD com:
1. Objetivo
2. Regras de negócio
3. Endpoints
4. DTOs
5. Entidades
6. Fluxo da aplicação
7. Código completo
8. Testes no Postman
9. Conceitos backend que preciso aprender

Use Java 21, Spring Boot 3, PostgreSQL, Spring Security quando necessário, monólito modular e boas práticas profissionais.
```

---

## 16. Estratégia de Commits

Usar commits pequenos, claros e profissionais.

Formato sugerido:

```txt
feat: adiciona cadastro de usuario
feat: implementa login com jwt
feat: cria perfil de usuario
fix: corrige validacao de email duplicado
refactor: organiza pacote de autenticacao
chore: configura docker compose postgres
```

### Branches Sugeridas

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

---

## 17. Critério de Pronto do MVP

O MVP estará pronto quando:

- Usuário conseguir se cadastrar
- Usuário conseguir fazer login
- JWT proteger rotas privadas
- Usuário conseguir preencher perfil
- Dashboard exibir informações principais
- Usuário conseguir conversar com a IA
- Conversas ficarem salvas
- IA usar contexto básico do usuário
- Usuário conseguir enviar imagem
- IA conseguir gerar análise visual
- Sistema estiver demonstrável com frontend e backend funcionando

---

## 18. Marco de Validação

Após finalizar o MVP, o produto deve ser apresentado ao pai do fundador.

Objetivos da apresentação:

- Demonstrar valor real do LEGACY
- Mostrar cadastro, login, perfil, dashboard, chat e análise visual
- Validar se o produto deve continuar sendo desenvolvido
- Conversar sobre custos futuros
- Avaliar infraestrutura, APIs de IA, hospedagem, domínio e manutenção
- Decidir próximos passos como negócio

---

## 19. Custos Futuros a Considerar Após MVP

Não priorizar agora, mas lembrar para validação futura:

- Domínio
- Hospedagem frontend
- Hospedagem backend
- Banco PostgreSQL gerenciado
- Armazenamento de imagens
- API da OpenAI
- AWS
- Monitoramento
- Logs
- Backup
- Segurança
- Eventual assinatura SaaS

---

## 20. Direção Técnica Geral

Sempre preferir:

- Simplicidade bem feita
- Código claro
- Arquitetura modular
- Segurança desde o início
- Testes manuais bem documentados no Postman
- Evolução gradual
- MVP funcional antes de sofisticação
- Backend sólido antes de features avançadas

O LEGACY deve evoluir como um produto real, mas sem pular etapas.
