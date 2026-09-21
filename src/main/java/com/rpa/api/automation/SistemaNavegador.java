package com.rpa.api.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SistemaNavegador {

    private static final Logger log = LoggerFactory.getLogger(SistemaNavegador.class);

    private static final String TXT_USUARIO = "Input_UsernameVal";
    private static final String TXT_SENHA = "Input_PasswordVal";
    private static final String BTN_ENTRAR = "//div[@id='b8-Button']/button";
    private static final String BTN_ORGAO = "//select[@id='DropdownOrgao']/option[@value='99']";
    private static final String BTN_ACESSO_SISTEMA = "//span[text()='EGOV']";
    private static final String BTN_CONFIRMAR_ACAO = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wtContent2_block_wtContent_wt387";
    private static final String LBL_INSCRITOS = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn1_WebPatterns_wt37_block_wtColumn3_WebPatterns_wt380_block_wtNumber";
    private static final String LBL_OUVINTES = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn2_WebPatterns_wt73_block_wtContent_WebPatterns_wt297_block_wtNumber";
    private static final String LBL_PRE_INSCRITOS = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn1_WebPatterns_wt361_block_wtColumn2_WebPatterns_wt153_block_wtNumber";
    private static final String LBL_SOLICITACOES_OUVINTES = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn2_WebPatterns_wt73_block_wtContent_WebPatterns_wt25_block_wtNumber";
    private static final String LBL_CANCELADOS = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn1_WebPatterns_wt361_block_wtColumn2_WebPatterns_wt30_block_wtNumber";
    private static final String TELA_CARREGAMENTO = "divWait";

    private final WebDriver navegador;
    private final WebDriverWait espera;
    private final WebDriverWait esperaLogin;
    private final String urlSistema;
    private final String usuarioLogin;
    private final String senhaLogin;

    public SistemaNavegador() {
        Dotenv dotenv;
        try {
            dotenv = Dotenv.load();
        } catch (Exception e) {
            dotenv = null; // Ignora se o arquivo .env físico não existir (ambiente de nuvem)
        }

        // Leitura segura com fallback para as variáveis de ambiente do sistema/GitHub
        this.urlSistema = (dotenv != null && dotenv.get("URL_SISTEMA") != null)
                ? dotenv.get("URL_SISTEMA")
                : System.getenv("URL_SISTEMA");

        this.usuarioLogin = (dotenv != null && dotenv.get("USUARIO_LOGIN") != null)
                ? dotenv.get("USUARIO_LOGIN")
                : System.getenv("USUARIO_LOGIN");

        this.senhaLogin = (dotenv != null && dotenv.get("SENHA_LOGIN") != null)
                ? dotenv.get("SENHA_LOGIN")
                : System.getenv("SENHA_LOGIN");

        String tempoEsperaStr = (dotenv != null && dotenv.get("TEMPO_ESPERA_SEGUNDOS") != null)
                ? dotenv.get("TEMPO_ESPERA_SEGUNDOS")
                : System.getenv("TEMPO_ESPERA_SEGUNDOS");

        String tempoLoginStr = (dotenv != null && dotenv.get("TEMPO_ESPERA_LOGIN_SEGUNDOS") != null)
                ? dotenv.get("TEMPO_ESPERA_LOGIN_SEGUNDOS")
                : System.getenv("TEMPO_ESPERA_LOGIN_SEGUNDOS");

        int tempoEspera = Integer.parseInt(tempoEsperaStr != null ? tempoEsperaStr : "15");
        int tempoLogin = Integer.parseInt(tempoLoginStr != null ? tempoLoginStr : "60");

        log.info("Iniciando o Navegador Chrome (Modo Nuvem)...");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        this.navegador = new ChromeDriver(options);
        this.navegador.manage().window().maximize();

        this.espera = new WebDriverWait(navegador, Duration.ofSeconds(tempoEspera));
        this.esperaLogin = new WebDriverWait(navegador, Duration.ofSeconds(tempoLogin));
    }

    public void fazerLogin() {
        log.info("Acessando a página de login...");
        navegador.get(urlSistema);

        esperaLogin.until(ExpectedConditions.visibilityOfElementLocated(By.id(TXT_USUARIO))).sendKeys(usuarioLogin);
        navegador.findElement(By.id(TXT_SENHA)).sendKeys(senhaLogin);

        log.info("Selecionando o órgão...");
        esperaLogin.until(ExpectedConditions.elementToBeClickable(By.xpath(BTN_ORGAO))).click();

        log.info("Formulário preenchido, efetuando login...");
        esperaLogin.until(ExpectedConditions.elementToBeClickable(By.xpath(BTN_ENTRAR))).click();

        esperaLogin.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BTN_ACESSO_SISTEMA)));
        log.info("Login automatizado concluído com sucesso! Assumindo o controle do Dashboard...");
    }

    public void processarCurso(CursoModel curso) {
        try {
            log.info("\nProcessando curso: {} | Data: {} | ID: {}", curso.getNome(), curso.getData(), curso.getId());

            String urlDireta = urlSistema + curso.getId();
            navegador.get(urlDireta);
            aguardarCarregamento();

            // Look-Before-You-Leap
            int preInscritos = Integer.parseInt(navegador.findElement(By.id(LBL_PRE_INSCRITOS)).getText().trim());
            int solicitacoesOuvintes = Integer.parseInt(navegador.findElement(By.id(LBL_SOLICITACOES_OUVINTES)).getText().trim());
            int cancelados = Integer.parseInt(navegador.findElement(By.id(LBL_CANCELADOS)).getText().trim());
            int ouvintesConfirmados = Integer.parseInt(navegador.findElement(By.id(LBL_OUVINTES)).getText().trim());

            if (preInscritos > 0 || solicitacoesOuvintes > ouvintesConfirmados || cancelados > 0) {
                log.info("Ações pendentes detectadas (Pré: {}, Solicitados: {}, Cancelados: {}). Confirmando...", preInscritos, solicitacoesOuvintes, cancelados);

                espera.until(ExpectedConditions.elementToBeClickable(By.id(BTN_CONFIRMAR_ACAO))).click();
                aguardarCarregamento();
            } else {
                log.info("Nenhuma inscrição pendente. Pulando a confirmação para economizar tempo.");
            }

            curso.setInscritos(Integer.parseInt(espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(LBL_INSCRITOS))).getText().trim()));
            curso.setOuvintes(Integer.parseInt(espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(LBL_OUVINTES))).getText().trim()));

            log.info("Extraído -> Inscritos: {} | Ouvintes: {}", curso.getInscritos(), curso.getOuvintes());
        } catch(Exception e) {
            log.error("Falha ao processar a página do curso: {}", curso.getNome(), e);
            throw new RuntimeException("Erro ao processar o curso: " + curso.getNome(), e);
        }
    }

    private void aguardarCarregamento() throws InterruptedException {
        Thread.sleep(1000);
        espera.until(ExpectedConditions.invisibilityOfElementLocated(By.id(TELA_CARREGAMENTO)));
        Thread.sleep(1000);
    }

    public void fecharNavegador() {
        if (navegador != null) {
            navegador.quit();
        }
    }
}