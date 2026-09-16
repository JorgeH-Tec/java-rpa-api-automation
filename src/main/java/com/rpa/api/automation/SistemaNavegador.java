package com.rpa.api.automation;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SistemaNavegador {

    private static final String BTN_ACESSO_SISTEMA = "//span[text()='EGOV']";
    private static final String MENU_INSCRICAO = "LisbonTheme_wt268_block_wtMenu_wt261_RichWidgets_wtInscricoes_block_wtMenuItem_wt233";
    private static final String MENU_CONFIRMAR_INSCRICAO = "LisbonTheme_wt268_block_wtMenu_wt261_RichWidgets_wtInscricoes_block_wtMenuSubItems_wt107";
    private static final String BTN_LIMPAR = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wt278";
    private static final String INPUT_NOME = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wtInputNome2";
    private static final String INPUT_DATA = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wtTurma_DataInicioPeriodo";
    private static final String BTN_PESQUISAR = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wt42";
    private static final String BTN_LISTAR = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt319_block_wtContent_wtTableTurmas_ctl03_wt284";
    private static final String BTN_CONFIRMAR_ACAO = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wtContent2_block_wtContent_wt387";
    private static final String LBL_INSCRITOS = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn1_WebPatterns_wt37_block_wtColumn3_WebPatterns_wt380_block_wtNumber";
    private static final String LBL_OUVINTES = "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn2_WebPatterns_wt73_block_wtContent_WebPatterns_wt297_block_wtNumber";
    private static final String TELA_CARREGAMENTO = "divWait";

    private WebDriver navegador;
    private WebDriverWait espera;
    private WebDriverWait esperaLogin;
    private String urlSistema;

    public SistemaNavegador() {
        Dotenv dotenv = Dotenv.load();
        this.urlSistema = dotenv.get("URL_SISTEMA");
        int tempoEspera = Integer.parseInt(dotenv.get("TEMPO_ESPERA_SEGUNDOS"));
        int tempoLogin = Integer.parseInt(dotenv.get("TEMPO_ESPERA_LOGIN_SEGUNDOS"));

        System.out.println("Iniciando o Navegador Chrome...");
        WebDriverManager.chromedriver().setup();
        this.navegador = new ChromeDriver();
        this.navegador.manage().window().maximize();

        this.espera = new WebDriverWait(navegador, Duration.ofSeconds(tempoEspera));
        this.esperaLogin = new WebDriverWait(navegador, Duration.ofSeconds(tempoLogin));
    }

    public void fazerLogin() {
        navegador.get(urlSistema);
        System.out.println("Realize o Login no navegador...");

        WebElement acesso = esperaLogin.until(ExpectedConditions.elementToBeClickable(By.xpath(BTN_ACESSO_SISTEMA)));
        System.out.println("Login detectado! Assumindo o controle...");
        acesso.click();

        espera.until(ExpectedConditions.elementToBeClickable(By.id(MENU_INSCRICAO))).click();
        espera.until(ExpectedConditions.elementToBeClickable(By.id(MENU_CONFIRMAR_INSCRICAO))).click();
    }

    public void processarCurso(CursoModel curso) throws InterruptedException {
        System.out.println("\nProcessando curso: " + curso.getNome() + " | Data: " + curso.getData());

        WebElement btnLimpar = espera.until(ExpectedConditions.presenceOfElementLocated(By.id(BTN_LIMPAR)));
        JavascriptExecutor js = (JavascriptExecutor) navegador;
        js.executeScript("arguments[0].click();", btnLimpar);

        espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(INPUT_NOME))).sendKeys(curso.getNome());

        if (!curso.getData().isEmpty()) {
            espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(INPUT_DATA))).sendKeys(curso.getData());
        }

        espera.until(ExpectedConditions.elementToBeClickable(By.id(BTN_PESQUISAR))).click();
        aguardarCarregamento();

        espera.until(ExpectedConditions.elementToBeClickable(By.id(BTN_LISTAR))).click();
        aguardarCarregamento();

        espera.until(ExpectedConditions.elementToBeClickable(By.id(BTN_CONFIRMAR_ACAO))).click();
        aguardarCarregamento();

        WebElement labelInscritos = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(LBL_INSCRITOS)));
        WebElement labelOuvintes = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(LBL_OUVINTES)));

        int inscritos = Integer.parseInt(labelInscritos.getText().trim());
        int ouvintes = Integer.parseInt(labelOuvintes.getText().trim());

        System.out.println("Extraído -> Inscritos: " + inscritos + " | Ouvintes: " + ouvintes);

        curso.setInscritos(inscritos);
        curso.setOuvintes(ouvintes);
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