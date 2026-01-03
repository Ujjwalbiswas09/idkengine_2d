package engine.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Flags {
    String[] flag();

}
//VISIBLE
//INVISIBLE
//STORE
//IGNORE
//NOTIFY