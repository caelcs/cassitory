package entities;

import uk.co.caeldev.cassitory.entities.CassitoryEntity;

public class FooInvalid {

    @CassitoryEntity(target = {})
    private String value;

}
