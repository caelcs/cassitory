package uk.co.caeldev.cassitory.repository;

import com.datastax.driver.mapping.MappingManager;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;
import uk.co.caeldev.cassitory.pojos.UserDto;
import uk.co.caeldev.cassitory.pojos.TestUserDtoCreators;

import java.util.List;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newArrayList;

public class UserDtoRepository extends BaseRepository<UserDto> {

    public UserDtoRepository(MappingManager manager) {
        super(manager);
    }

    @Override
    protected List<Supplier> getCreators(UserDto dtoEntity) {
        return new TestUserDtoCreators(dtoEntity).creators;
    }

    @Override
    protected List<Class> getTargetClasses() {
        return newArrayList(User.class, UserByName.class);
    }
}