package com.library.administration.services.implementation;

import com.library.administration.services.IService;
import org.springframework.data.jpa.repository.JpaRepository;

public abstract class ServiceImple<T, ID> implements IService<T, ID> {
    protected final JpaRepository<T, ID> repository;

    protected ServiceImple(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }
    @Override
    public Iterable<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }
}
