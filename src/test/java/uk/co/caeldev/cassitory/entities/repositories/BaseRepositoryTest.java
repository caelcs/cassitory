package uk.co.caeldev.cassitory.entities.repositories;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.caeldev.cassitory.pojos.User;
import uk.co.caeldev.cassitory.pojos.UserByName;
import uk.co.caeldev.cassitory.pojos.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.org.fyodor.generators.RDG.integer;
import static uk.org.fyodor.generators.RDG.string;

@ExtendWith(MockitoExtension.class)
class BaseRepositoryTest {

    @Mock
    private MappingManager mappingManager;

    @Mock
    private Mapper<User> mapper;

    @Mock
    private Mapper<UserByName> mapperByName;

    @Mock
    private Session session;

    @Mock
    private Statement deleteQuery;

    @Mock
    private Statement deleteQueryByName;

    @Captor
    private ArgumentCaptor<BatchStatement> batchStatementArgumentCaptor;

    @Test
    @DisplayName("Should save a Dto to User and UserByName table")
    public void shouldSaveToUserAndUserByNameTables() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setName(string().next());
        userDto.setAge(integer().next());

        //And
        when(mappingManager.mapper(User.class)).thenReturn(mapper);
        when(mappingManager.mapper(UserByName.class)).thenReturn(mapperByName);

        //And
        when(mapper.saveQuery(any(User.class), any(Mapper.Option.class))).thenReturn(deleteQuery);
        when(mapperByName.saveQuery(any(UserByName.class), any(Mapper.Option.class))).thenReturn(deleteQueryByName);

        //And
        when(mappingManager.getSession()).thenReturn(session);

        //When
        UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
        userDtoRepository.save(userDto);

        //Then
        verify(session).execute(batchStatementArgumentCaptor.capture());
        BatchStatement batchStatement = batchStatementArgumentCaptor.getValue();
        assertThat(batchStatement.getStatements()).containsExactlyInAnyOrder(deleteQuery, deleteQueryByName);

        assertThat(userDtoRepository.mappers).containsOnlyKeys(User.class, UserByName.class);
        assertThat(userDtoRepository.mappers).containsEntry(User.class, mapper);
        assertThat(userDtoRepository.mappers).containsEntry(UserByName.class, mapperByName);
        assertThat(userDtoRepository.getTargetClasses()).containsExactly(User.class, UserByName.class);
    }

    @Test
    @DisplayName("Should delete a Dto from User and UserByName table")
    public void shouldDeleteFromUserAndUserByNameTables() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setName(string().next());
        userDto.setAge(integer().next());

        //And
        when(mappingManager.mapper(User.class)).thenReturn(mapper);
        when(mappingManager.mapper(UserByName.class)).thenReturn(mapperByName);

        //And
        when(mapper.deleteQuery(any(User.class), any(Mapper.Option.class))).thenReturn(deleteQuery);
        when(mapperByName.deleteQuery(any(UserByName.class), any(Mapper.Option.class))).thenReturn(deleteQueryByName);

        //And
        when(mappingManager.getSession()).thenReturn(session);

        //When
        UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
        userDtoRepository.delete(userDto);

        //Then
        verify(session).execute(batchStatementArgumentCaptor.capture());
        BatchStatement batchStatement = batchStatementArgumentCaptor.getValue();
        assertThat(batchStatement.getStatements()).containsExactlyInAnyOrder(deleteQuery, deleteQueryByName);

        assertThat(userDtoRepository.mappers).containsOnlyKeys(User.class, UserByName.class);
        assertThat(userDtoRepository.mappers).containsEntry(User.class, mapper);
        assertThat(userDtoRepository.mappers).containsEntry(UserByName.class, mapperByName);
        assertThat(userDtoRepository.getTargetClasses()).containsExactly(User.class, UserByName.class);
    }

    @Test
    @DisplayName("Should save async a Dto to User and UserByName table")
    public void shouldSaveAsyncToUserAndUserByNameTables() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setName(string().next());
        userDto.setAge(integer().next());

        //And
        when(mappingManager.mapper(User.class)).thenReturn(mapper);
        when(mappingManager.mapper(UserByName.class)).thenReturn(mapperByName);

        //And
        when(mapper.saveQuery(any(User.class), any(Mapper.Option.class))).thenReturn(deleteQuery);
        when(mapperByName.saveQuery(any(UserByName.class), any(Mapper.Option.class))).thenReturn(deleteQueryByName);

        //And
        when(mappingManager.getSession()).thenReturn(session);

        //When
        UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
        userDtoRepository.saveAsync(userDto);

        //Then
        verify(session).executeAsync(batchStatementArgumentCaptor.capture());
        BatchStatement batchStatement = batchStatementArgumentCaptor.getValue();
        assertThat(batchStatement.getStatements()).containsExactlyInAnyOrder(deleteQuery, deleteQueryByName);

        assertThat(userDtoRepository.mappers).containsOnlyKeys(User.class, UserByName.class);
        assertThat(userDtoRepository.mappers).containsEntry(User.class, mapper);
        assertThat(userDtoRepository.mappers).containsEntry(UserByName.class, mapperByName);
        assertThat(userDtoRepository.getTargetClasses()).containsExactly(User.class, UserByName.class);
    }

    @Test
    @DisplayName("Should delete async a Dto from User and UserByName table")
    public void shouldDeleteAsyncFromUserAndUserByNameTables() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setName(string().next());
        userDto.setAge(integer().next());

        //And
        when(mappingManager.mapper(User.class)).thenReturn(mapper);
        when(mappingManager.mapper(UserByName.class)).thenReturn(mapperByName);

        //And
        when(mapper.deleteQuery(any(User.class), any(Mapper.Option.class))).thenReturn(deleteQuery);
        when(mapperByName.deleteQuery(any(UserByName.class), any(Mapper.Option.class))).thenReturn(deleteQueryByName);

        //And
        when(mappingManager.getSession()).thenReturn(session);

        //When
        UserDtoRepository userDtoRepository = new UserDtoRepository(mappingManager);
        userDtoRepository.deleteAsync(userDto);

        //Then
        verify(session).executeAsync(batchStatementArgumentCaptor.capture());
        BatchStatement batchStatement = batchStatementArgumentCaptor.getValue();
        assertThat(batchStatement.getStatements()).containsExactlyInAnyOrder(deleteQuery, deleteQueryByName);

        assertThat(userDtoRepository.mappers).containsOnlyKeys(User.class, UserByName.class);
        assertThat(userDtoRepository.mappers).containsEntry(User.class, mapper);
        assertThat(userDtoRepository.mappers).containsEntry(UserByName.class, mapperByName);
        assertThat(userDtoRepository.getTargetClasses()).containsExactly(User.class, UserByName.class);
    }

}