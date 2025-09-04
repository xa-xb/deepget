package my.iris.model;

public interface PageableVo<T> extends ListableVo<T> {
    long currentPage();

    long totalPages();

    long totalRecords();
}
