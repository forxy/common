package common.api

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

import javax.xml.bind.annotation.XmlRootElement

/**
 * EntityPage class container for generic data
 */
@XmlRootElement(name = 'page')
@JsonIgnoreProperties(['numberOfElements', 'firstPage', 'lastPage', 'totalPages'])
class EntityPage<T> implements Serializable, Iterable<T> {

    int size

    int number

    long total

    List<T> content = new ArrayList<T>()

    EntityPage() {
        this(new ArrayList<T>(), 0, 0, 0)
    }

    /**
     * Constructor of {@code PageImpl}.
     *
     * @param content the content of this number, must not be {@literal null}
     * @param size the page size
     * @param number the page number
     * @param total the total amount of items available
     */
    EntityPage(final List<T> content, final int size, final int number, final long total) {
        if (null != content) {
            this.content.addAll(content)
        }
        this.total = total
        this.size = size
        this.number = number
    }

    /**
     * Creates a new {@link EntityPage} with the given content. This will result in the created {@link EntityPage} being identical
     * to the entire {@link List}.
     *
     * @param content must not be {@literal null}.
     */
    EntityPage(final List<T> content) {
        this(content, null == content ? 0 : content.size(), 0, null == content ? 0 : content.size())
    }

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    int getTotalPages() {
        return size == 0 ? 1 : (int) Math.ceil((double) total / (double) size)
    }

    /**
     * Returns the number of elements currently on this number.
     *
     * @return the number of elements currently on this number
     */
    int getNumberOfElements() {
        return content.size()
    }

    /**
     * Returns if there is a previous number.
     *
     * @return if there is a previous number
     */
    boolean hasPreviousPage() {
        return number > 0
    }

    /**
     * Returns whether the current number is the first one.
     *
     * @return true if the current number is the first one.
     */
    boolean isFirstPage() {
        return !hasPreviousPage()
    }

    /**
     * Returns if there is a next number.
     *
     * @return if there is a next number
     */
    boolean hasNextPage() {
        return number + 1 < totalPages
    }

    /**
     * Returns whether the current number is the last one.
     *
     * @return true if the current number is the last one.
     */
    boolean isLastPage() {
        return !hasNextPage()
    }

    /*
     * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */

    Iterator<T> iterator() {
        return content.iterator()
    }

    /**
     * Returns whether the {@link EntityPage} has content at all.
     *
     * @return true if the {@link EntityPage} has content at all.
     */
    boolean hasContent() {
        return !content.isEmpty()
    }

    /**
     * Returns the number content as {@link List}.
     *
     * @return the number content as {@link List}.
     */
    List<T> getContent() {
        return Collections.unmodifiableList(content)
    }
}
