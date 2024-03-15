package com.cosmaslang.musikserver.db.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NamedRepository<T> extends CrudRepository<T, Long> {
    T findByName(String name);
}
