<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Đăng nhập"/>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-5">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-3 text-center">Đăng nhập</h1>

                <c:if test="${param.registered != null}">
                    <div class="alert alert-success">Đăng ký tài khoản thành công. Vui lòng đăng nhập.</div>
                </c:if>
                <c:if test="${param.logout != null}">
                    <div class="alert alert-info">Bạn đã đăng xuất khỏi hệ thống.</div>
                </c:if>
                <c:if test="${param.error != null}">
                    <div class="alert alert-danger">Sai tên đăng nhập, mật khẩu hoặc tài khoản đang bị khóa.</div>
                </c:if>

                <form method="post" action="${pageContext.request.contextPath}/login">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div class="mb-3">
                        <label for="username" class="form-label">Tên đăng nhập</label>
                        <input type="text" class="form-control" id="username" name="username" required autofocus>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Đăng nhập</button>
                </form>

                <hr>
                <p class="text-center mb-0">
                    Chưa có tài khoản?
                    <a href="${pageContext.request.contextPath}/register">Đăng ký ngay</a>
                </p>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
