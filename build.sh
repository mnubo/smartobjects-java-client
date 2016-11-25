#!/bin/bash

if [ "$TRAVIS_PULL_REQUEST" = "false" ]; then
    openssl aes-256-cbc -K $encrypted_176ea4399631_key -iv $encrypted_176ea4399631_iv \
      -in credentials.properties.enc -out src/test/resources/credentials.properties -d
    mvn clean test
else
    # These test must be run on mnubo/smartobjects-java-client because they require the encrypted key
    mvn clean -Dtest.excludes="**/SdkClientIntegrationTest.java" test
fi