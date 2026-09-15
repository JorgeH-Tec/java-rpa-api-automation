package com.rpa.api.automation;

import io.github.cdimascio.dotenv.Dotenv;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.sheets.v4.model.ValueRange;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Automation {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();

        // 1. DADOS DE CONEXÃO DO GOOGLE
        String credentialsPath = dotenv.get("GOOGLE_CREDENTIALS_PATH");
        String spreadsheetId = dotenv.get("SPREADSHEET_ID");
        String nomeAba = dotenv.get("NOME_ABA");
        String urlSistema = dotenv.get("URL_SISTEMA");
        int tempoEsperaLogin = Integer.parseInt(dotenv.get("TEMPO_ESPERA_LOGIN_SEGUNDOS"));
        int tempoEspera = Integer.parseInt(dotenv.get("TEMPO_ESPERA_SEGUNDOS"));

        try {
            System.out.println("Autenticando com o Google Cloud...");
            GoogleCredentials credenciais = GoogleCredentials.fromStream(new FileInputStream(
                    credentialsPath))
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));

            Sheets servicoPlanilha = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credenciais))
                    .setApplicationName("Robo")
                    .build();

            // LENDO OS CURSOS DA PLANILHA
            System.out.println("Buscando cursos na nuvem...");
            ValueRange resposta = servicoPlanilha.spreadsheets().values()
                    .get(spreadsheetId, nomeAba + "!A2:B")
                    .execute();
            List<List<Object>> valores = resposta.getValues();

            if (valores == null || valores.isEmpty()) {
                System.out.println("Nenhuma linha encontrada. Encerrando.");
                return;
            }

            // INICIANDO O SELENIUM
            System.out.println("Iniciando o Navegador...");
            WebDriverManager.chromedriver().setup();
            WebDriver navegador = new ChromeDriver();
            navegador.manage().window().maximize();
            WebDriverWait espera = new WebDriverWait(navegador, Duration.ofSeconds(tempoEspera));

            try {
                // LOGIN DO USUARIO
                navegador.get(urlSistema);
                System.out.println("Realize o Login no navegador...");

                WebDriverWait esperaLogin = new WebDriverWait(navegador, Duration.ofSeconds(tempoEsperaLogin));
                WebElement egov = esperaLogin
                        .until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='EGOV']")));

                System.out.println("Login detectado! Assumindo o controle...");
                egov.click();

                WebElement inscricao = espera.until(ExpectedConditions.elementToBeClickable(
                        By.id("LisbonTheme_wt268_block_wtMenu_wt261_RichWidgets_wtInscricoes_block_wtMenuItem_wt233")));
                inscricao.click();

                WebElement confirmarInscricoes = espera.until(ExpectedConditions.elementToBeClickable(By.id(
                        "LisbonTheme_wt268_block_wtMenu_wt261_RichWidgets_wtInscricoes_block_wtMenuSubItems_wt107")));
                confirmarInscricoes.click();

                // LOOP DE CADA LINHA DA PLANILHA
                int linhaAtualPlanilha = 2; // linha 2 porque a 1 é o cabeçalho

                for (List<Object> linha : valores) {
                    if (linha == null || linha.isEmpty() || linha.get(0) == null) {
                        linhaAtualPlanilha++;
                        continue;
                    }

                    String textoColunaA = linha.get(0).toString().trim();

                    String textoParaVerificar = textoColunaA.toUpperCase();

                    if (textoColunaA.isEmpty() || textoParaVerificar.contains("PROAMIS")) {
                        System.out.println("Pulando linha de divisão: " + textoColunaA);
                        linhaAtualPlanilha++;
                        continue;
                    }

                    String dataCurso = (linha.size() > 1 && linha.get(1) != null) ? linha.get(1).toString().trim() : "";

                    System.out.println("\nProcessando curso: " + textoColunaA + " | Data: " + dataCurso);

                    // Clica em Limpar via JavaScript para passar pela DivWait do OutSystems
                    WebElement btnLimpar = espera.until(ExpectedConditions.presenceOfElementLocated(
                            By.id("LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wt278")));
                    JavascriptExecutor js = (JavascriptExecutor) navegador;
                    js.executeScript("arguments[0].click();", btnLimpar);

                    WebElement inputPesquisa = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(
                            "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wtInputNome2")));
                    inputPesquisa.sendKeys(textoColunaA);
                    WebElement inputData = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(
                            "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wtTurma_DataInicioPeriodo")));
                    inputData.sendKeys(dataCurso);

                    WebElement btnPesquisar = espera.until(ExpectedConditions.elementToBeClickable(
                            By.id("LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt114_block_wtContent_wt42")));
                    btnPesquisar.click();

                    // Esperar tela de carregamento surgir
                    Thread.sleep(1000);
                    espera.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divWait")));
                    Thread.sleep(1000);

                    WebElement btnListar = espera.until(ExpectedConditions.elementToBeClickable(By.id(
                            "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt319_block_wtContent_wtTableTurmas_ctl03_wt284")));
                    btnListar.click();

                    Thread.sleep(1000);
                    espera.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divWait")));
                    Thread.sleep(1000);

                    WebElement btnConfirmar = espera.until(ExpectedConditions.elementToBeClickable(By
                            .id("LisbonTheme_wt171_block_wtMainContent_WebPatterns_wtContent2_block_wtContent_wt387")));
                    btnConfirmar.click();

                    // Esperar tela de carregamento surgir
                    Thread.sleep(1000);
                    espera.until(ExpectedConditions.invisibilityOfElementLocated(By.id("divWait")));
                    Thread.sleep(1000);

                    // Extrai Números
                    WebElement labelInscritos = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(
                            "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn1_WebPatterns_wt37_block_wtColumn3_WebPatterns_wt380_block_wtNumber")));
                    WebElement labelOuvintes = espera.until(ExpectedConditions.visibilityOfElementLocated(By.id(
                            "LisbonTheme_wt171_block_wtMainContent_WebPatterns_wt411_block_wtColumn2_WebPatterns_wt73_block_wtContent_WebPatterns_wt297_block_wtNumber")));

                    int inscritos = Integer.parseInt(labelInscritos.getText().trim());
                    int ouvintes = Integer.parseInt(labelOuvintes.getText().trim());

                    System.out.println("Extraído -> Inscritos: " + inscritos + " | Ouvintes: " + ouvintes);

                    // Salva na Planilha
                    List<List<Object>> valoresAtualizacao = List.of(
                            Arrays.asList(inscritos, ouvintes)
                    );

                    ValueRange body = new ValueRange().setValues(valoresAtualizacao);

                    String intervaloAtualizacao = nomeAba + "!C" + linhaAtualPlanilha + ":D" + linhaAtualPlanilha;

                    servicoPlanilha.spreadsheets().values()
                            .update(spreadsheetId, intervaloAtualizacao, body)
                            .setValueInputOption("USER_ENTERED")
                            .execute();

                    System.out.println("Salvo na planilha na linha " + linhaAtualPlanilha + "!");

                    linhaAtualPlanilha++;
                }

                System.out.println("\nAutomação finalizada com sucesso!");

            } catch (Exception e) {
                System.err.println("Erro na automação do Selenium: " + e.getMessage());
                e.printStackTrace();
            } finally {
                navegador.quit();
            }

        } catch (Exception e) {
            System.err.println("Erro geral: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
