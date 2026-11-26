<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>${isEdit ? 'Cập nhật Người dùng' : 'Thêm Người dùng'}</title>
</head>
<body>

<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-3">${isEdit ? 'Cập nhật người dùng' : 'Thêm người dùng mới'}</h1>

                <c:choose>
                    <c:when test="${isEdit}">
                        <c:url var="formAction" value="/admin/users/edit/${userForm.id}"/>
                    </c:when>
                    <c:otherwise>
                        <c:url var="formAction" value="/admin/users/create"/>
                    </c:otherwise>
                </c:choose>

                <form:form method="post" action="${formAction}" modelAttribute="userForm">
                    <div class="mb-3">
                        <label for="username" class="form-label">Tên đăng nhập *</label>
                        <form:input path="username" id="username" cssClass="form-control" required="required"/>
                        <form:errors path="username" cssClass="invalid-feedback d-block"/>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label">
                            ${isEdit ? 'Mật khẩu mới (để trống nếu giữ nguyên)' : 'Mật khẩu *'}
                        </label>
                        <form:password path="password" id="password" cssClass="form-control"/>
                        <form:errors path="password" cssClass="invalid-feedback d-block"/>
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
                        <label for="avatar" class="form-label">Ảnh đại diện (URL)</label>
                        <form:input path="avatar" id="avatar" cssClass="form-control" placeholder="https://..."/>
                        <form:errors path="avatar" cssClass="invalid-feedback d-block"/>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="roleid" class="form-label">Vai trò</label>
                            <form:select path="roleid" id="roleid" cssClass="form-select">
                                <form:option value="1" label="Admin"/>
                                <form:option value="2" label="Manager"/>
                                <form:option value="3" label="User"/>
                            </form:select>
                            <form:errors path="roleid" cssClass="invalid-feedback d-block"/>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="status" class="form-label">Trạng thái</label>
                            <form:select path="status" id="status" cssClass="form-select">
                                <form:option value="1" label="Hoạt động"/>
                                <form:option value="0" label="Khóa"/>
                            </form:select>
                            <form:errors path="status" cssClass="invalid-feedback d-block"/>
                        </div>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">${isEdit ? 'Lưu thay đổi' : 'Thêm mới'}</button>
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/users">Hủy</a>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
