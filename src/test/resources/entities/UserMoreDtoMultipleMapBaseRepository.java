package entities;

import static com.google.common.collect.Lists.newArrayList;

import com.datastax.driver.mapping.MappingManager;
import java.lang.Class;
import java.lang.Override;
import java.util.List;
import java.util.function.Supplier;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;
import uk.co.caeldev.cassitory.repository.BaseRepository;

public class UserMoreDtoMultipleMapBaseRepository extends BaseRepository<UserMoreDtoMultipleMap> {
    public UserMoreDtoMultipleMapBaseRepository(MappingManager mappingManager) {
        super(mappingManager);
    }

    @Override
    protected List<Supplier> getCreators(UserMoreDtoMultipleMap userMoreDtoMultipleMap) {
        return new UserMoreDtoMultipleMapCreators(userMoreDtoMultipleMap).creators;
    }

    @Override
    protected List<Class> getTargetClasses() {
        return newArrayList(User.class, UserByName.class);
    }
}