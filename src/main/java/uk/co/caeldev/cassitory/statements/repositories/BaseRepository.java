package uk.co.caeldev.cassitory.statements.repositories;

import com.datastax.driver.core.*;
import uk.co.caeldev.cassitory.base.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseRepository<T> implements Repository<T> {

    private final Session session;
    private final Map<String, PreparedStatement> saveStatements;
    private final Map<String, PreparedStatement> deleteStatements;

    public BaseRepository(Session session) {
        this.session = session;
        this.saveStatements = initSaveStatements();
        this.deleteStatements = initDeleteStatements();
    }

    @Override
    public ResultSet save(T dtoEntity) {
        BatchStatement batchStatement = createSaveBatchStatements(getSaveParameters(dtoEntity),
                saveStatements);

        return session.execute(batchStatement);
    }

    @Override
    public ResultSet delete(T dtoEntity) {
        BatchStatement batchStatement = createSaveBatchStatements(getDeleteParameters(dtoEntity),
                deleteStatements);

        return session.execute(batchStatement);
    }

    @Override
    public ResultSetFuture saveAsync(T dtoEntity) {
        BatchStatement batchStatement = createSaveBatchStatements(getSaveParameters(dtoEntity),
                saveStatements);

        return session.executeAsync(batchStatement);
    }

    @Override
    public ResultSetFuture deleteAsync(T dtoEntity) {
        BatchStatement batchStatement = createSaveBatchStatements(getDeleteParameters(dtoEntity),
                deleteStatements);

        return session.executeAsync(batchStatement);
    }

    protected abstract Map<String, String> getSaveQueries();

    protected abstract Map<String, String> getDeleteQueries();

    protected abstract Map<String, Object[]> getSaveParameters(T entity);

    protected abstract Map<String, Object[]> getDeleteParameters(T entity);

    private Map<String, PreparedStatement> initSaveStatements() {
        return getSaveQueries().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        (it) -> session.prepare(it.getValue())));
    }

    private Map<String, PreparedStatement> initDeleteStatements() {
        return getDeleteQueries().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        (it) -> session.prepare(it.getValue())));
    }

    private BatchStatement createSaveBatchStatements(Map<String, Object[]> parameters,
                                                     Map<String, PreparedStatement> preparedStatements) {
        BatchStatement batchStatement = new BatchStatement();

        List<BoundStatement> boundStatements = preparedStatements.entrySet().stream()
                .map(it -> it.getValue().bind(parameters.get(it.getKey())))
                .collect(Collectors.toList());

        batchStatement.addAll(boundStatements);
        return batchStatement;
    }

}
