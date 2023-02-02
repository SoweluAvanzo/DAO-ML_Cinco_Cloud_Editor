package info.scce.cincocloud.core.rest.tos;

import java.util.List;
import java.util.Objects;

/**
 * Entity for dealing with database pages.
 *
 * @param <T> The type of the items that are wrapped by the page.
 */
public class PageTO<T> {

  /** The items that belong to the page. */
  public List<T> items;

  /** The current requested page number. */
  public int number;

  /** The size of the current page. */
  public int size;

  public boolean hasNextPage;

  public boolean hasPreviousPage;

  /** The amount of possible pages given the page size. */
  public long amountOfPages;

  public PageTO(List<T> items, int number, int size, long amountOfPages, boolean hasPreviousPage, boolean hasNextPage) {
    Objects.requireNonNull(items, "items must not be null");

    if (number < 0) {
      throw new IllegalArgumentException("pageNumber can not be < 0");
    } else if (size < 0) {
      throw new IllegalArgumentException("pageSize can not be < 0");
    } else if (amountOfPages < 0) {
      throw new IllegalArgumentException("numberOfPages can not be < 0");
    }

    this.items = items;
    this.number = number;
    this.size = size;
    this.amountOfPages = amountOfPages;
    this.hasPreviousPage = hasPreviousPage;
    this.hasNextPage = hasNextPage;
  }
}
