package com.rpa.api.automation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Automation {

    private static final Logger log = LoggerFactory.getLogger(Automation.class);

    public static void main(String[] args) {

        log.info("=== INICIANDO ROBÔ ===");

        SistemaNavegador sistema = null;

        try {
            AutomationConfig config = AutomationConfig.load();
            GoogleSheetsService google = new GoogleSheetsService(config);
            List<CursoModel> listaDeCursos = google.buscarCursos();

            if (listaDeCursos.isEmpty()) {
                log.info("Nenhum registro válido encontrado na planilha para processar.");
                return;
            }

            sistema = new SistemaNavegador(config);
            sistema.fazerLogin();

            for (CursoModel cursoAtual : listaDeCursos) {
                sistema.processarCurso(cursoAtual);
            }

            google.salvarResultadosEmLote(listaDeCursos);

            log.info("\nAutomação finalizada com sucesso!");

        } catch (Exception e) {
            log.error("Erro crítico na execução do robô", e);
            throw new RuntimeException("Falha na execução da automação.", e);
        } finally {
            if (sistema != null) {
                sistema.fecharNavegador();
            }
        }
    }
}
