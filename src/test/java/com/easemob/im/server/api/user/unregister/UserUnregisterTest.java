package com.easemob.im.server.api.user.unregister;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.api.MockingContext;
import com.easemob.im.server.api.MockingHttpServer;
import com.easemob.im.server.model.EMUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserUnregisterTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    private MockingHttpServer server = MockingHttpServer.builder()
        .addHandler("DELETE /easemob/demo/users/username", this::handleUserUnregisterSingle)
        .addHandler("DELETE /easemob/demo/users?limit=100", req -> handleUserUnregisterAll(req, 100, "cursor"))
        .addHandler("DELETE /easemob/demo/users?limit=100&cursor=cursor", req -> handleUserUnregisterAll(req, 100, null))
        .build();



    private EMProperties properties = EMProperties.builder()
        .baseUri(this.server.uri())
        .appkey("easemob#demo")
        .clientId("clientId")
        .clientSecret("clientSecret")
        .build();

    private MockingContext context = new MockingContext(properties);

    @Test
    public void testUserUnregisterSingle() {
        UserUnregister unregister = new UserUnregister(this.context);
        EMUser user = unregister.single("username").block(Duration.ofSeconds(3));
        assertEquals("username", user.getUsername());
    }

    @Test
    public void testUserUnregisterAll() {
        UserUnregister unregister = new UserUnregister(this.context);
        List<EMUser> users = unregister.all(100).collectList().block(Duration.ofSeconds(3));
        assertEquals(200, users.size());
        for (int i = 0; i < 200; i++) {
            assertEquals("username", users.get(i).getUsername());
        }
    }

    private JsonNode handleUserUnregisterSingle(JsonNode jsonNode) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        users.add(user);

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);

        return rsp;
    }

    private JsonNode handleUserUnregisterAll(JsonNode jsonNode, int limit, String cursor) {
        ObjectNode user = this.objectMapper.createObjectNode();
        user.put("username", "username");

        ArrayNode users = this.objectMapper.createArrayNode();
        for (int i = 0; i < limit; i++) {
            users.add(user);
        }

        ObjectNode rsp = this.objectMapper.createObjectNode();
        rsp.set("entities", users);
        if (cursor != null) {
            rsp.put("cursor", cursor);
        }

        return rsp;
    }
}