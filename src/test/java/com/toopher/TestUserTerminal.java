package com.toopher;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import static org.junit.Assert.*;


public class TestUserTerminal {
    private static final String DEFAULT_BASE_URL = "https://api.toopher.test/v1/";

    private static String id;
    private static String name;
    private static String requesterSpecifiedId;
    private static JSONObject terminalJson = new JSONObject();
    private static JSONObject user = new JSONObject();
    private static String userName;

    @BeforeClass
    public static void setUp() {
        id = UUID.randomUUID().toString();
        name = "terminalName";
        requesterSpecifiedId = "terminalNameExtra";

        user.put("id", UUID.randomUUID().toString());
        user.put("name", "userName");
        userName = user.getString("name");

        terminalJson.put("id", id);
        terminalJson.put("name", name);
        terminalJson.put("name_extra", requesterSpecifiedId);
        terminalJson.put("user", user);
    }

    @Test
    public void testRefreshFromServer() throws InterruptedException, RequestError {
        JSONObject newJsonResponse = new JSONObject();
        newJsonResponse.put("name", "terminalNameChanged");
        newJsonResponse.put("name_extra", "terminalNameExtraChanged");
        JSONObject newUser = new JSONObject();
        newUser.put("name", "userNameChanged");
        newJsonResponse.put("user", newUser);

        HttpClientMock httpClient = new HttpClientMock(200, newJsonResponse.toString());
        ToopherApi toopherApi = new ToopherApi("key", "secret",
                URI.create(DEFAULT_BASE_URL), httpClient);
        UserTerminal terminal = new UserTerminal(terminalJson, toopherApi);

        assertEquals(id, terminal.id, id);
        assertEquals(name, terminal.name, name);
        assertEquals(requesterSpecifiedId, terminal.requesterSpecifiedId);
        assertEquals(userName, terminal.user.name, userName);

        terminal.refreshFromServer();

        assertEquals("GET", httpClient.getLastCalledMethod());
        assertEquals(String.format("user_terminals/%s", id), httpClient.getLastCalledEndpoint());
        assertEquals(id, terminal.id);
        assertEquals("terminalNameChanged", terminal.name);
        assertEquals("terminalNameExtraChanged", terminal.requesterSpecifiedId);
        assertEquals("userNameChanged", terminal.user.name);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        JSONObject updatedJson = new JSONObject();
        updatedJson.put("name", "terminalNameChanged");
        updatedJson.put("name_extra", "terminalNameExtraChanged");
        JSONObject updatedUser = new JSONObject();
        updatedUser.put("name", "userNameChanged");
        updatedJson.put("user", updatedUser);

        HttpClientMock httpClient = new HttpClientMock(200, "{}");
        ToopherApi toopherApi = new ToopherApi("key", "secret",
                URI.create(DEFAULT_BASE_URL), httpClient);
        UserTerminal terminal = new UserTerminal(terminalJson, toopherApi);
        terminal.update(updatedJson);

        assertEquals(id, terminal.id);
        assertEquals("terminalNameChanged", terminal.name);
        assertEquals("terminalNameExtraChanged", terminal.requesterSpecifiedId);
        assertEquals("userNameChanged", terminal.user.name);
    }
}
