package com.rpa.api.automation;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GoogleSheetsService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsService.class);
    private static final Pattern PADRAO_TURMA_ID = Pattern.compile("TurmaId=(\\d+)");

    private final String spreadsheetId;
    private final String nomeAba;
    private final Sheets servicePlanilha;

    public GoogleSheetsService() {
        this(AutomationConfig.load());
    }

    GoogleSheetsService(AutomationConfig config) {
        this.spreadsheetId = config.getSpreadsheetId();
        this.nomeAba = config.getNomeAba();

        Path credentialsPath = config.getGoogleCredentialsPath();
        validateCredentialsFile(credentialsPath);

        try (InputStream credentialsStream = Files.newInputStream(credentialsPath)) {
            log.info("Autenticando com o Google Cloud...");
            GoogleCredentials credenciais = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));

            this.servicePlanilha = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credenciais))
                    .setApplicationName("Robo")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar no Google.", e);
        }
    }

    public List<CursoModel> buscarCursos() throws Exception {
        log.info("Buscando cursos na nuvem...");

        ValueRange resposta = servicePlanilha.spreadsheets().values()
                .get(spreadsheetId, nomeAba + "!A2:C")
                .execute();

        List<List<Object>> valores = resposta.getValues();
        List<CursoModel> listaDeCursosValidos = new ArrayList<>();

        if (valores == null || valores.isEmpty()) {
            log.info("A planilha está vazia.");
            return listaDeCursosValidos;
        }

        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate dataDeHoje = LocalDate.now();
        int linhaAtualPlanilha = 2;

        for (List<Object> linha : valores) {
            if (linha == null || linha.isEmpty() || linha.get(0) == null) {
                linhaAtualPlanilha++;
                continue;
            }

            String textoColunaA = linha.get(0).toString().trim();

            if (textoColunaA.isEmpty() || textoColunaA.toUpperCase().contains("PROAMIS")) {
                linhaAtualPlanilha++;
                continue;
            }

            String dataCursoStr = (linha.size() > 1 && linha.get(1) != null) ? linha.get(1).toString().trim() : "";

            if (!dataCursoStr.isEmpty()) {
                try {
                    LocalDate dataDoCurso = LocalDate.parse(dataCursoStr, formatadorData);
                    if (dataDoCurso.isBefore(dataDeHoje) || dataDoCurso.isEqual(dataDeHoje)) {
                        log.info("Pulando curso '{}': A data ({}) é de hoje ou já passou.", textoColunaA, dataCursoStr);
                        linhaAtualPlanilha++;
                        continue;
                    }
                } catch (DateTimeParseException e) {
                    log.warn("Formato de data inválido para o curso '{}': '{}'", textoColunaA, dataCursoStr);
                }
            }

            String urlSistema = (linha.size() > 2 && linha.get(2) != null) ? linha.get(2).toString().trim() : "";
            String idExtraido = "";

            if (!urlSistema.isEmpty()) {
                Matcher buscador = PADRAO_TURMA_ID.matcher(urlSistema);
                if (buscador.find()) {
                    idExtraido = buscador.group(1);
                }
            }
            if (idExtraido.isEmpty()) {
                log.warn("Nenhum 'TurmaId' localizado na linha {} para o curso: {}. Pulando...", linhaAtualPlanilha, textoColunaA);
                linhaAtualPlanilha++;
                continue;
            }

            CursoModel curso = new CursoModel(Integer.parseInt(idExtraido), textoColunaA, dataCursoStr, linhaAtualPlanilha);
            listaDeCursosValidos.add(curso);

            linhaAtualPlanilha++;
        }

        return listaDeCursosValidos;
    }

    public void salvarResultadosEmLote(List<CursoModel> cursosProcessados) throws Exception {
        log.info("Preparando envio em lote para o Google Sheets...");

        List<ValueRange> dadosLote = new ArrayList<>();

        for (CursoModel curso : cursosProcessados) {
            List<List<Object>> valores = List.of(
                    List.of(curso.getInscritos(), curso.getOuvintes())
            );

            String intervalo = nomeAba + "!D" + curso.getLinhaPlanilha() + ":E" + curso.getLinhaPlanilha();

            ValueRange body = new ValueRange()
                    .setRange(intervalo)
                    .setValues(valores);

            dadosLote.add(body);
        }

        if (dadosLote.isEmpty()) {
            log.info("Nenhum dado para atualizar.");
            return;
        }

        BatchUpdateValuesRequest pacoteEmLote = new BatchUpdateValuesRequest()
                .setValueInputOption("USER_ENTERED")
                .setData(dadosLote);

        servicePlanilha.spreadsheets().values()
                .batchUpdate(spreadsheetId, pacoteEmLote)
                .execute();

        log.info("Sucesso! {} cursos atualizados na planilha.", dadosLote.size());
    }

    private void validateCredentialsFile(Path credentialsPath) {
        if (!Files.isRegularFile(credentialsPath) || !Files.isReadable(credentialsPath)) {
            throw new IllegalArgumentException("Configuração inválida: arquivo de credenciais do Google não está acessível.");
        }
    }
}
