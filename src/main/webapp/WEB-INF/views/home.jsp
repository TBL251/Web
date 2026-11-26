<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Trang chủ</title>
</head>
<body>
<c:if test="${param.denied != null}">
    <div class="alert alert-warning">Bạn không có quyền truy cập khu vực quản trị (chỉ dành cho Admin).</div>
</c:if>

<div class="card shadow-sm">
    <div class="card-body p-4">
        <h1 class="h4 mb-3">Trang chủ hệ thống</h1>
        <c:if test="${currentUser != null}">
            <p class="mb-1">Tài khoản: <strong><c:out value="${currentUser.username}"/></strong></p>
            <p class="mb-1">Họ tên: <c:out value="${currentUser.fullname}"/></p>
            <p class="mb-1">Email: <c:out value="${currentUser.email}"/></p>
            <p class="mb-3">Vai trò: <span class="badge bg-secondary"><c:out value="${currentUser.roleName}"/></span></p>
        </c:if>

        <% if (request.isUserInRole("ADMIN")) { %>
        <div class="d-flex gap-2">
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/categories/searchpaginated">Quản lý Danh mục</a>
            <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/users/searchpaginated">Quản lý Người dùng</a>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
