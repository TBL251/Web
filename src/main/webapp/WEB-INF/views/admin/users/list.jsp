<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quản lý Người dùng</title>
</head>
<body>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h1 class="h4 mb-0">Quản lý Người dùng (User)</h1>
    <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/users/add">+ Thêm người dùng</a>
</div>

<c:if test="${not empty successMessage}">
    <div class="alert alert-success"><c:out value="${successMessage}"/></div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger"><c:out value="${errorMessage}"/></div>
</c:if>

<div class="row g-2 mb-3 align-items-center">
    <div class="col-md-8">
        <form method="get" action="${pageContext.request.contextPath}/admin/users/searchpaginated" class="d-flex gap-2">
            <input type="text" class="form-control" name="name" id="name"
                   value="<c:out value='${name}'/>" placeholder="Nhập từ khóa tìm theo username, họ tên hoặc email...">
            <input type="hidden" name="size" value="${userPage.size}"/>
            <button type="submit" class="btn btn-outline-primary text-nowrap">Tìm kiếm</button>
            <a class="btn btn-outline-secondary text-nowrap" href="${pageContext.request.contextPath}/admin/users/searchpaginated">Đặt lại</a>
        </form>
    </div>
    <div class="col-md-4 d-flex justify-content-md-end">
        <form method="get" action="${pageContext.request.contextPath}/admin/users/searchpaginated" class="d-flex align-items-center gap-2">
            <c:if test="${not empty name}">
                <input type="hidden" name="name" value="<c:out value='${name}'/>"/>
            </c:if>
            <label for="size" class="form-label mb-0 text-nowrap">Hiển thị:</label>
            <select name="size" id="size" class="form-select form-select-sm" style="width:auto;" onchange="this.form.submit()">
                <option value="3" ${userPage.size == 3 ? 'selected' : ''}>3 / trang</option>
                <option value="5" ${userPage.size == 5 ? 'selected' : ''}>5 / trang</option>
                <option value="10" ${userPage.size == 10 ? 'selected' : ''}>10 / trang</option>
                <option value="15" ${userPage.size == 15 ? 'selected' : ''}>15 / trang</option>
                <option value="20" ${userPage.size == 20 ? 'selected' : ''}>20 / trang</option>
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
                <th>Tên đăng nhập</th>
                <th>Họ tên</th>
                <th>Email</th>
                <th>Vai trò</th>
                <th>Trạng thái</th>
                <th class="text-end">Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:if test="${!userPage.hasContent()}">
                <tr>
                    <td colspan="8" class="text-center py-4 text-muted">Không có người dùng nào phù hợp.</td>
                </tr>
            </c:if>
            <c:if test="${userPage.hasContent()}">
                <c:forEach var="u" items="${userPage.content}" varStatus="STT">
                    <tr>
                        <td>${userPage.number * userPage.size + STT.index + 1}</td>
                        <td><c:out value="${u.id}"/></td>
                        <td><strong><c:out value="${u.username}"/></strong></td>
                        <td><c:out value="${u.fullname}"/></td>
                        <td><c:out value="${u.email}"/></td>
                        <td>
                            <c:choose>
                                <c:when test="${u.roleid == 1}">
                                    <span class="badge bg-danger">ADMIN</span>
                                </c:when>
                                <c:when test="${u.roleid == 2}">
                                    <span class="badge bg-warning text-dark">MANAGER</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-info text-dark">USER</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:choose>
                                <c:when test="${u.status == 1}">
                                    <span class="badge bg-success">Hoạt động</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Khóa</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-end">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/admin/users/edit/${u.id}">Sửa</a>
                            <form method="post"
                                  action="${pageContext.request.contextPath}/admin/users/delete/${u.id}"
                                  class="d-inline"
                                  onsubmit="return confirm('Xác nhận xóa người dùng này?');">
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

<c:if test="${userPage.totalPages > 1}">
    <nav class="mt-3 d-flex justify-content-between align-items-center" aria-label="Phân trang người dùng">
        <div class="text-muted small">
            Trang ${userPage.number + 1} / ${userPage.totalPages} (Tổng ${userPage.totalElements} người dùng)
        </div>
        <ul class="pagination mb-0">
            <c:url var="prevUrl" value="/admin/users/searchpaginated">
                <c:if test="${not empty name}"><c:param name="name" value="${name}"/></c:if>
                <c:param name="size" value="${userPage.size}"/>
                <c:param name="page" value="${userPage.number}"/>
            </c:url>
            <li class="page-item ${userPage.first ? 'disabled' : ''}">
                <a class="page-link" href="${userPage.first ? '#' : prevUrl}">Trước</a>
            </li>

            <c:forEach var="pageNumber" items="${pageNumbers}">
                <c:url var="pageUrl" value="/admin/users/searchpaginated">
                    <c:if test="${not empty name}"><c:param name="name" value="${name}"/></c:if>
                    <c:param name="size" value="${userPage.size}"/>
                    <c:param name="page" value="${pageNumber}"/>
                </c:url>
                <li class="${pageNumber == userPage.number + 1 ? 'page-item active' : 'page-item'}">
                    <a class="page-link" href="${pageUrl}">${pageNumber}</a>
                </li>
            </c:forEach>

            <c:url var="nextUrl" value="/admin/users/searchpaginated">
                <c:if test="${not empty name}"><c:param name="name" value="${name}"/></c:if>
                <c:param name="size" value="${userPage.size}"/>
                <c:param name="page" value="${userPage.number + 2}"/>
            </c:url>
            <li class="page-item ${userPage.last ? 'disabled' : ''}">
                <a class="page-link" href="${userPage.last ? '#' : nextUrl}">Sau</a>
            </li>
        </ul>
    </nav>
</c:if>

</body>
</html>
