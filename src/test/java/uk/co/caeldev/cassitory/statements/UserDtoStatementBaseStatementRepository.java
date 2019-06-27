package uk.co.caeldev.cassitory.statements;

import com.datastax.driver.core.Session;
import uk.co.caeldev.cassitory.pojos.UserDtoStatement;
import uk.co.caeldev.cassitory.statements.repositories.BaseRepository;

import java.util.Map;

public class UserDtoStatementBaseStatementRepository extends BaseRepository<UserDtoStatement> {

    public UserDtoStatementBaseStatementRepository(Session session) {
        super(session);
    }

    @Override
    protected Map<String, String> getSaveQueries() {
        return UserDtoStatementQueriesCreator.saveQueries;
    }

    @Override
    protected Map<String, String> getDeleteQueries() {
        return UserDtoStatementQueriesCreator.deleteQueries;
    }

    @Override
    protected Map<String, Object[]> getSaveParameters(UserDtoStatement entity) {
        UserDtoStatementParametersCreator userDtoStatementParametersCreator = new UserDtoStatementParametersCreator(entity);
        return userDtoStatementParametersCreator.createSaveParameters();
    }

    @Override
    protected Map<String, Object[]> getDeleteParameters(UserDtoStatement entity) {
        UserDtoStatementParametersCreator userDtoStatementParametersCreator = new UserDtoStatementParametersCreator(entity);
        return userDtoStatementParametersCreator.createDeleteParameters();
    }

}
