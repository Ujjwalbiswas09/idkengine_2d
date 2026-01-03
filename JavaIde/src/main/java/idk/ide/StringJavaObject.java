package idk.ide;

import java.net.URI;
import javax.tools.SimpleJavaFileObject;

public class StringJavaObject extends SimpleJavaFileObject {
    public CharSequence str;
    public StringJavaObject(String name, CharSequence code) {
        super(URI.create("string:///" + name), Kind.SOURCE);
        str = code;
    }

    public void update(String code) {
        str = code;
    }

    @Override
    public CharSequence getCharContent(boolean arg0) {
        return str;
    }
}