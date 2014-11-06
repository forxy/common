package common.logging.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.regex.Pattern;

/**
 * Helper class for data prettifying
 */
public class FormatHelper {
    final private static class MuteErrorListener implements ErrorListener {
        @Override
        public void warning(final TransformerException exception) throws TransformerException {
            //ignore error
        }

        @Override
        public void error(final TransformerException exception) throws TransformerException {
            //ignore error
        }

        @Override
        public void fatalError(final TransformerException exception) throws TransformerException {
            //ignore error
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(FormatHelper.class);

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();
    private static final Pattern COMPACT_FORMAT = Pattern.compile("\\s*[\r\n]\\s*");
    private static final char[] BREAK_AFTER_CHARS = new char[]{'>', ']', '\t', ' '};

    private FormatHelper() {
    }

    /**
     * Format string compact removing line breaks & trailing\leading spaces
     *
     * @param input data string
     * @return formatted string
     */
    public static String compactFormat(String input) {
        if (input != null) {
            input = COMPACT_FORMAT.matcher(input).replaceAll("");
        }
        return input;
    }

    /**
     * Format xml with pretty-print.
     * Note: dom4j OutputFormat is slower comparing to javax
     *
     * @param input data string
     * @return formatted string
     */
    public static String prettyFormat(String input) {
        if (input != null) {
            try {
                final Source xmlInput = new StreamSource(new StringReader(input));
                final StreamResult xmlOutput = new StreamResult(new StringWriter());
                final Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
                transformer.setErrorListener(new MuteErrorListener());
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                input = xmlOutput.getWriter().toString();
            } catch (Exception e) {
                LOGGER.debug("Error formatting with prettyFormat", e);
            }
        }
        return input;
    }

    /**
     * Break line into chunks.
     *
     * @param input     string to break, we imply suppose it does not have line breaks
     * @param chunkSize chunk size
     * @param newLine   new line char
     * @return chunked string
     */
    public static String prettyBreak(String input, final int chunkSize, final String newLine) {
        if (input != null) {
            final StringBuilder result = new StringBuilder(10000);
            while (input.length() > chunkSize) {
                int breakIndex = -1;
                final String breakLookup = input.substring(0, chunkSize);
                for (final char breakChar : BREAK_AFTER_CHARS) {
                    breakIndex = breakLookup.lastIndexOf(breakChar);
                    if (breakIndex > -1) {
                        break;
                    }
                }
                breakIndex = breakIndex == -1 ? chunkSize - 1 : breakIndex;
                result.append(input.substring(0, breakIndex + 1)).append(newLine);
                input = input.substring(breakIndex + 1);
            }
            if (input.length() > 0) {
                result.append(input);
            }
            input = result.toString();
        }
        return input;
    }
}
