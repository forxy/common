package common.utils;

import java.util.regex.Pattern;

/**
 * Created by Tiger on 18.08.14.
 */
public abstract class ValidationUtils {

    protected static final Pattern GUID_PATTERN = Pattern
            .compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");

    public static boolean isValidGUID(String guid) {
        return guid != null && guid.length() > 0 && GUID_PATTERN.matcher(guid).matches();
    }
}
