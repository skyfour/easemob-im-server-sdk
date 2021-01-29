package com.easemob.im.server.api.block.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UnblockUserRequest {
    @JsonProperty("usernames")
    private List<String> usernames;

    @JsonCreator
    public UnblockUserRequest(@JsonProperty("usernames") List<String> usernames) {
        this.usernames = usernames;
    }

    public List<String> getUsernames() {
        return usernames;
    }

}
