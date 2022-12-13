package org.lib.domain.models;

import org.lib.domain.interfaces.Dependency;

import java.util.List;

public class FoundCycleException extends RuntimeException {
    public FoundCycleException(String message) {
        super(message);
    }
}
