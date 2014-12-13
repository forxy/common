package common.rest.client.transport.support

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.util.ISO8601DateFormat
import com.fasterxml.jackson.datatype.joda.JodaModule

import java.text.DateFormat

final class ObjectMapperProvider {

    private static final Config DEFAULT_CONFIG = Config.newInstance()

    private static class StringTrimmingDeserializer extends JsonDeserializer<String> {
        @Override
        String deserialize(final JsonParser jsonParser, final DeserializationContext deserializationContext)
                throws IOException {
            jsonParser.text ?: jsonParser.text.trim()
        }
    }

    /**
     * Builds the mapper with the default configuration. See {@link ObjectMapperProvider.Config} for the details about
     * default values being used.
     *
     * @return Object mapper buidl from the default config
     */
    static ObjectMapper getDefaultMapper() {
        return getMapper(DEFAULT_CONFIG)
    }

    static ObjectMapper getMapper(final Config config) {
        final ObjectMapper mapper = new ObjectMapper()

        if (config.propertyNamingStrategy) {
            mapper.propertyNamingStrategy = config.propertyNamingStrategy
        }

        config.modules.each {
            mapper.registerModule(it)
        }
        if (config.deserializationTrimStrings) {
            mapper.registerModule(
                    new SimpleModule(
                            'StringDeserializerModule',
                            new Version(1, 0, 0, null)
                    ).addDeserializer(String.class, new StringTrimmingDeserializer())
            )
        }

        // Configure deserialization
        DeserializationConfig dConfig = mapper.deserializationConfig
        if (config.dateFormat) {
            dConfig = dConfig.withDateFormat(config.dateFormat)
        }
        mapper.deserializationConfig = dConfig
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                config.isFailOnUnknownProperties())
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                config.isFailOnUnknownProperties())

        // Configure serialization
        SerializationConfig sConfig = mapper.serializationConfig
        sConfig = sConfig.withSerializationInclusion(config.serializationInclusion)
        sConfig = cfgFeature(sConfig, SerializationFeature.INDENT_OUTPUT, false)
        sConfig = cfgFeature(sConfig, MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false)
        sConfig = cfgFeature(sConfig, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, config.writeDatesAsTimestamps)
        sConfig = cfgFeature(sConfig, SerializationFeature.WRITE_NULL_MAP_VALUES, config.writeNullMapValues)
        sConfig = cfgFeature(sConfig, SerializationFeature.WRITE_EMPTY_JSON_ARRAYS, config.writeEmptyArrays)
        if (config.dateFormat) {
            sConfig = sConfig.withDateFormat(config.dateFormat)
        }
        mapper.serializationConfig = sConfig

        return mapper
    }

    private static SerializationConfig cfgFeature(final SerializationConfig config, final MapperFeature feature,
                                                  final boolean featureEnabled) {
        return featureEnabled ? config.with(feature) : config.without(feature)
    }

    private static SerializationConfig cfgFeature(final SerializationConfig config, final SerializationFeature feature,
                                                  final boolean featureEnabled) {
        return featureEnabled ? config.with(feature) : config.without(feature)
    }

    /**
     * ObjectMapper configuration
     */
    static class Config {
        /**
         * Determines whether {@link java.util.Date} values (and Date-based things like
         * {@link java.util.Calendar}s) are to be serialized as numeric timestamps,
         * or as something else (usually textual representation).<p> Default: <b>false</b>
         */
        boolean writeDatesAsTimestamps = false
        /**
         * Determines whether encountering of unknown properties (ones that do not map to a
         * property, and there is no 'any setter' or handler that can handle it) should result
         * in a failure (by throwing a {@link com.fasterxml.jackson.databind.JsonMappingException}) or not.<p> Default: <b>true</b>
         */
        boolean failOnUnknownProperties = true
        /**
         * Holds the {@link java.text.DateFormat} instance to be used for JSON (de)serialization.<p>
         */
        DateFormat dateFormat = new ISO8601DateFormat()
        /**
         * Holds the list of mpodules that are to be registered with the ObjectMapper.<p>
         * Default: no modules will be registered with the {@link org.codehaus.jackson.map.ObjectMapper}.
         */
        List<Module> modules = [new JodaModule()]
        /**
         * Holds the custom property naming strategy to use for the newly created
         * {@link com.fasterxml.jackson.databind.ObjectMapper}.<p>Default: {@link com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}.
         */
        PropertyNamingStrategy propertyNamingStrategy = new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy()
        /**
         * Defines which properties of Java Beans are to be included in serialization.
         * <p>Default: <code>Inclusion.NON_NULL</code>
         */
        JsonInclude.Include serializationInclusion = JsonInclude.Include.NON_NULL
        /**
         * Defines whether string properties should be trimmed during deserialization.
         * <p>Default: <code>true</code>
         */
        boolean deserializationTrimStrings = true
        /**
         * Defines whether empty container properties (collections or arrays) should be
         * serialized as empty JSON arrays.
         * <p>Default: <code>false</code>
         */
        boolean writeEmptyArrays = false
        /**
         * Defines whether map entries with null values are to be serialized (true) or not (false).
         * <p>Default: <code>false</code>
         */
        boolean writeNullMapValues = false

        private Config() {
        }

        static Config newInstance() {
            return new Config()
        }
    }
}
