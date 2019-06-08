package lexer;

import enumerations.CompilerEnum;

import java.util.Objects;

public class Variable {
    private String name;
    private CompilerEnum valueType;
    private Object value;
    private CompilerEnum variableType;

    public Variable(String name, CompilerEnum valueType, Object value, CompilerEnum variableType) {
        this.name = name;
        this.valueType = valueType;
        this.value = value;
        this.variableType = variableType;
    }

    public String getName() {
        return name;
    }

    public CompilerEnum getValueType() {
        return valueType;
    }

    public void setValueType(CompilerEnum valueType) {
        this.valueType = valueType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public CompilerEnum getVariableType() {
        return variableType;
    }

    public void setVariableType(CompilerEnum variableType) {
        this.variableType = variableType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Variable)) return false;
        Variable o = (Variable) obj;
        return name.equals(o.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
