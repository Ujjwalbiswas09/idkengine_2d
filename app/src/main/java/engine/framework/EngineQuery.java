package engine.framework;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EngineQuery {
    String name();
    String value() default "";
}

/*
name = file
value = texture|mesh|audio|all
name = files
value = ".jpg",".mp3",".wav"
 */
