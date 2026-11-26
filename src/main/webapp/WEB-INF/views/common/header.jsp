<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><c:out value="${pageTitle != null ? pageTitle : 'Spring Boot 4 Admin'}"/></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand fw-semibold" href="${pageContext.request.contextPath}/home">Web Admin</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto">
                <c:if test="${pageContext.request.userPrincipal != null}">
                    <li class="nav-item">
                        <a class="nav-link ${activeMenu == 'home' ? 'active' : ''}" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                    </li>
                    <% if (request.isUserInRole("ADMIN")) { %>
                    <li class="nav-item">
                        <a class="nav-link ${activeMenu == 'categories' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/categories">Quản lý Danh mục</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link ${activeMenu == 'users' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/users">Quản lý Người dùng</a>
                    </li>
                    <% } %>
                </c:if>
            </ul>
            <div class="d-flex align-items-center gap-2">
                <c:choose>
                    <c:when test="${pageContext.request.userPrincipal != null}">
                        <span class="navbar-text text-light me-2">
                            Xin chào, <strong><c:out value="${pageContext.request.userPrincipal.name}"/></strong>
                        </span>
                        <form method="post" action="${pageContext.request.contextPath}/logout" class="m-0">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="btn btn-outline-light btn-sm">Đăng xuất</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <a class="btn btn-outline-light btn-sm" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                        <a class="btn btn-primary btn-sm" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>
<main class="container pb-5">
