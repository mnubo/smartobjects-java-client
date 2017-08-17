package com.mnubo.java.sdk.client.config;

public class ExponentialBackoffConfig {
    public static final long DEFAULT_INITIAL_DELAY = 500;
    public static final int DEFAULT_NUMBER_OF_ATTEMPTS = 5;
    public static final OnRetryCallback DEFAULT_ON_RETRY = new OnRetryCallback() {
        @Override
        public void onRetry(int attempt) { }
    };

    final int numberOfAttempts;
    final double multiplier = 2.0d;
    final long initialDelay;
    final OnRetryCallback onRetry;

    public ExponentialBackoffConfig() {
        this(DEFAULT_NUMBER_OF_ATTEMPTS, DEFAULT_INITIAL_DELAY, DEFAULT_ON_RETRY);
    }

    public ExponentialBackoffConfig(int numberOfAttempts) {
        this(numberOfAttempts, DEFAULT_INITIAL_DELAY, DEFAULT_ON_RETRY);
    }

    public ExponentialBackoffConfig(int numberOfAttempts, long initialDelay) {
        this(numberOfAttempts, initialDelay, DEFAULT_ON_RETRY);
    }

    public ExponentialBackoffConfig(int numberOfAttempts, long initialDelay, OnRetryCallback onRetry) {
        this.numberOfAttempts = numberOfAttempts;
        this.initialDelay = initialDelay;
        this.onRetry = onRetry;
    }

    public static ExponentialBackoffConfig DEFAULT = new ExponentialBackoffConfig();

    public int getNumberOfAttempts() {
        return numberOfAttempts;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public long getInitialDelay() {
        return initialDelay;
    }

    public OnRetryCallback getOnRetry() {
        return onRetry;
    }
}
