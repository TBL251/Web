<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><sitemesh:write property="title"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <sitemesh:write property="head"/>
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid px-4">
        <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/admin/categories/searchpaginated">Admin Dashboard</a>
        <div class="d-flex align-items-center gap-2 ms-auto">
            <c:if test="${pageContext.request.userPrincipal != null}">
                <span class="navbar-text text-light me-2">
                    Admin: <strong><c:out value="${pageContext.request.userPrincipal.name}"/></strong>
                </span>
                <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <button type="submit" class="btn btn-outline-light btn-sm">Đăng xuất</button>
                </form>
            </c:if>
        </div>
    </div>
</nav>
<div class="container-fluid">
    <div class="row">
        <aside class="col-md-3 col-lg-2 bg-white border-end min-vh-100 py-3">
            <div class="list-group list-group-flush">
                <a class="list-group-item list-group-item-action ${activeMenu == 'categories' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/admin/categories/searchpaginated">Quản lý Danh mục</a>
                <a class="list-group-item list-group-item-action ${activeMenu == 'users' ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/admin/users/searchpaginated">Quản lý Người dùng</a>
                <a class="list-group-item list-group-item-action"
                   href="${pageContext.request.contextPath}/home">Về Trang chủ</a>
            </div>
        </aside>
        <main class="col-md-9 col-lg-10 py-4 px-4">
            <sitemesh:write property="body"/>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
