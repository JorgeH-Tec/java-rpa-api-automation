# Java RPA & API Automation - Sincronização de Dados

> **Projeto de Automação:** Pipeline de extração de dados via Web Scraping e sincronização de estado com Google Workspace.
> **Stack:** Java 21 | Selenium WebDriver | Google Sheets API | SLF4J + Logback.
> **Foco Arquitetural:** RPA, Orientação a Objetos, Clean Code, Design Patterns e Cloud Integration.

---

## 📑 Índice

* [📍 Sobre o Repositório](https://www.google.com/search?q=%23-sobre-o-reposit%C3%B3rio)
* [📂 Estrutura de Pastas](https://www.google.com/search?q=%23-estrutura-de-pastas)
* [🛠️ Destaques Técnicos](https://www.google.com/search?q=%23%EF%B8%8F-destaques-t%C3%A9cnicos)
* [🚀 Arquitetura e Módulos](https://www.google.com/search?q=%23-arquitetura-e-m%C3%B3dulos)
* [🔜 Próximos Passos](https://www.google.com/search?q=%23-pr%C3%B3ximos-passos)

---

## 📍 Sobre o Repositório

Solução de RPA desenvolvida em **Java Core** para automatizar a extração e consolidação de dados entre sistemas institucionais legados (SPA) e a nuvem. O pipeline consome filas de processamento via **Google Sheets API**, assume o controle de instâncias de navegação via **Selenium WebDriver** para *parsing* do sistema alvo e sincroniza os resultados de forma autônoma.

O repositório demonstra a refatoração de scripts procedurais para uma **Arquitetura Orientada a Objetos** robusta, aplicando princípios SOLID, isolamento de domínios, gestão segura de credenciais e telemetria para operação em ambientes de produção.

---

## 📂 Estrutura de Pastas

```bash
├── src/
│   └── main/
│       ├── java/com/rpa/api/automation/
│       │   ├── Automation.java            # Classe Main
│       │   ├── CursoModel.java            
│       │   ├── GoogleSheetsService.java   
│       │   └── SistemaNavegador.java      
│       └── resources/
│           └── logback.xml                
├── .env.example                           # Exemplo de formato para Credenciais
├── .gitignore                             
├── pom.xml                                
└── README.md                              

```

---

## 🛠️ Destaques Técnicos

Práticas de engenharia de software implementadas para garantir resiliência, segurança e manutenibilidade:

### 🛡️ Secret Management (Dotenv)

Isolamento de credenciais e variáveis de ambiente seguindo as premissas do *12-Factor App*. Nenhuma string de conexão, chave JSON ou credencial de sistema reside no código-fonte, permitindo a transição segura entre ambientes (Homologação/Produção) alterando apenas configurações de SO.

### 🧩 Abstração de DOM (Page Objects e Constantes)

Desacoplamento estrito entre lógica de negócio e estrutura HTML. O mapeamento de seletores (XPaths/IDs) é centralizado no topo da classe via atributos `static final`, garantindo manutenção ágil (O(1)) frente a atualizações estruturais na UI do sistema alvo.

### ⚡ Estratégia Fail-Fast e Eager Loading

Consumo síncrono e integral da API REST, alocando os dados em memória RAM via estruturas imutáveis (`List.of()`) *antes* da alocação de recursos pesados (instância do ChromeDriver). Previne overhead computacional imediato caso ocorram falhas de rede ou indisponibilidade da nuvem.

### 📊 Telemetria Estruturada (SLF4J + Logback)

Implementação de logging corporativo substituindo saídas padrão (`System.out`). Utilização de *placeholders* (`{}`) para otimização de processamento de strings em tempo de execução. Roteamento de logs com formatação de *timestamp* e *thread* no console, aliado a um *FileAppender* configurado para persistir rastros de exceções severas localmente (filtrado via `.gitignore`).

---

## 🚀 Arquitetura e Módulos

**1. CursoModel (Data Transfer Object):**
Recipiente de dados com atributos imutáveis (`final`). Garante o encapsulamento e a tipagem estrita no trânsito de estado entre as camadas de rede (Google Cloud) e interface web, blindando a aplicação contra mutações indevidas.

**2. GoogleSheetsService (API Integration):**
Encapsula o ecossistema `com.google.api`. Gerencia o protocolo OAuth2 via Service Account e executa operações HTTP (Leitura/Escrita) na nuvem, abstraindo a complexidade de autenticação e serialização JSON do orquestrador.

**3. SistemaNavegador (Web Automation):**
Camada de abstração do ecossistema `org.openqa.selenium`. Gerencia o ciclo de vida do *WebDriver*, implementa rotinas de *Explicit Waits* (`WebDriverWait`) para sincronização com o render assíncrono do SPA e utiliza injeção de scripts no DOM (`JavascriptExecutor`) para contornar *overlays* de interface.

---

## 🔜 Próximos Passos

O roadmap de evolução foca em otimização algorítmica de tempo de execução e integração CI/CD:

1. **Filtro Temporal (java.time):** Implementação de *parser* de datas para avaliação em memória do estado de expiração dos registros. O *Crawler* pulará instantaneamente instâncias obsoletas, reduzindo o tempo de ciclo e o consumo de banda de rede.
2. **Heurística de Interação (Look-Before-You-Leap):** Refatoração da árvore de decisão no *Dashboard*. A aplicação fará o *parsing* condicional de métricas na UI (`findElements`) para validar mudanças de estado *antes* de despachar eventos de clique, eliminando esperas síncronas cegas e exceções de *timeout*.
3. **Cloud Execution e Zero-Trust (GitHub Actions):** Migração para *runners* efêmeros em nuvem operando de forma autônoma (modo *Headless*). A execução será acionada por `workflow_dispatch`, injetando credenciais temporárias em *runtime* via inputs criptografados, eliminando a persistência de senhas de usuários.
4. **Otimização de I/O (Batch API):** Transição de requisições HTTP atômicas para processamento em lote. A consolidação do estado final ocorrerá integralmente em memória para posterior submissão em um único *payload* (POST/PUT), minimizando a latência de rede e reduzindo o consumo de cota da REST API.

---

## 📫 Contato

* Email: **jorge.paiv4@gmail.com**
* LinkedIn: [Jorge Henrique](https://www.linkedin.com/in/jorge-henrique-16b567263/)
