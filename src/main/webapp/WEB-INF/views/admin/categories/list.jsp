<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quản lý Danh mục</title>
</head>
<body>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h1 class="h4 mb-0">Quản lý Danh mục (Category)</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/categories/add">+ Thêm danh mục</a>
</div>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success"><c:out value="${successMessage}"/></div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
</c:if>

<div class="row g-2 mb-3 align-items-center">
    <div class="col-md-8">
        <form method="get" action="${pageContext.request.contextPath}/admin/categories/searchpaginated" class="d-flex gap-2">
            <input type="text" class="form-control" name="name" id="name"
                   value="<c:out value='${name}'/>" placeholder="Nhập từ khóa để tìm tên danh mục...">
            <input type="hidden" name="size" value="${categoryPage.size}"/>
            <button type="submit" class="btn btn-outline-primary text-nowrap">Tìm kiếm</button>
            <a class="btn btn-outline-secondary text-nowrap" href="${pageContext.request.contextPath}/admin/categories/searchpaginated">Đặt lại</a>
        </form>
    </div>
    <div class="col-md-4 d-flex justify-content-md-end">
        <form method="get" action="${pageContext.request.contextPath}/admin/categories/searchpaginated" class="d-flex align-items-center gap-2">
            <c:if test="${not empty name}">
                <input type="hidden" name="name" value="<c:out value='${name}'/>"/>
            </c:if>
            <label for="size" class="form-label mb-0 text-nowrap">Hiển thị:</label>
            <select name="size" id="size" class="form-select form-select-sm" style="width:auto;" onchange="this.form.submit()">
                <option value="3" ${categoryPage.size == 3 ? 'selected' : ''}>3 / trang</option>
                <option value="5" ${categoryPage.size == 5 ? 'selected' : ''}>5 / trang</option>
                <option value="10" ${categoryPage.size == 10 ? 'selected' : ''}>10 / trang</option>
                <option value="15" ${categoryPage.size == 15 ? 'selected' : ''}>15 / trang</option>
                <option value="20" ${categoryPage.size == 20 ? 'selected' : ''}>20 / trang</option>
            </select>
        </form>
    </div>
</div>

<div class="card shadow-sm">
    <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
            <thead class="table-light">
            <tr>
                <th>STT</th>
                <th>ID</th>
                <th>Ảnh</th>
                <th>Tên danh mục</th>
                <th>Trạng thái</th>
                <th class="text-end">Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${!categoryPage.hasContent()}">
                <tr>
                    <td colspan="6" class="text-center py-4 text-muted">Không có danh mục nào phù hợp.</td>
                </tr>
            </c:if>
            <c:if test="${categoryPage.hasContent()}">
                <c:forEach var="cate" items="${categoryPage.content}" varStatus="STT">
                    <tr>
                        <td>${categoryPage.number * categoryPage.size + STT.index + 1}</td>
                        <td><c:out value="${cate.categoryId}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty cate.images}">
                                    <img src="<c:out value='${cate.images}'/>" alt="<c:out value='${cate.categoryname}'/>"
                                         style="width:48px;height:48px;object-fit:cover;" class="rounded border">
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">Không có</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><c:out value="${cate.categoryname}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${cate.status == 1}">
                                    <span class="badge bg-success">Hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Ẩn</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/admin/categories/edit/${cate.categoryId}">Sửa</a>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/categories/delete/${cate.categoryId}"
                                  class="d-inline"
                                  onsubmit="return confirm('Xác nhận xóa danh mục này?');">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </c:if>
            </tbody>
        </table>
    </div>
</div>

<c:if test="${categoryPage.totalPages > 1}">
    <nav class="mt-3 d-flex justify-content-between align-items-center" aria-label="Phân trang danh mục">
        <div class="text-muted small">
            Trang ${categoryPage.number + 1} / ${categoryPage.totalPages} (Tổng ${categoryPage.totalElements} mục)
        </div>
        <ul class="pagination mb-0">
            <c:url var="prevUrl" value="/admin/categories/searchpaginated">
                <c:if test="${not empty name}"><c:param name="name" value="${name}"/></c:if>
                <c:param name="size" value="${categoryPage.size}"/>
                <c:param name="page" value="${categoryPage.number}"/>
            </c:url>
            <li class="page-item ${categoryPage.first ? 'disabled' : ''}">
                <a class="page-link" href="${categoryPage.first ? '#' : prevUrl}">Trước</a>
            </li>

            <c:forEach var="pageNumber" items="${pageNumbers}">
                <c:url var="pageUrl" value="/admin/categories/searchpaginated">
                    <c:if test="${not empty name}"><c:param name="name" value="${name}"/></c:if>
                    <c:param name="size" value="${categoryPage.size}"/>
                    <c:param name="page" value="${pageNumber}"/>
                </c:url>
                <li class="${pageNumber == categoryPage.number + 1 ? 'page-item active' : 'page-item'}">
                    <a class="page-link" href="${pageUrl}">${pageNumber}</a>
                </li>
            </c:forEach>

            <c:url var="nextUrl" value="/admin/categories/searchpaginated">
                <c:if test="${not empty name}"><c:param name="name" value="${name}"/></c:if>
                <c:param name="size" value="${categoryPage.size}"/>
                <c:param name="page" value="${categoryPage.number + 2}"/>
            </c:url>
            <li class="page-item ${categoryPage.last ? 'disabled' : ''}">
                <a class="page-link" href="${categoryPage.last ? '#' : nextUrl}">Sau</a>
            </li>
        </ul>
    </nav>
</c:if>

</body>
</html>
