package com.rpa.api.automation;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AutomationConfigTest {

    @Test
    void shouldPreferEnvironmentValuesOverDotenvValues() {
        Map<String, String> env = validBaseConfig();
        env.put("URL_SISTEMA", "https://env.example.com/turma?TurmaId=");

        Map<String, String> dotenv = validBaseConfig();
        dotenv.put("URL_SISTEMA", "https://dotenv.example.com/turma?TurmaId=");

        AutomationConfig config = AutomationConfig.fromSources(env, dotenv);

        assertThat(config.getUrlSistema()).isEqualTo("https://env.example.com/turma?TurmaId=");
    }

    @Test
    void shouldLoadValueFromDotenvWhenEnvironmentIsMissing() {
        Map<String, String> env = new HashMap<>();
        Map<String, String> dotenv = validBaseConfig();

        AutomationConfig config = AutomationConfig.fromSources(env, dotenv);

        assertThat(config.getSpreadsheetId()).isEqualTo("spreadsheet-id");
        assertThat(config.getTempoEsperaSegundos()).isEqualTo(15);
        assertThat(config.getTempoEsperaLoginSegundos()).isEqualTo(60);
    }

    @Test
    void shouldFailWhenRequiredConfigIsMissing() {
        Map<String, String> env = validBaseConfig();
        env.remove("SPREADSHEET_ID");

        assertThatThrownBy(() -> AutomationConfig.fromSources(env, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("SPREADSHEET_ID");
    }

    @Test
    void shouldFailWhenTimeoutIsNotPositiveInteger() {
        Map<String, String> env = validBaseConfig();
        env.put("TEMPO_ESPERA_SEGUNDOS", "0");

        assertThatThrownBy(() -> AutomationConfig.fromSources(env, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TEMPO_ESPERA_SEGUNDOS");
    }

    @Test
    void shouldFailWhenTimeoutIsNotANumber() {
        Map<String, String> env = validBaseConfig();
        env.put("TEMPO_ESPERA_LOGIN_SEGUNDOS", "abc");

        assertThatThrownBy(() -> AutomationConfig.fromSources(env, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TEMPO_ESPERA_LOGIN_SEGUNDOS");
    }

    @Test
    void shouldFailWhenUrlIsInvalid() {
        Map<String, String> env = validBaseConfig();
        env.put("URL_SISTEMA", "ftp://example.com");

        assertThatThrownBy(() -> AutomationConfig.fromSources(env, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("URL_SISTEMA");
    }

    @Test
    void shouldFailWhenCredentialsPathIsBlank() {
        Map<String, String> env = validBaseConfig();
        env.put("GOOGLE_CREDENTIALS_PATH", "   ");

        assertThatThrownBy(() -> AutomationConfig.fromSources(env, Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GOOGLE_CREDENTIALS_PATH");
    }

    private static Map<String, String> validBaseConfig() {
        Map<String, String> values = new HashMap<>();
        values.put("URL_SISTEMA", "https://example.com/turma?TurmaId=");
        values.put("SPREADSHEET_ID", "spreadsheet-id");
        values.put("NOME_ABA", "ABA");
        values.put("GOOGLE_CREDENTIALS_PATH", "credentials.json");
        values.put("USUARIO_LOGIN", "user");
        values.put("SENHA_LOGIN", "password");
        return values;
    }
}
