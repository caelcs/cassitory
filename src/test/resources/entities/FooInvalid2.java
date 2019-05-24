package entities;

import uk.co.caeldev.cassitory.Mapping;

public class FooInvalid2 {

    private String value;

    @Mapping(target = {}, field = "")
    public String getValue() {
        return value;
    }
}
