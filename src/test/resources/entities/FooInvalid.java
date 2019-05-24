package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;

public class FooInvalid {

    @CassitoryEntity(target = {})
    private String value;

}
