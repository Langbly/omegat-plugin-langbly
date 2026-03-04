package com.langbly.omegat;

import java.awt.Window;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;


import org.omegat.core.Core;
import org.omegat.core.machinetranslators.BaseCachedTranslate;
import org.omegat.util.HttpConnectionUtils;
import org.omegat.util.Language;
import org.omegat.util.Preferences;

/**
 * OmegaT machine translation plugin for the Langbly API.
 *
 * Langbly provides a Google Translate v2-compatible REST API with support
 * for 100+ languages, glossaries, and EU data residency.
 *
 * Configuration is stored in OmegaT preferences:
 * - API key (required)
 * - Base URL (defaults to https://api.langbly.com, can be set to https://eu.langbly.com)
 */
public class LangblyTranslate extends BaseCachedTranslate {

    private static final String PREF_API_KEY = "langbly.api.key";
    private static final String PREF_BASE_URL = "langbly.api.base.url";
    private static final String DEFAULT_BASE_URL = "https://api.langbly.com";

    /**
     * Register and unregister hooks for OmegaT plugin lifecycle.
     */
    public static void loadPlugins() {
        Core.registerMachineTranslationClass(LangblyTranslate.class);
    }

    public static void unloadPlugins() {
        // nothing to clean up
    }

    @Override
    public String getName() {
        return "Langbly Translate";
    }

    @Override
    protected String getPreferenceName() {
        return "allow_langbly_translate";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public void showConfigurationUI(Window parent) {
        LangblyConfigDialog dialog = new LangblyConfigDialog(parent);
        dialog.setVisible(true);
    }

    @Override
    protected String translate(Language sLang, Language tLang, String text) throws Exception {
        String apiKey = getApiKey();
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }

        String baseUrl = getBaseUrl();
        String url = baseUrl + "/v2?key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name());

        // Build POST parameters (Google v2 compatible)
        Map<String, String> params = new TreeMap<>();
        params.put("q", text);
        params.put("source", sLang.getLanguage());
        params.put("target", tLang.getLanguage());
        params.put("format", "text");

        Map<String, String> headers = new TreeMap<>();

        String response;
        try {
            response = HttpConnectionUtils.post(url, params, headers);
        } catch (IOException e) {
            throw new Exception("Langbly API request failed: " + e.getMessage(), e);
        }

        return parseTranslation(response);
    }

    /**
     * Parse the Google v2-compatible JSON response.
     *
     * Expected format:
     * {"data":{"translations":[{"translatedText":"..."}]}}
     *
     * Error format:
     * {"error":{"message":"...","code":400}}
     */
    private String parseTranslation(String json) throws Exception {
        if (json == null || json.isEmpty()) {
            throw new Exception("Empty response from Langbly API");
        }

        // Check for error response
        if (json.contains("\"error\"")) {
            String message = extractJsonString(json, "message");
            if (message != null) {
                throw new Exception("Langbly API error: " + message);
            }
            throw new Exception("Langbly API returned an error");
        }

        // Extract translatedText from successful response
        String translated = extractJsonString(json, "translatedText");
        if (translated == null) {
            throw new Exception("Could not parse Langbly API response");
        }

        return unescapeJson(translated);
    }

    /**
     * Extract a string value for a given key from a JSON string.
     * Simple parser sufficient for the well-known Google v2 response format.
     */
    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx < 0) {
            return null;
        }

        // Find the colon after the key
        int colonIdx = json.indexOf(':', keyIdx + search.length());
        if (colonIdx < 0) {
            return null;
        }

        // Find the opening quote of the value
        int startQuote = json.indexOf('"', colonIdx + 1);
        if (startQuote < 0) {
            return null;
        }

        // Find the closing quote, handling escaped quotes
        int endQuote = startQuote + 1;
        while (endQuote < json.length()) {
            char c = json.charAt(endQuote);
            if (c == '\\') {
                endQuote += 2; // skip escaped character
                continue;
            }
            if (c == '"') {
                break;
            }
            endQuote++;
        }

        if (endQuote >= json.length()) {
            return null;
        }

        return json.substring(startQuote + 1, endQuote);
    }

    /**
     * Unescape common JSON escape sequences.
     */
    private String unescapeJson(String s) {
        if (s == null || !s.contains("\\")) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"': sb.append('"'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case '/': sb.append('/'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case 'u':
                        if (i + 5 < s.length()) {
                            String hex = s.substring(i + 2, i + 6);
                            try {
                                sb.append((char) Integer.parseInt(hex, 16));
                                i += 5;
                            } catch (NumberFormatException e) {
                                sb.append(c);
                            }
                        } else {
                            sb.append(c);
                        }
                        break;
                    default: sb.append(c); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String getApiKey() {
        return Preferences.getPreference(PREF_API_KEY);
    }

    private String getBaseUrl() {
        String url = Preferences.getPreference(PREF_BASE_URL);
        if (url == null || url.isEmpty()) {
            return DEFAULT_BASE_URL;
        }
        return url;
    }
}
