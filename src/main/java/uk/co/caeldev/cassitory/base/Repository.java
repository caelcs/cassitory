package uk.co.caeldev.cassitory.base;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;

public interface Repository<T> {

    ResultSet save(T dtoEntity);

    ResultSet delete(T dtoEntity);

    ResultSetFuture saveAsync(T dtoEntity);

    ResultSetFuture deleteAsync(T dtoEntity);

}
