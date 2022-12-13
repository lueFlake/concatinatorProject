package org.lib.domain.exceptions;

import org.lib.domain.interfaces.Dependency;

import java.util.List;

public class FoundCycleException extends RuntimeException {
    public FoundCycleException(String message) {
        super(message);
    }
}
