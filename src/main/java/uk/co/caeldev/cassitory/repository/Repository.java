package uk.co.caeldev.cassitory.repository;

public interface Repository<T> {

    void save(T dtoEntity);

    void delete(T dtoEntity);

}
