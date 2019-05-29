package uk.co.caeldev.cassitory.generators;

import com.squareup.javapoet.JavaFile;

import javax.lang.model.element.TypeElement;
import java.util.List;

public interface Generator {

    List<JavaFile> generate(List<TypeElement> elementsAnnotated);

}
