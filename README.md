# Java RPA & API Automation - Sincronização de Dados

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=java&logoColor=white)
![Selenium](https://img.shields.io/badge/Selenium-Automation-green?style=for-the-badge&logo=selenium&logoColor=white)
![Google Sheets](https://img.shields.io/badge/Google%20Sheets-API-34A853?style=for-the-badge&logo=googlesheets&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

> **Projeto de Automação:** pipeline de extração de dados e sincronização com Google Sheets.
> **Stack:** Java 21 | Selenium WebDriver | Google Sheets API | SLF4J + Logback | Maven.
> **Execução:** local ou GitHub Actions, em modo headless.
> **Foco Arquitetural:** RPA, orientação a objetos, Clean Code, segurança de credenciais, testes automatizados e integração com serviços externos.

---

## 📑 Índice

* [📍 Sobre o Repositório](#-sobre-o-repositório)
* [⚙️ Como Funciona](#️-como-funciona)
* [📂 Estrutura de Pastas](#-estrutura-de-pastas)
* [🛠️ Destaques Técnicos](#️-destaques-técnicos)
* [🔐 Configuração e Credenciais](#-configuração-e-credenciais)
* [▶️ Execução Local](#️-execução-local)
* [☁️ Execução no GitHub Actions](#️-execução-no-github-actions)
* [🧪 Testes e Build](#-testes-e-build)
* [🚀 Arquitetura e Módulos](#-arquitetura-e-módulos)
* [🔜 Próximos Passos](#-próximos-passos)

---

## 📍 Sobre o Repositório

Solução de RPA desenvolvida em **Java 21** para automatizar a extração e consolidação de dados entre um sistema institucional web e o Google Sheets.

O robô lê cursos de uma planilha, acessa o sistema institucional usando **Selenium WebDriver**, realiza o processamento necessário, coleta as métricas de inscrições e ouvintes e grava os resultados de volta na planilha.

O projeto foi estruturado para permitir execução simples pelo usuário, mantendo internamente validação de configuração, testes automatizados, execução headless, logs e tratamento seguro de credenciais.

---

## ⚙️ Como Funciona

```text
Google Sheets
     │
     │  Leitura dos cursos
     ▼
Automation
     │
     │  Validação das configurações
     ▼
SistemaNavegador / Selenium
     │
     │  Login e processamento dos cursos
     ▼
Resultados
     │
     │  Atualização em lote
     ▼
Google Sheets
```

Fluxo operacional:

1. Carrega e valida as configurações.
2. Autentica na API do Google Sheets.
3. Lê as linhas válidas da planilha.
4. Filtra cursos conforme as regras de data e identificação.
5. Inicializa o Chrome em modo headless.
6. Realiza o login no sistema institucional.
7. Processa cada curso e coleta os indicadores.
8. Atualiza os resultados na planilha.
9. Fecha o navegador e encerra a execução.

---

## 📂 Estrutura de Pastas

```text
├── .github/
│   └── workflows/
│       └── automation.yml                 # Execução manual no GitHub Actions
├── src/
│   ├── main/
│   │   ├── java/com/rpa/api/automation/
│   │   │   ├── Automation.java            # Ponto de entrada e orquestração
│   │   │   ├── AutomationConfig.java      # Carregamento e validação da configuração
│   │   │   ├── CursoModel.java             # Modelo de dados do curso
│   │   │   ├── GoogleSheetsService.java    # Integração com Google Sheets
│   │   │   └── SistemaNavegador.java       # Automação web com Selenium
│   │   └── resources/
│   │       └── logback.xml                 # Configuração de logs
│   └── test/
│       └── java/com/rpa/api/automation/
│           └── AutomationConfigTest.java   # Testes de validação da configuração
├── .env.example                             # Modelo de configuração local
├── .gitignore                               # Exclusão de arquivos sensíveis e gerados
├── pom.xml                                  # Build, dependências e plugins Maven
└── README.md                                # Documentação do projeto
```

---

## 🛠️ Destaques Técnicos

Práticas implementadas para melhorar segurança, confiabilidade e manutenibilidade:

### ⚡ Automação Web com Selenium

Controle do Chrome por meio do Selenium WebDriver, com execução em modo **headless** para ambientes sem interface gráfica, como GitHub Actions.

### 🔎 Processamento Orientado a Dados

Leitura da planilha a partir das colunas configuradas, filtragem de registros inválidos ou expirados e extração do identificador `TurmaId` por expressão regular.

### ⏲️ Validação Temporal

Uso de `java.time` e `LocalDate` para validar as datas dos cursos e evitar o processamento de registros que já passaram ou vencem na data atual.

### 🛡️ Configuração Centralizada

A classe `AutomationConfig` concentra a leitura e validação das configurações necessárias, com prioridade para variáveis de ambiente sobre valores locais do arquivo `.env`.

A aplicação valida:

- URL do sistema;
- ID da planilha;
- nome da aba;
- caminho das credenciais Google;
- usuário e senha do sistema;
- tempos de espera positivos.

### 🔐 Gestão de Credenciais

Nenhuma credencial real deve ser armazenada no código-fonte.

Na execução local, as configurações podem ser fornecidas por `.env`. No GitHub Actions, as configurações técnicas são fornecidas por GitHub Secrets e o usuário informa suas credenciais institucionais no início de cada execução manual.

### 📊 Logs com SLF4J e Logback

Uso de logging estruturado com níveis de informação, alerta e erro, evitando `System.out` e sem registrar senhas ou o conteúdo das credenciais Google.

### ⚙️ Atualização em Lote

Os resultados são consolidados e enviados ao Google Sheets por meio da Batch API, reduzindo a quantidade de chamadas individuais à API.

### 🧪 Build e Testes Automatizados

O Maven está configurado para:

- Java 21;
- compilação com encoding UTF-8;
- testes JUnit 5;
- asserções com AssertJ;
- mocks com Mockito;
- cobertura com JaCoCo;
- validação de versão do Java e Maven;
- análise opcional de vulnerabilidades de dependências com OWASP Dependency-Check.

---

## 🔐 Configuração e Credenciais

### Variáveis utilizadas

| Variável | Obrigatória | Descrição |
|---|---:|---|
| `URL_SISTEMA` | Sim | URL base do sistema institucional |
| `SPREADSHEET_ID` | Sim | ID da planilha Google Sheets |
| `NOME_ABA` | Sim | Nome da aba utilizada |
| `GOOGLE_CREDENTIALS_PATH` | Sim | Caminho do JSON de credenciais Google |
| `USUARIO_LOGIN` | Sim | Usuário do sistema institucional |
| `SENHA_LOGIN` | Sim | Senha do sistema institucional |
| `TEMPO_ESPERA_SEGUNDOS` | Não | Tempo padrão de espera do navegador; padrão: `15` |
| `TEMPO_ESPERA_LOGIN_SEGUNDOS` | Não | Tempo máximo de espera do login; padrão: `60` |

### Execução segura no GitHub Actions

- `GOOGLE_CREDENTIALS_JSON`, `URL_SISTEMA`, `SPREADSHEET_ID`, `NOME_ABA` e os tempos devem ser armazenados em **GitHub Secrets**.
- O workflow manual continua permitindo que o operador informe `usuario_sistema` e `senha_sistema` por execução.
- As credenciais são fornecidas ao processo por variáveis de ambiente.
- O JSON do Google é criado apenas em arquivo temporário no diretório do runner.
- O arquivo temporário é removido ao final da execução, inclusive em caso de falha.
- O workflow possui permissões mínimas, timeout e bloqueio contra execuções simultâneas.
- O acesso de execução do workflow deve ser restrito a operadores confiáveis.
- Para produção, recomenda-se utilizar um GitHub Environment protegido.

> **Atenção:** nunca faça commit de `.env`, arquivos JSON de credenciais, senhas ou logs contendo informações sensíveis.

---

## ▶️ Execução Local

### Pré-requisitos

- Java 21;
- Maven 3.9 ou superior;
- Google Chrome;
- acesso à planilha Google Sheets;
- acesso ao sistema institucional;
- credencial Google com permissão na planilha.

### 1. Criar a configuração local

Copie `.env.example` para `.env` e preencha os valores:

```dotenv
URL_SISTEMA=https://seu-sistema.example.com/
SPREADSHEET_ID=seu_spreadsheet_id
NOME_ABA=NomeDaAba
GOOGLE_CREDENTIALS_PATH=credentials/google-credentials.json

USUARIO_LOGIN=seu_usuario
SENHA_LOGIN=sua_senha

TEMPO_ESPERA_SEGUNDOS=15
TEMPO_ESPERA_LOGIN_SEGUNDOS=60
```

Mantenha o arquivo JSON do Google fora do controle de versão.

### 2. Validar o projeto

```bash
mvn -B -ntp clean verify
```

Esse comando limpa os arquivos anteriores, compila o projeto, executa os testes e gera o relatório de cobertura.

### 3. Executar a automação

```bash
mvn -B -ntp exec:java \
  -Dexec.mainClass="com.rpa.api.automation.Automation"
```

---

## ☁️ Execução no GitHub Actions

O workflow está em:

```text
.github/workflows/automation.yml
```

Para executar:

1. Abra a aba **Actions** do repositório.
2. Selecione **RPA EGOV - Cloud Execution**.
3. Clique em **Run workflow**.
4. Informe o usuário institucional.
5. Informe a senha institucional.
6. Clique em **Run workflow** novamente.

O workflow executa:

```text
Checkout
→ Java 21
→ Arquivo temporário de credenciais Google
→ mvn clean verify
→ Automação headless
→ Limpeza das credenciais temporárias
```

A primeira execução deve ser feita com poucos registros ou em uma cópia controlada da planilha para validar o ambiente.

---

## 🧪 Testes e Build

Executar testes e validações:

```bash
mvn -B -ntp clean verify
```

Executar somente os testes:

```bash
mvn -B -ntp test
```

Executar análise de vulnerabilidades das dependências, quando necessário:

```bash
mvn -B -ntp verify -Ddependency-check.skip=false
```

A análise de vulnerabilidades pode ser mais demorada na primeira execução devido ao download da base de dados do OWASP.

---

## 🚀 Arquitetura e Módulos

### 1. `Automation`

Ponto de entrada da aplicação e responsável pela orquestração do fluxo principal:

```text
configuração → leitura → login → processamento → atualização → encerramento
```

### 2. `AutomationConfig`

Objeto imutável responsável por carregar, validar e disponibilizar as configurações da aplicação.

A prioridade de configuração é:

```text
Variável de ambiente > arquivo .env
```

### 3. `CursoModel`

Modelo de dados utilizado para transportar as informações do curso entre a planilha e a automação web.

### 4. `GoogleSheetsService`

Encapsula a integração com a Google Sheets API, realiza a leitura dos cursos e atualiza os resultados em lote.

### 5. `SistemaNavegador`

Encapsula o Selenium WebDriver, gerencia o ciclo de vida do Chrome, realiza o login, navega pelo sistema e coleta os indicadores dos cursos.

---

## 🔜 Próximos Passos

- Adicionar processamento parcial e retomada após falhas.
- Persistir status individual de cada curso.
- Implementar retry com backoff para chamadas externas.
- Substituir seletores frágeis por seletores estáveis ou Page Objects.
- Adicionar modo `dry-run` para testes sem alterações reais.
- Evoluir a gestão de credenciais Google para autenticação sem chave JSON permanente.
- Ampliar os testes para parsing de planilha e processamento de cursos.
- Adicionar relatórios operacionais da execução.

---

## 📫 Contato

* Email: **jorge.paiv4@gmail.com**
* LinkedIn: [Jorge Henrique](https://www.linkedin.com/in/jorge-henrique-16b567263/)
