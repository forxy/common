package common.utils

import java.util.regex.Pattern

abstract class ValidationUtils {

    protected static final Pattern GUID_PATTERN = Pattern
            .compile('^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}')

    static boolean isValidGUID(String guid) {
        return guid != null && guid.length() > 0 && GUID_PATTERN.matcher(guid).matches()
    }
}
