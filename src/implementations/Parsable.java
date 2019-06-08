package implementations;

import enumerations.CompilerEnum;

import java.util.ArrayList;

public interface Parsable {
    Object doMethod(String methodName, ArrayList<Object> arguments, CompilerEnum type);
}
