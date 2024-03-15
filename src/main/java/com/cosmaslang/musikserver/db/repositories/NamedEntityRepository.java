package com.cosmaslang.musikserver.db.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface NamedEntityRepository<T> extends CrudRepository<T, Long> {
    T findByName(String name);
}
