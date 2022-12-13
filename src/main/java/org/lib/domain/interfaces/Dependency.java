package org.lib.domain.interfaces;

import java.util.List;

public interface Dependency<T> extends Numerable {
    public List<Dependency<T>> getDependencies();
    public T getValue();
}
