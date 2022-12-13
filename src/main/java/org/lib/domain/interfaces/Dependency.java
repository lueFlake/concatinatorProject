package org.lib.domain.interfaces;

import java.util.List;

public interface Dependency<T> extends Numerable {
    public List<Dependency<T>> getDependent();
    public T getValue();
}
