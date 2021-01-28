package com.easemob.im.server.api.token.allocate;

import com.easemob.im.server.EMProperties;
import com.easemob.im.server.api.MockingContext;
import com.easemob.im.server.api.MockingHttpServer;
import com.easemob.im.server.model.EMToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultTokenProviderTest {
    private ObjectMapper objectMapper = new ObjectMapper();

    private MockingHttpServer server = MockingHttpServer.builder()
        .addHandler("POST /easemob/demo/tokens", this::handlePostToken)
        .build();

    private EMProperties properties = EMProperties.builder()
        .baseUri(server.uri())
        .appkey("easemob#demo")
        .clientId("id")
        .clientSecret("secret")
        .build();

    private MockingContext context = new MockingContext(properties);

    @Test
    public void testFetchAppToken() {
        DefaultTokenProvider tokenProvider = new DefaultTokenProvider(this.context.getProperties(), this.context.getHttpClient(), this.context.getCodec(), this.context.getErrorMapper());

        EMToken appToken = tokenProvider.fetchAppToken().block(Duration.ofSeconds(3));
        assertEquals("access_token", appToken.getValue());
        assertTrue(appToken.isValid());
    }

    private JsonNode handlePostToken(JsonNode jsonNode) {
        return this.objectMapper.createObjectNode()
            .put("access_token", "access_token")
            .put("expires_in", 3600);
    }

    @Test
    public void testFetchUserToken() {
        DefaultTokenProvider tokenProvider = new DefaultTokenProvider(this.context.getProperties(), this.context.getHttpClient(), this.context.getCodec(), this.context.getErrorMapper());

        EMToken appToken = tokenProvider.fetchUserToken("username", "password").block(Duration.ofSeconds(3));
        assertEquals("access_token", appToken.getValue());
        assertTrue(appToken.isValid());
    }

}