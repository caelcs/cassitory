package uk.co.caeldev.cassitory.repository;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BaseRepository<T> implements Repository<T> {

    protected final MappingManager mappingManager;
    protected final Map<Class, Mapper> mappers;

    public BaseRepository(MappingManager mappingManager) {
        this.mappingManager = mappingManager;
        this.mappers = initMappers();
    }

    protected abstract List<Supplier> getCreators(T dtoEntity);

    protected abstract List<Class> getTargetClasses();

    private Map<Class, Mapper> initMappers() {
        return getTargetClasses().stream()
                .collect(Collectors.toMap(Function.identity(), (clazz) -> mappingManager.mapper(clazz)));
    }

    @Override
    public void save(T dtoEntity) {
        List<Supplier> creators = getCreators(dtoEntity);

        BatchStatement batchStatement = new BatchStatement();

        Set<Statement> statements = creators.stream().map(it -> {
            Object entity = it.get();
            Mapper mapper = mappers.get(entity.getClass());
            return mapper.saveQuery(entity);
        }).collect(Collectors.toSet());

        batchStatement.addAll(statements);

        mappingManager.getSession().execute(batchStatement);
    }
}
