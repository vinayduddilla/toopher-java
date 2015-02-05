package com.toopher;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides information about the status of a user terminal
 *
 */
public class UserTerminal extends ApiResponseObject {
    public ToopherAPI api;
    public String id;
    public String name;
    public String requesterSpecifiedId;
    public User user;

    public UserTerminal (JSONObject json, ToopherAPI toopherAPI) throws JSONException {
        super(json);

        this.api = toopherAPI;
        this.id = json.getString("id");
        this.name = json.getString("name");
        if (json.has("name_extra")) {
            this.requesterSpecifiedId = json.getString("name_extra");
        }
        this.user = new User(json.getJSONObject("user"), toopherAPI);
    }

    @Override
    public String toString() {
        return String.format("[UserTerminal: id=%s; name=%s; requesterSpecifiedId=%s; userName=%s; userId=%s]",
                id, name, requesterSpecifiedId, user.name, user.id);
    }

    public void refreshFromServer() throws RequestError {
        String endpoint = String.format("user_terminals/%s", id);
        JSONObject result = api.advanced.raw.get(endpoint);
        update(result);
    }

    public void update(JSONObject jsonResponse) {
        this.name = jsonResponse.getString("name");
        if (jsonResponse.has("name_extra")) {
            this.requesterSpecifiedId = jsonResponse.getString("name_extra");
        }
        this.user.update(jsonResponse.getJSONObject("user"));
    }
}
