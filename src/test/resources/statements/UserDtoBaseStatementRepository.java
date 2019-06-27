package statements;

import com.datastax.driver.core.Session;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.Map;
import uk.co.caeldev.cassitory.statements.repositories.BaseRepository;

public class UserDtoBaseStatementRepository extends BaseRepository<UserDto> {
    public UserDtoBaseStatementRepository(Session session) {
        super(session);
    }

    @Override
    protected Map<String, String> getSaveQueries() {
        return UserDtoQueriesCreator.saveQueries;
    }

    @Override
    protected Map<String, String> getDeleteQueries() {
        return UserDtoQueriesCreator.deleteQueries;
    }

    @Override
    protected Map<String, Object[]> getSaveParameters(UserDto entity) {
        UserDtoParametersCreator userDtoParametersCreator = new UserDtoParametersCreator(entity);
        return userDtoParametersCreator.createSaveParameters();
    }

    @Override
    protected Map<String, Object[]> getDeleteParameters(UserDto entity) {
        UserDtoParametersCreator userDtoParametersCreator = new UserDtoParametersCreator(entity);
        return userDtoParametersCreator.createDeleteParameters();
    }
}