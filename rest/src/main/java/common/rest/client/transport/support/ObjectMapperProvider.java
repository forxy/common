package common.rest.client.transport.support;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.util.ISO8601DateFormat;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ObjectMapperProvider {

    private static final Config DEFAULT_CONFIG = Config.newInstance();

    private static class StringTrimmingDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(final JsonParser jsonParser,
                                  final DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getText() != null) {
                return jsonParser.getText().trim();
            }
            return jsonParser.getText();
        }
    }

    /**
     * Builds the mapper with the default configuration. See {@link ObjectMapperProvider.Config} for the details about
     * default values being used.
     *
     * @return Object mapper buidl from the default config
     */
    public static ObjectMapper getDefaultMapper() {
        return getMapper(DEFAULT_CONFIG);
    }

    public static ObjectMapper getMapper(final Config config) {
        final ObjectMapper mapper = new ObjectMapper();

        if (config.getPropertyNamingStrategy() != null) {
            mapper.setPropertyNamingStrategy(config.getPropertyNamingStrategy());
        }

        for (final Module module : config.getModules()) {
            mapper.registerModule(module);
        }
        if (config.isDeserializationTrimStrings()) {
            final SimpleModule stringTrimmingModule =
                    new SimpleModule("StringDeserializerModule", new Version(1, 0, 0, null)).addDeserializer(
                            String.class, new StringTrimmingDeserializer());
            mapper.registerModule(stringTrimmingModule);
        }

        // Configure deserialization
        DeserializationConfig dConfig = mapper.getDeserializationConfig();
        if (config.getDateFormat() != null) {
            dConfig = dConfig.withDateFormat(config.getDateFormat());
        }
        mapper.setDeserializationConfig(dConfig);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
                config.isFailOnUnknownProperties());
        mapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                config.isFailOnUnknownProperties());

        // Configure serialization
        SerializationConfig sConfig = mapper.getSerializationConfig();
        sConfig = sConfig.withSerializationInclusion(config.getSerializationInclusion());
        sConfig = cfgFeature(sConfig, Feature.INDENT_OUTPUT, false);
        sConfig = cfgFeature(sConfig, Feature.SORT_PROPERTIES_ALPHABETICALLY, false);
        sConfig = cfgFeature(sConfig, Feature.WRITE_DATES_AS_TIMESTAMPS, config.isWriteDatesAsTimestamps());
        sConfig = cfgFeature(sConfig, Feature.WRITE_NULL_MAP_VALUES, config.isWriteNullMapValues());
        sConfig = cfgFeature(sConfig, Feature.WRITE_EMPTY_JSON_ARRAYS, config.isWriteEmptyArrays());
        if (config.getDateFormat() != null) {
            sConfig = sConfig.withDateFormat(config.getDateFormat());
        }
        mapper.setSerializationConfig(sConfig);

        return mapper;
    }

    private static SerializationConfig cfgFeature(final SerializationConfig config, final Feature feature,
                                                  final boolean featureEnabled) {
        return featureEnabled ? config.with(feature) : config.without(feature);
    }

    /**
     * ObjectMapper configuration
     */
    public static class Config {
        /**
         * Determines whether {@link java.util.Date} values (and Date-based things like
         * {@link java.util.Calendar}s) are to be serialized as numeric timestamps,
         * or as something else (usually textual representation).<p> Default: <b>false</b>
         */
        private boolean writeDatesAsTimestamps = false;
        /**
         * Determines whether encountering of unknown properties (ones that do not map to a
         * property, and there is no "any setter" or handler that can handle it) should result
         * in a failure (by throwing a {@link org.codehaus.jackson.map.JsonMappingException}) or not.<p> Default: <b>true</b>
         */
        private boolean failOnUnknownProperties = true;
        /**
         * Holds the {@link java.text.DateFormat} instance to be used for JSON (de)serialization.<p>
         */
        private DateFormat dateFormat = new ISO8601DateFormat();
        /**
         * Holds the list of mpodules that are to be registered with the ObjectMapper.<p>
         * Default: no modules will be registered with the {@link org.codehaus.jackson.map.ObjectMapper}.
         */
        private final List<Module> modules = new ArrayList<Module>();
        /**
         * Holds the custom property naming strategy to use for the newly created
         * {@link org.codehaus.jackson.map.ObjectMapper}.<p>Default: {@link org.codehaus.jackson.map.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy}.
         */
        private PropertyNamingStrategy propertyNamingStrategy = new LowerCaseWithUnderscoresStrategy();
        /**
         * Defines which properties of Java Beans are to be included in serialization.
         * <p>Default: <code>Inclusion.NON_NULL</code>
         */
        private Inclusion serializationInclusion = Inclusion.NON_NULL;
        /**
         * Defines whether string properties should be trimmed during deserialization.
         * <p>Default: <code>true</code>
         */
        private boolean deserializationTrimStrings = true;
        /**
         * Defines whether empty container properties (collections or arrays) should be
         * serialized as empty JSON arrays.
         * <p>Default: <code>false</code>
         */
        private boolean writeEmptyArrays = false;
        /**
         * Defines whether map entries with null values are to be serialized (true) or not (false).
         * <p>Default: <code>false</code>
         */
        private boolean writeNullMapValues = false;

        private Config() {
        }

        public static Config newInstance() {
            return new Config();
        }

        public boolean isWriteDatesAsTimestamps() {
            return writeDatesAsTimestamps;
        }

        public boolean isFailOnUnknownProperties() {
            return failOnUnknownProperties;
        }

        public DateFormat getDateFormat() {
            return dateFormat;
        }

        public List<Module> getModules() {
            return modules;
        }

        public PropertyNamingStrategy getPropertyNamingStrategy() {
            return propertyNamingStrategy;
        }

        public Inclusion getSerializationInclusion() {
            return serializationInclusion;
        }

        boolean isDeserializationTrimStrings() {
            return deserializationTrimStrings;
        }

        public boolean isWriteEmptyArrays() {
            return writeEmptyArrays;
        }

        public boolean isWriteNullMapValues() {
            return writeNullMapValues;
        }

        public Config writeDatesAsTimestamps(final boolean writeDatesAsTimestamps) {
            this.writeDatesAsTimestamps = writeDatesAsTimestamps;
            return this;
        }

        public Config failOnUnknownProperties(final boolean failOnUnknownProperties) {
            this.failOnUnknownProperties = failOnUnknownProperties;
            return this;
        }

        public Config withDateFormat(final DateFormat dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Config withoutDateFormat() {
            return withDateFormat(null);
        }

        public Config withModule(final Module module) {
            modules.add(module);
            return this;
        }

        public Config withModules(final Module... modules) {
            if (modules != null) {
                Collections.addAll(this.modules, modules);
            }
            return this;
        }

        public Config withPropertyNamingStrategy(final PropertyNamingStrategy propertyNamingStrategy) {
            this.propertyNamingStrategy = propertyNamingStrategy;
            return this;
        }

        public Config withoutPropertyNamingStrategy() {
            return withPropertyNamingStrategy(null);
        }

        public Config withSerializationInclusion(final Inclusion serializationInclusion) {
            this.serializationInclusion = serializationInclusion;
            return this;
        }

        public Config withDeserializationTrimStrings(final boolean deserializationTrimStrings) {
            this.deserializationTrimStrings = deserializationTrimStrings;
            return this;
        }

        public Config writeEmptyArrays(final boolean writeEmptyArrays) {
            this.writeEmptyArrays = writeEmptyArrays;
            return this;
        }

        public Config writeNullMapValues(final boolean writeNullMapValues) {
            this.writeNullMapValues = writeNullMapValues;
            return this;
        }
    }
}
