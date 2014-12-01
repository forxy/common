package common.rest.client.transport.support

import org.codehaus.jackson.JsonParser
import org.codehaus.jackson.Version
import org.codehaus.jackson.map.*
import org.codehaus.jackson.map.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy
import org.codehaus.jackson.map.SerializationConfig.Feature
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion
import org.codehaus.jackson.map.module.SimpleModule
import org.codehaus.jackson.map.util.ISO8601DateFormat

import java.text.DateFormat

import static org.codehaus.jackson.map.DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY
import static org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES
import static org.codehaus.jackson.map.SerializationConfig.Feature.*

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
        mapper.setDeserializationConfig(dConfig)
        mapper.configure(FAIL_ON_UNKNOWN_PROPERTIES,
                config.isFailOnUnknownProperties())
        mapper.configure(ACCEPT_SINGLE_VALUE_AS_ARRAY,
                config.isFailOnUnknownProperties())

        // Configure serialization
        SerializationConfig sConfig = mapper.serializationConfig
        sConfig = sConfig.withSerializationInclusion(config.serializationInclusion)
        sConfig = cfgFeature(sConfig, INDENT_OUTPUT, false)
        sConfig = cfgFeature(sConfig, SORT_PROPERTIES_ALPHABETICALLY, false)
        sConfig = cfgFeature(sConfig, WRITE_DATES_AS_TIMESTAMPS, config.writeDatesAsTimestamps)
        sConfig = cfgFeature(sConfig, WRITE_NULL_MAP_VALUES, config.writeNullMapValues)
        sConfig = cfgFeature(sConfig, WRITE_EMPTY_JSON_ARRAYS, config.writeEmptyArrays)
        if (config.dateFormat) {
            sConfig = sConfig.withDateFormat(config.dateFormat)
        }
        mapper.serializationConfig = sConfig

        return mapper
    }

    private static SerializationConfig cfgFeature(final SerializationConfig config, final Feature feature,
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
         * in a failure (by throwing a {@link org.codehaus.jackson.map.JsonMappingException}) or not.<p> Default: <b>true</b>
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
        final List<Module> modules = new ArrayList<Module>()
        /**
         * Holds the custom property naming strategy to use for the newly created
         * {@link org.codehaus.jackson.map.ObjectMapper}.<p>Default: {@link org.codehaus.jackson.map.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}.
         */
        PropertyNamingStrategy propertyNamingStrategy = new LowerCaseWithUnderscoresStrategy()
        /**
         * Defines which properties of Java Beans are to be included in serialization.
         * <p>Default: <code>Inclusion.NON_NULL</code>
         */
        Inclusion serializationInclusion = Inclusion.NON_NULL
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

        Config writeDatesAsTimestamps(final boolean writeDatesAsTimestamps) {
            this.writeDatesAsTimestamps = writeDatesAsTimestamps
            return this
        }

        Config failOnUnknownProperties(final boolean failOnUnknownProperties) {
            this.failOnUnknownProperties = failOnUnknownProperties
            return this
        }

        Config withDateFormat(final DateFormat dateFormat) {
            this.dateFormat = dateFormat
            return this
        }

        Config withoutDateFormat() {
            return withDateFormat(null)
        }

        Config withModule(final Module module) {
            modules.add(module)
            return this
        }

        Config withModules(final Module... modules) {
            if (modules != null) {
                Collections.addAll(this.modules, modules)
            }
            return this
        }

        Config withPropertyNamingStrategy(final PropertyNamingStrategy propertyNamingStrategy) {
            this.propertyNamingStrategy = propertyNamingStrategy
            return this
        }

        Config withoutPropertyNamingStrategy() {
            return withPropertyNamingStrategy(null)
        }

        Config withSerializationInclusion(final Inclusion serializationInclusion) {
            this.serializationInclusion = serializationInclusion
            return this
        }

        Config withDeserializationTrimStrings(final boolean deserializationTrimStrings) {
            this.deserializationTrimStrings = deserializationTrimStrings
            return this
        }

        Config writeEmptyArrays(final boolean writeEmptyArrays) {
            this.writeEmptyArrays = writeEmptyArrays
            return this
        }

        Config writeNullMapValues(final boolean writeNullMapValues) {
            this.writeNullMapValues = writeNullMapValues
            return this
        }
    }
}
