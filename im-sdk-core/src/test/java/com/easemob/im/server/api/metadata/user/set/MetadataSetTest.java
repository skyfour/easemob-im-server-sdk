package com.easemob.im.server.api.metadata.user.set;

import com.easemob.im.server.api.AbstractApiTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetadataSetTest extends AbstractApiTest {
    MetadataSet metadataSet = new MetadataSet(this.context);

    public MetadataSetTest() {
        this.server.addHandler("PUT /easemob/demo/metadata/user/bob", this::handleMetadataSet);
    }

    @Test
    public void testMetadataSet() {
        Map<String, String> map = new HashMap<>();
        assertDoesNotThrow(() -> this.metadataSet.set("bob", map).block(Duration.ofSeconds(3)));
    }

    public JsonNode handleMetadataSet(JsonNode req) {
        ObjectNode data = this.objectMapper.createObjectNode();
        data.put("nickname", "昵称");
        data.put("avatar", "http://www.easemob.com/avatar.png");
        data.put("phone", "159");

        ObjectNode jsonRsp = this.objectMapper.createObjectNode();
        jsonRsp.set("data", data);

        return jsonRsp;
    }
}