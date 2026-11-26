<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="pageTitle" value="Đăng ký tài khoản"/>
<%@ include file="../common/header.jsp" %>

<div class="row justify-content-center">
    <div class="col-md-6">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-3 text-center">Đăng ký tài khoản</h1>

                <form:form method="post" action="${pageContext.request.contextPath}/register" modelAttribute="registerRequest">
                    <div class="mb-3">
                        <label for="username" class="form-label">Tên đăng nhập *</label>
                        <form:input path="username" id="username" cssClass="form-control" required="required"/>
                        <form:errors path="username" cssClass="invalid-feedback d-block"/>
                    </div>
                    <div class="mb-3">
                        <label for="fullname" class="form-label">Họ và tên *</label>
                        <form:input path="fullname" id="fullname" cssClass="form-control" required="required"/>
                        <form:errors path="fullname" cssClass="invalid-feedback d-block"/>
                    </div>
                    <div class="mb-3">
                        <label for="email" class="form-label">Email *</label>
                        <form:input path="email" id="email" type="email" cssClass="form-control" required="required"/>
                        <form:errors path="email" cssClass="invalid-feedback d-block"/>
                    </div>
                    <div class="mb-3">
                        <label for="phone" class="form-label">Số điện thoại</label>
                        <form:input path="phone" id="phone" cssClass="form-control"/>
                        <form:errors path="phone" cssClass="invalid-feedback d-block"/>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Mật khẩu *</label>
                        <form:password path="password" id="password" cssClass="form-control" required="required"/>
                        <form:errors path="password" cssClass="invalid-feedback d-block"/>
                    </div>
                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Xác nhận mật khẩu *</label>
                        <form:password path="confirmPassword" id="confirmPassword" cssClass="form-control" required="required"/>
                        <form:errors path="confirmPassword" cssClass="invalid-feedback d-block"/>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Tạo tài khoản</button>
                </form:form>

                <hr>
                <p class="text-center mb-0">
                    Đã có tài khoản?
                    <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
                </p>
            </div>
        </div>
    </div>
</div>

<%@ include file="../common/footer.jsp" %>
