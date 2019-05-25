package entities;

import uk.co.caeldev.cassitory.CassitoryEntity;
import uk.co.caeldev.cassitory.pojos.User;

@CassitoryEntity(target = {User.class, User.class})
public class FooInvalid6 {

}
