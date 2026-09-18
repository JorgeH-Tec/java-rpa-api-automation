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


### ⚡ Otimização de Cliques (Look-Before-You-Leap)

Refatoração da árvore de decisão baseada na leitura prévia de métricas do Dashboard da interface alvo. O robô realiza a extração e o cálculo matemático de variáveis de estado da UI antes de despachar eventos de ação. A execução de cliques e mutações de estado ocorre estritamente sob demanda, eliminando esperas síncronas cegas, mitigando a latência operacional e blindando o processo contra falhas de timeout.

### ⏲️ Validação Temporal (Time Travel)

Implementação de um parser de datas via java.time na camada de ingestão de dados. O pipeline avalia o estado de expiração dos registros diretamente em memória RAM (LocalDate), permitindo que o crawler ignore e salte instantaneamente instâncias obsoletas (cursos vencidos ou do dia corrente). Essa abordagem Fail-Fast reduz drasticamente o consumo de banda de rede e otimiza o tempo de ciclo da esteira de RPA antes de alocar recursos de navegação pesados.

### 🛡️ Secret Management (Dotenv)

Isolamento de credenciais e variáveis de ambiente seguindo as premissas do *12-Factor App*. Nenhuma string de conexão, chave JSON ou credencial de sistema reside no código-fonte, permitindo a transição segura entre ambientes (Homologação/Produção) alterando apenas configurações de SO.

### 📊 Telemetria Estruturada (SLF4J + Logback)

Implementação de logging corporativo substituindo saídas padrão (`System.out`). Utilização de *placeholders* (`{}`) para otimização de processamento de strings em tempo de execução. Roteamento de logs com formatação de *timestamp* e *thread* no console, aliado a um *FileAppender* configurado para persistir rastros de exceções severas localmente (filtrado via `.gitignore`).


---

## 🚀 Arquitetura e Módulos

**1. CursoModel (Data Transfer Object):**
Recipiente de dados com atributos imutáveis (`final`). Garante o encapsulamento e a tipagem estrita no trânsito de estado entre as camadas de rede (Google Cloud) e interface web, blindando a aplicação contra mutações indevidas.

**2. GoogleSheetsService (API Integration & Fail-Fast):**
Encapsula o ecossistema `com.google.api` via OAuth2 (Service Account). Realiza a leitura otimizada do intervalo da planilha, aplicando filtragem temporal em memória (`java.time` / *Time Travel*) para descarte imediato de registros vencidos ou correntes, além de extração determinística de parâmetros via *Regex* diretamente da nuvem antes de alocar recursos web.

**3. SistemaNavegador (Web Automation):**
Camada de abstração do ecossistema `org.openqa.selenium`. Gerencia o ciclo de vida do *WebDriver*, executa *bypass* de interface navegando diretamente via URL construída dinamicamente (`.env` + ID) e aplica a heurística *Look-Before-You-Leap* para avaliar métricas de estado da UI antes de despachar eventos de clique condicional.

---

## 🔜 Próximos Passos

O roadmap de evolução foca em otimização da latência de rede e integração CI/CD:

3. **Cloud Execution e Zero-Trust (GitHub Actions):** Migração para *runners* efêmeros em nuvem operando de forma autônoma (modo *Headless*). A execução será acionada por `workflow_dispatch`, injetando credenciais temporárias em *runtime* via inputs criptografados, eliminando a persistência de senhas de usuários.
4. **Otimização de I/O (Batch API):** Transição de requisições HTTP atômicas para processamento em lote. A consolidação do estado final ocorrerá integralmente em memória para posterior submissão em um único *payload* (POST/PUT), minimizando a latência de rede e reduzindo o consumo de cota da REST API.

---

## 📫 Contato

* Email: **jorge.paiv4@gmail.com**
* LinkedIn: [Jorge Henrique](https://www.linkedin.com/in/jorge-henrique-16b567263/)
