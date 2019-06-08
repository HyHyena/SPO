package enumerations;

public enum ServiceEnum implements CompilerEnum {
    LABEL(1),
    FUNC(1),
    START_VISIBILITY_AREA(1),
    END_VISIBILITY_AREA(1),
    ARGUMENTS(1),
    OBJECT(1),
    START_FUNC(1),
    END_FUNC(1),
    GOTO_LIE(1),
    GOTO(1);

    int priority;

    ServiceEnum() {
        priority = 0;
    }

    ServiceEnum(int priority) {
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
