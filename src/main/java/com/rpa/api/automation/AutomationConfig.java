package com.rpa.api.automation;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.github.cdimascio.dotenv.DotenvException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AutomationConfig {

    private static final int TEMPO_ESPERA_PADRAO = 15;
    private static final int TEMPO_ESPERA_LOGIN_PADRAO = 60;

    private final String urlSistema;
    private final String spreadsheetId;
    private final String nomeAba;
    private final Path googleCredentialsPath;
    private final String usuarioLogin;
    private final String senhaLogin;
    private final int tempoEsperaSegundos;
    private final int tempoEsperaLoginSegundos;

    private AutomationConfig(
            String urlSistema,
            String spreadsheetId,
            String nomeAba,
            Path googleCredentialsPath,
            String usuarioLogin,
            String senhaLogin,
            int tempoEsperaSegundos,
            int tempoEsperaLoginSegundos
    ) {
        this.urlSistema = urlSistema;
        this.spreadsheetId = spreadsheetId;
        this.nomeAba = nomeAba;
        this.googleCredentialsPath = googleCredentialsPath;
        this.usuarioLogin = usuarioLogin;
        this.senhaLogin = senhaLogin;
        this.tempoEsperaSegundos = tempoEsperaSegundos;
        this.tempoEsperaLoginSegundos = tempoEsperaLoginSegundos;
    }

    public static AutomationConfig load() {
        return fromSources(System.getenv(), loadDotenvValues());
    }

    static AutomationConfig fromSources(Map<String, String> environmentValues, Map<String, String> dotenvValues) {
        Objects.requireNonNull(environmentValues, "environmentValues");
        Objects.requireNonNull(dotenvValues, "dotenvValues");

        String urlSistema = requireNonBlank("URL_SISTEMA", environmentValues, dotenvValues);
        validateUrl(urlSistema);

        String spreadsheetId = requireNonBlank("SPREADSHEET_ID", environmentValues, dotenvValues);
        String nomeAba = requireNonBlank("NOME_ABA", environmentValues, dotenvValues);

        String googleCredentialsPathRaw = requireNonBlank("GOOGLE_CREDENTIALS_PATH", environmentValues, dotenvValues);
        Path googleCredentialsPath = parseCredentialsPath(googleCredentialsPathRaw);

        String usuarioLogin = requireNonBlank("USUARIO_LOGIN", environmentValues, dotenvValues);
        String senhaLogin = requireNonBlank("SENHA_LOGIN", environmentValues, dotenvValues);

        int tempoEsperaSegundos = parsePositiveInt(
                "TEMPO_ESPERA_SEGUNDOS",
                valueOf("TEMPO_ESPERA_SEGUNDOS", environmentValues, dotenvValues),
                TEMPO_ESPERA_PADRAO
        );
        int tempoEsperaLoginSegundos = parsePositiveInt(
                "TEMPO_ESPERA_LOGIN_SEGUNDOS",
                valueOf("TEMPO_ESPERA_LOGIN_SEGUNDOS", environmentValues, dotenvValues),
                TEMPO_ESPERA_LOGIN_PADRAO
        );

        return new AutomationConfig(
                urlSistema,
                spreadsheetId,
                nomeAba,
                googleCredentialsPath,
                usuarioLogin,
                senhaLogin,
                tempoEsperaSegundos,
                tempoEsperaLoginSegundos
        );
    }

    public String getUrlSistema() {
        return urlSistema;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public String getNomeAba() {
        return nomeAba;
    }

    public Path getGoogleCredentialsPath() {
        return googleCredentialsPath;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public String getSenhaLogin() {
        return senhaLogin;
    }

    public int getTempoEsperaSegundos() {
        return tempoEsperaSegundos;
    }

    public int getTempoEsperaLoginSegundos() {
        return tempoEsperaLoginSegundos;
    }

    private static Map<String, String> loadDotenvValues() {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            Map<String, String> values = new HashMap<>();
            for (DotenvEntry entry : dotenv.entries()) {
                values.put(entry.getKey(), entry.getValue());
            }
            return values;
        } catch (DotenvException e) {
            throw new IllegalStateException("Falha ao carregar configurações locais do arquivo .env.", e);
        }
    }

    private static String requireNonBlank(String key, Map<String, String> environmentValues, Map<String, String> dotenvValues) {
        String value = valueOf(key, environmentValues, dotenvValues);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Configuração inválida: " + key + " não foi informada.");
        }
        return value;
    }

    private static String valueOf(String key, Map<String, String> environmentValues, Map<String, String> dotenvValues) {
        String envValue = trimToNull(environmentValues.get(key));
        if (envValue != null) {
            return envValue;
        }
        return trimToNull(dotenvValues.get(key));
    }

    private static int parsePositiveInt(String key, String value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed <= 0) {
                throw new IllegalArgumentException("Configuração inválida: " + key + " deve ser um número inteiro positivo.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Configuração inválida: " + key + " deve ser um número inteiro positivo.", e);
        }
    }

    private static void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("Configuração inválida: URL_SISTEMA deve usar http ou https.");
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new IllegalArgumentException("Configuração inválida: URL_SISTEMA deve conter host válido.");
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Configuração inválida: URL_SISTEMA está malformada.", e);
        }
    }

    private static Path parseCredentialsPath(String rawPath) {
        try {
            return Paths.get(rawPath).normalize();
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Configuração inválida: GOOGLE_CREDENTIALS_PATH está malformado.", e);
        }
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
