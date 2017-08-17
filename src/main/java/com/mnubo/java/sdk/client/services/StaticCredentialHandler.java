package com.mnubo.java.sdk.client.services;

class StaticCredentialHandler implements CredentialHandler {

    private final String autorizationToken;

    StaticCredentialHandler(String token) {
        this.autorizationToken = token;
    }

    public String getAutorizationToken() {
        return "Bearer " + autorizationToken;
    }
}
