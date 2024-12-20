package com.library.administration.services;

public interface IService<T, ID> {
    T findById(ID id);

    Iterable<T> findAll();

    T save(T entity);

    void deleteById(ID id);
}
