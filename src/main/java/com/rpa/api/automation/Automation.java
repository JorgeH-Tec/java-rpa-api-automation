package com.rpa.api.automation;

import java.util.List;

public class Automation {

    public static void main(String[] args) {

        System.out.println("=== INICIANDO ROBÔ ===");

        SistemaNavegador sistema = null;

        try {

            GoogleSheetsService google = new GoogleSheetsService();
            List<CursoModel> listaDeCursos = google.buscarCursos();

            if (listaDeCursos.isEmpty()) {
                System.out.println("Nenhum registro válido encontrado na planilha para processar.");
                return;
            }

            sistema = new SistemaNavegador();
            sistema.fazerLogin();

            for (CursoModel cursoAtual : listaDeCursos) {
                sistema.processarCurso(cursoAtual);

                google.salvarResultados(cursoAtual);
            }

            System.out.println("\nAutomação finalizada com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro crítico na execução do robô: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (sistema != null) {
                sistema.fecharNavegador();
            }
        }
    }
}