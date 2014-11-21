package common.utils

/**
 * Helps to get network data
 */
abstract class NetworkUtils {

    static String getURLResource(final String urlString) throws IOException {
        URL currenciesXmlUrl = new URL(urlString)
        URLConnection urlConnection = currenciesXmlUrl.openConnection()
        InputStream is = urlConnection.inputStream
        BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        StringBuilder sb = new StringBuilder()
        try {
            String line
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n')
            }
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            try {
                is.close()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
        return sb.toString()
    }
}
