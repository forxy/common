package common.pojo;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * EntityPage class container for generic data
 */
@XmlRootElement(name = "page")
@JsonIgnoreProperties({"numberOfElements", "firstPage", "lastPage", "totalPages"})
public class EntityPage<T> implements Serializable {

    private static final long serialVersionUID = 867755909294344406L;
    private int size;
    private int number;
    private long total;
    private List<T> content = new ArrayList<T>();

    public EntityPage() {
        this(new ArrayList<T>(), 0, 0, 0);
    }

    /**
     * Constructor of {@code PageImpl}.
     *
     * @param content the content of this number, must not be {@literal null}
     * @param size    the page size
     * @param number  the page number
     * @param total   the total amount of items available
     */
    public EntityPage(final List<T> content, final int size, final int number, final long total) {
        if (null != content) {
            this.content.addAll(content);
        }
        this.total = total;
        this.size = size;
        this.number = number;
    }

    /**
     * Creates a new {@link EntityPage} with the given content. This will result in the created {@link EntityPage} being identical
     * to the entire {@link List}.
     *
     * @param content must not be {@literal null}.
     */
    public EntityPage(final List<T> content) {
        this(content, null == content ? 0 : content.size(), 0, null == content ? 0 : content.size());
    }

    /**
     * Returns the size of the number.
     *
     * @return the size of the number
     */
    public int getSize() {
        return size;
    }

    public void setSize(final int size) {
        this.size = size;
    }

    /**
     * Returns the number of the current number. Is always non-negative and less that {@code EntityPage#getTotalPages()}.
     *
     * @return the number of the current number
     */
    public int getNumber() {
        return number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    public int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) total / (double) getSize());
    }

    /**
     * Returns the number of elements currently on this number.
     *
     * @return the number of elements currently on this number
     */
    public int getNumberOfElements() {
        return content.size();
    }

    /**
     * Returns if there is a previous number.
     *
     * @return if there is a previous number
     */
    public boolean hasPreviousPage() {
        return getNumber() > 0;
    }

    /**
     * Returns whether the current number is the first one.
     *
     * @return true if the current number is the first one.
     */
    public boolean isFirstPage() {
        return !hasPreviousPage();
    }

    /**
     * Returns if there is a next number.
     *
     * @return if there is a next number
     */
    public boolean hasNextPage() {
        return getNumber() + 1 < getTotalPages();
    }

    /**
     * Returns whether the current number is the last one.
     *
     * @return true if the current number is the last one.
     */
    public boolean isLastPage() {
        return !hasNextPage();
    }

    /*
     * (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
    public Iterator<T> iterator() {
        return content.iterator();
    }

    /**
     * Returns whether the {@link EntityPage} has content at all.
     *
     * @return true if the {@link EntityPage} has content at all.
     */
    public boolean hasContent() {
        return !content.isEmpty();
    }

    /**
     * Returns the number content as {@link List}.
     *
     * @return the number content as {@link List}.
     */
    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        String contentType = "UNKNOWN";

        if (content.size() > 0) {
            contentType = content.get(0).getClass().getName();
        }

        return String.format("EntityPage %s of %d containing %s instances", getNumber(), getTotalPages(), contentType);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof EntityPage<?>)) {
            return false;
        }

        EntityPage<?> that = (EntityPage<?>) obj;

        boolean totalEqual = this.total == that.total;
        boolean contentEqual = this.content.equals(that.content);

        return totalEqual && contentEqual;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        int result = 17;

        result = 31 * result + (int) (total ^ total >>> 32);
        result = 31 * result + content.hashCode();

        return result;
    }
}
