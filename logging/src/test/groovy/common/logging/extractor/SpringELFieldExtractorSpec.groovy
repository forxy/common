package common.logging.extractor

import spock.lang.Specification
import spock.lang.Subject

/**
 * Spring EL field extractor specification
 */
@Subject(SpringELFieldExtractor)
class SpringELFieldExtractorSpec extends Specification {

    class TestEntity {
        int intValue
        String stringValue
        List<Integer> listValue
        Map<String, String> mapValue
    }

    def "Should extract property from the specified object"() {
        setup:
        SpringELFieldExtractor extractor = new SpringELFieldExtractor(extractRules: [
                'expressionResult': "#frame['entity'].getIntValue() > 0 ? 'Greater than zero' : 'Less than zero'",
                'stringValue'     : "#frame['entity'].getStringValue()",
                'listValue'       : "#frame['entity'].getListValue()[1]",
                'mapValue'        : "#frame['entity'].getMapValue()['value']",
                'list'            : "#frame['entity'].getListValue()",
                'map'             : "#frame['entity'].getMapValue()"
        ])

        when:
        Map<String, Object> extracted = extractor.extract('payload'.bytes,
                ['entity': new TestEntity(
                        intValue: 1,
                        stringValue: 'string',
                        listValue: [1, 2],
                        mapValue: ['value': 'value1']
                )]
        )
        then:
        extracted == [
                'expressionResult': 'Greater than zero',
                'stringValue'     : 'string',
                'listValue'       : 2,
                'mapValue'        : 'value1',
                'list'            : [1, 2],
                'map'             : ['value': 'value1']
        ] as LinkedHashMap<String, Object>
    }
}
