package common.exceptions;

/**
 * The EventType enumeration lists the various Event type categories
 */
public enum EventType {

    InternalError(-2, "Internal Error"),

    InvalidInput(-1, "Invalid Input"),

    Success(0, "Success"),

    BusinessLogicFailure(1, "Business Logic Failure"),

    Warning(2, "Warning");

    final private int code;

    final private String description;

    /**
     * Constructs a new instance initialized with an enum code.
     *
     * @param code        The int code that uniquely identifies this enum value.
     * @param description The String description of code.
     */
    private EventType(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * @return The int code of the enum value.
     */
    public int getCode() {
        return code;
    }

    public boolean isOk() {
        return code == 0;
    }

    public String getDescription() {
        return description;
    }
}
