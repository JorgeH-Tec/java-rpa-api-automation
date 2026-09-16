# Java RPA & API Automation - Sincronização de Dados

> **Projeto de Automação:** Extração de dados via Web Scraping e sincronização em tempo real com o Google Workspace.
> **Linguagem:** Java 21 | **Foco:** RPA, Orientação a Objetos, Clean Code e Cloud Integration.

---

## 📑 Índice

* [📍 Sobre o Repositório](https://www.google.com/search?q=%23-sobre-o-reposit%C3%B3rio)
* [📂 Estrutura de Pastas](https://www.google.com/search?q=%23-estrutura-de-pastas)
* [🛠️ Destaques Técnicos](https://www.google.com/search?q=%23%EF%B8%8F-destaques-t%C3%A9cnicos)
* [🚀 Arquitetura e Módulos](https://www.google.com/search?q=%23-arquitetura-e-m%C3%B3dulos)
* [🔜 Próximos Passos](https://www.google.com/search?q=%23-pr%C3%B3ximos-passos)

---

## 📍 Sobre o Repositório

Este projeto soluciona um gargalo real de negócios: a extração manual de dados de sistemas institucionais legados (SPA). O robô desenvolvido em **Java** orquestra o ciclo de vida completo do dado: consome uma lista de tarefas da nuvem via **Google Sheets API**, assume o controle do navegador via **Selenium WebDriver** para raspar os dados do sistema alvo e consolida os resultados de volta na nuvem de forma autônoma.

O foco técnico do repositório é demonstrar a refatoração de scripts procedurais pesados para uma **Arquitetura Orientada a Objetos**, aplicando princípios SOLID, isolamento de domínios e proteção de dados sensíveis.

---

## 📂 Estrutura de Pastas

```bash
├── src/main/java/com/rpa/api/automation/
│   ├── Automation.java            # Main
│   ├── CursoModel.java            
│   ├── GoogleSheetsService.java   
│   └── SistemaNavegador.java      
├── .env.example                   # Template seguro de variáveis de ambiente
├── .gitignore                     
└── pom.xml                        

```

---

## 🛠️ Destaques Técnicos

Neste repositório, as práticas de engenharia de software garantem a estabilidade, segurança e manutenibilidade da automação em ambientes produtivos:

### 🛡️ Gestão de Segredos e Variáveis de Ambiente (Dotenv)

O código fonte é estritamente limpo de informações sensíveis (IDs de planilhas, credenciais JSON e URLs do sistema) através da implementação da biblioteca `dotenv-java`. O isolamento das configurações garante que o robô possa ser migrado de um servidor de homologação para produção modificando apenas um arquivo oculto no sistema operacional.

### 🧩 Padrão de Projeto (Page Objects e Constantes)

A classe que manipula a interface web não mistura lógica de negócio com mapeamento HTML. A utilização do padrão de constantes (`static final`) concentra todas as âncoras da página (XPaths e IDs) no topo da classe, tornando a manutenção imediata caso a interface do sistema alvo receba atualizações.

### ⚡ Estratégia Fail-Fast e Otimização de Memória

A arquitetura consome toda a planilha da nuvem de uma única vez para a memória RAM (alocando os dados em estruturas imutáveis `List.of()`) *antes* de instanciar o navegador pesado do Chrome. Isso garante que a automação aborte silenciosamente se a nuvem estiver inacessível, economizando recursos computacionais e prevenindo falhas na metade do processamento.

---

## 🚀 Arquitetura e Módulos

**1. CursoModel (Data Transfer Object):**
Atua como o recipiente imutável de dados. Responsável pelo trânsito das informações (nome, data, linha de processamento e resultados da raspagem) entre a camada de nuvem e a camada web, garantindo um contrato forte e tipado.

**2. GoogleSheetsService (Integração de Nuvem):**
Encapsula o ecossistema `com.google.api`. O construtor é auto-suficiente para autenticar a chave JSON do Service Account no Google Cloud. Ele expõe métodos polidos para ler as tarefas (`buscarCursos`) e consolidar relatórios (`salvarResultados`) via HTTP POST/PUT.

**3. SistemaNavegador (Automação Web):**
Abstração total do ecossistema `org.openqa.selenium`. O orquestrador não sabe o que é um `WebElement`. Esta classe gerencia instâncias automáticas de ChromeDriver (via WebDriverManager), domina a renderização assíncrona de SPAs com esperas explícitas (`WebDriverWait`) e utiliza injeção de JavaScript para contornar overlays bloqueantes no DOM.

---

## 🔜 Próximos Passos

A evolução deste repositório acompanhará requisitos de nível corporativo e escalabilidade em nuvem:

1. **Telemetria e Observabilidade:** Substituição das saídas padrão do console (`System.out`) e empilhamentos crus (`printStackTrace`) pela implementação de bibliotecas corporativas de *logging* (SLF4J/Logback) para rastreabilidade de falhas invisíveis em servidores noturnos.
2. **Operações em Lote (Batch API):** Refatorar o método de salvamento do `GoogleSheetsService` para consolidar o relatório inteiro na memória e efetuar apenas uma única requisição HTTP massiva de gravação no final da esteira, minimizando a cota de uso da API REST.
3. **Execução Headless:** Configuração do ChromeOptions para execução invisível sem interface gráfica, preparando a imagem Java para ser encapsulada em um contêiner Docker.

---

## 📫 Contato

* Email: **jorge.paiv4@gmail.com**
* LinkedIn: [Jorge Henrique](https://www.linkedin.com/in/jorge-henrique-16b567263/)
