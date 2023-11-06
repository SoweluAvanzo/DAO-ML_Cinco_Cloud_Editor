package info.scce.cincocloud.core.rest.tos;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  public static <S,T> PageTO<S> ofQuery(PanacheQuery<T> query, int index, int size, Function<T,S> transformFunction) {
    final var page = Page.of(index, size);
    final var pagedQuery = query.page(page);
    final var to = new PageTO<S>();
    to.items = pagedQuery.list().stream()
            .map(transformFunction)
            .collect(Collectors.toList());
    to.number = pagedQuery.page().index;
    to.size = pagedQuery.page().size;
    to.amountOfPages = pagedQuery.pageCount();
    to.hasPreviousPage = pagedQuery.hasPreviousPage();
    to.hasNextPage = pagedQuery.hasNextPage();
    return to;
  }

  public static <S,T> PageTO<S> ofList(List<T> list, int index, int size, Function<T,S> transformFunction) {
    final var to = new PageTO<S>();

    int start = index * size;
    int end = Math.min(start + size, list.size());

    to.items = list.subList(start, end).stream()
            .map(transformFunction)
            .collect(Collectors.toList());

    to.number = index;
    to.size = size;
    to.amountOfPages = (long) Math.ceil((double) list.size() / size);
    to.hasPreviousPage = index > 0;
    to.hasNextPage = (index + 1) * size < list.size();

    return to;
  }

}
