package com.twitchy.auth;

import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.twitchy.Config;
import com.twitchy.Twitchy;
import com.twitchy.api.TwitchApiClient;

/**
 * Handles Twitch's implicit OAuth grant (response_type=token) entirely client-side: no client
 * secret is ever needed or stored. Because implicit-grant tokens come back in the URL *fragment*
 * (which browsers never send to a server), the redirect target is a tiny local page that reads
 * the fragment with JavaScript and bounces it back to us as a query string on a second request.
 */
public class TwitchAuth {

    /** Scopes requested. Adjust here if you add features that need more. */
    public static final String[] SCOPES = { "channel:read:redemptions", "channel:manage:redemptions", "user:write:chat",
        "user:bot", "chat:read", "user:read:chat" };

    private static final SecureRandom RANDOM = new SecureRandom();

    private static HttpServer activeServer;

    private TwitchAuth() {}

    public static String redirectUri() {
        return "http://localhost:" + Config.callbackPort + "/twitchy-callback";
    }

    /**
     * Opens the system browser to Twitch's authorize page and waits (up to 3 minutes) for the
     * user to approve. Resolves the broadcaster's user id/login via Helix once a token is captured.
     */
    public static CompletableFuture<TwitchCredentials> beginAuthFlow() {
        CompletableFuture<TwitchCredentials> result = new CompletableFuture<>();

        if (Config.clientId == null || Config.clientId.isBlank()) {
            result.completeExceptionally(
                new IllegalStateException(
                    "No Twitch Client ID configured. Set 'clientId' in config/twitchy.cfg first - see the README for setup steps."));
            return result;
        }

        String state = Long.toHexString(RANDOM.nextLong()) + Long.toHexString(RANDOM.nextLong());

        stopServerIfRunning();

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", Config.callbackPort), 0);
            activeServer = server;

            server.createContext("/twitchy-callback", exchange -> handleLandingPage(exchange));
            server.createContext("/twitchy-callback/capture", exchange -> handleCapture(exchange, state, result));
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            result.completeExceptionally(
                new IOException(
                    "Could not start local OAuth callback server on port " + Config.callbackPort
                        + ". Is another program using that port?",
                    e));
            return result;
        }

        String authorizeUrl = buildAuthorizeUrl(state);
        try {
            Desktop.getDesktop()
                .browse(URI.create(authorizeUrl));
            Twitchy.LOG.info("Opened browser for Twitch authorization. Waiting for approval...");
        } catch (Exception e) {
            stopServerIfRunning();
            result.completeExceptionally(
                new IOException(
                    "Could not open your browser automatically. Open this URL manually to authorize:\n" + authorizeUrl,
                    e));
            return result;
        }

        // Timeout so we don't leave a local server open forever if the user never finishes.
        result.orTimeout(3, TimeUnit.MINUTES)
            .whenComplete((creds, err) -> {
                stopServerIfRunning();
                if (err instanceof TimeoutException) {
                    Twitchy.LOG.warn("Twitch authorization timed out waiting for browser approval.");
                }
            });

        return result;
    }

    public static void cancel() {
        stopServerIfRunning();
    }

    private static synchronized void stopServerIfRunning() {
        if (activeServer != null) {
            activeServer.stop(0);
            activeServer = null;
        }
    }

    private static String buildAuthorizeUrl(String state) {
        StringBuilder scopeBuilder = new StringBuilder();
        for (String s : SCOPES) {
            if (scopeBuilder.length() > 0) scopeBuilder.append(' ');
            scopeBuilder.append(s);
        }
        String encodedScopes = urlEncode(scopeBuilder.toString());
        String encodedRedirect = urlEncode(redirectUri());
        return "https://id.twitch.tv/oauth2/authorize" + "?client_id="
            + urlEncode(Config.clientId)
            + "&redirect_uri="
            + encodedRedirect
            + "&response_type=token"
            + "&scope="
            + encodedScopes
            + "&state="
            + state
            + "&force_verify=false";
    }

    private static void handleLandingPage(HttpExchange exchange) throws IOException {
        // This page runs client-side JS to grab window.location.hash (which contains the token)
        // and forwards it to /capture as a normal query string, since fragments never reach a server.
        String html = "<!doctype html><html><head><meta charset='utf-8'><title>Twitchy</title></head>"
            + "<body style='font-family:sans-serif;background:#18181b;color:#efeff1;text-align:center;padding-top:10%'>"
            + "<h2>Finishing sign-in with Twitch...</h2>"
            + "<p>You can close this window once it confirms success.</p>"
            + "<script>"
            + "var h = window.location.hash.substring(1);"
            + "fetch('/twitchy-callback/capture?' + h).then(function(){"
            + "  document.body.innerHTML = '<h2>Success! You can close this window and return to Minecraft.</h2>';"
            + "}).catch(function(){"
            + "  document.body.innerHTML = '<h2>Something went wrong. Return to Minecraft and check the log.</h2>';"
            + "});"
            + "</script>"
            + "</body></html>";
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders()
            .add("Content-Type", "text/html; charset=utf-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void handleCapture(HttpExchange exchange, String expectedState,
        CompletableFuture<TwitchCredentials> result) throws IOException {
        Map<String, String> params = parseQuery(
            exchange.getRequestURI()
                .getRawQuery());

        byte[] bytes = "ok".getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }

        String error = params.get("error");
        if (error != null) {
            result.completeExceptionally(
                new IOException("Twitch authorization denied: " + params.get("error_description")));
            return;
        }

        String token = params.get("access_token");
        String state = params.get("state");
        String scopeParam = params.getOrDefault("scope", "");

        if (token == null || token.isBlank()) {
            result.completeExceptionally(new IOException("Twitch did not return an access token."));
            return;
        }
        if (state == null || !state.equals(expectedState)) {
            result.completeExceptionally(new IOException("OAuth state mismatch - possible CSRF attempt, aborting."));
            return;
        }

        TwitchCredentials creds = new TwitchCredentials();
        creds.accessToken = token;
        creds.scopes = scopeParam.isBlank() ? new String[0] : scopeParam.split(" ");

        // Resolve who this token belongs to, then persist.
        TwitchApiClient.getSelfUser(creds.accessToken)
            .thenAccept(user -> {
                creds.userId = user.id;
                creds.userLogin = user.login;
                creds.save();
                result.complete(creds);
            })
            .exceptionally(ex -> {
                result.completeExceptionally(ex);
                return null;
            });
    }

    private static Map<String, String> parseQuery(String rawQuery) {
        Map<String, String> map = new LinkedHashMap<>();
        if (rawQuery == null || rawQuery.isEmpty()) return map;
        for (String pair : rawQuery.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;
            String key = urlDecode(pair.substring(0, idx));
            String value = urlDecode(pair.substring(idx + 1));
            map.put(key, value);
        }
        return map;
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static String urlDecode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }
}
