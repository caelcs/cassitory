package entities.repositories;

import static com.google.common.collect.Lists.newArrayList;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import entities.UserMoreDtoMultipleMap4;
import java.lang.Class;
import java.lang.Override;
import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;
import uk.co.caeldev.cassitory.repository.BaseRepository;

public class UserMoreDtoMultipleMap4BaseRepository extends BaseRepository<UserMoreDtoMultipleMap4> {
    public UserMoreDtoMultipleMap4BaseRepository(MappingManager mappingManager) {
        super(mappingManager);
    }

    @Override
    protected List<Supplier> getCreators(UserMoreDtoMultipleMap4 userMoreDtoMultipleMap4) {
        return new UserMoreDtoMultipleMap4Creators(userMoreDtoMultipleMap4).creators;
    }

    @Override
    protected List<Class> getTargetClasses() {
        return newArrayList(User.class, UserByName.class);
    }

    @Override
    protected Mapper.Option[] getWriteOptions() {
        List<Mapper.Option> options = newArrayList(Mapper.Option.consistencyLevel(ConsistencyLevel.ALL), Mapper.Option.tracing(true));
        return options.stream().toArray(Mapper.Option[]::new);
    }
}