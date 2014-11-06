package common.exceptions;

/**
 * The EventType enumeration lists the various Event type categories
 */
public enum EventType
{
    InternalError(-2, "Internal Error"),
    InvalidInput(-1, "Invalid Input"),
    Success(0, "Success"),
    BusinessLogicFailure(1, "Business Logic Failure"),
    Warning(2, "Warning");

    final private int m_code;

    final private String m_description;

    /**
     * Constructs a new instance initialized with an enum code.
     * 
     * @param code The int code that uniquely identifies this enum value.
     * @param description The String description of code.
     */
    private EventType(final int code, final String description)
    {
        m_code = code;
        m_description = description;
    }

    /**
     * @return The int code of the enum value.
     */
    public int getCode()
    {
        return m_code;
    }

    public boolean isOk()
    {
        return m_code == 0;
    }

    public String getDescription()
    {
        return m_description;
    }
}
