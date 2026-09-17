package com.rpa.api.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoogleSheetsService {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsService.class);

    private final String spreadsheetId;
    private final String nomeAba;
    private final Sheets servicePlanilha;

    public GoogleSheetsService() {
        Dotenv dotenv = Dotenv.load();
        this.spreadsheetId = dotenv.get("SPREADSHEET_ID");
        this.nomeAba = dotenv.get("NOME_ABA");
        String credentialsPath = dotenv.get("GOOGLE_CREDENTIALS_PATH");

        try {
            log.info("Autenticando com o Google Cloud...");
            GoogleCredentials credenciais = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS));

            this.servicePlanilha = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credenciais))
                    .setApplicationName("Robo")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao conectar no Google: " + e.getMessage());
        }
    }

    public List<CursoModel> buscarCursos() throws Exception {
        log.info("Buscando cursos na nuvem...");
        ValueRange resposta = servicePlanilha.spreadsheets().values()
                .get(spreadsheetId, nomeAba + "!A2:B")
                .execute();

        List<List<Object>> valores = resposta.getValues();
        List<CursoModel> listaDeCursosValidos = new ArrayList<>();

        if (valores == null || valores.isEmpty()) {
            log.info("A planilha está vazia.");
            return listaDeCursosValidos; // Retorna lista vazia
        }

        int linhaAtualPlanilha = 2;

        for (List<Object> linha : valores) {
            if (linha == null || linha.isEmpty() || linha.get(0) == null) {
                linhaAtualPlanilha++;
                continue;
            }

            String textoColunaA = linha.get(0).toString().trim();

            if (textoColunaA.isEmpty()) {
                linhaAtualPlanilha++;
                continue;
            }

            if (textoColunaA.toUpperCase().contains("PROAMIS")) {
                linhaAtualPlanilha++;
                continue;
            }

            String dataCurso = (linha.size() > 1 && linha.get(1) != null) ? linha.get(1).toString().trim() : "";

            CursoModel curso = new CursoModel(textoColunaA, dataCurso, linhaAtualPlanilha);
            listaDeCursosValidos.add(curso);

            linhaAtualPlanilha++;
        }

        return listaDeCursosValidos;
    }

    public void salvarResultados(CursoModel curso) throws Exception {
        List<List<Object>> valoresAtualizacao = List.of(
                List.of(curso.getInscritos(), curso.getOuvintes())
        );

        ValueRange body = new ValueRange().setValues(valoresAtualizacao);

        String intervaloAtualizacao = nomeAba + "!C" + curso.getLinhaPlanilha() + ":D" + curso.getLinhaPlanilha();

        servicePlanilha.spreadsheets().values()
                .update(spreadsheetId, intervaloAtualizacao, body)
                .setValueInputOption("USER_ENTERED")
                .execute();

        log.info("Salvo na planilha na linha {}!", curso.getLinhaPlanilha());
    }
}