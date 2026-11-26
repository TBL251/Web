<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>${isEdit ? 'Cập nhật Danh mục' : 'Thêm Danh mục'}</title>
</head>
<body>

<div class="row justify-content-center">
    <div class="col-md-7">
        <div class="card shadow-sm">
            <div class="card-body p-4">
                <h1 class="h4 mb-3">${isEdit ? 'Cập nhật danh mục' : 'Thêm danh mục mới'}</h1>

                <c:choose>
                    <c:when test="${isEdit}">
                        <c:url var="formAction" value="/admin/categories/edit/${categoryForm.categoryId}"/>
                    </c:when>
                    <c:otherwise>
                        <c:url var="formAction" value="/admin/categories/create"/>
                    </c:otherwise>
                </c:choose>

                <form:form method="post" action="${formAction}" modelAttribute="categoryForm">
                    <div class="mb-3">
                        <label for="categoryname" class="form-label">Tên danh mục *</label>
                        <form:input path="categoryname" id="categoryname" cssClass="form-control" required="required"/>
                        <form:errors path="categoryname" cssClass="invalid-feedback d-block"/>
                    </div>

                    <div class="mb-3">
                        <label for="images" class="form-label">Đường dẫn hình ảnh (URL)</label>
                        <form:input path="images" id="images" cssClass="form-control" placeholder="https://..."/>
                        <form:errors path="images" cssClass="invalid-feedback d-block"/>
                    </div>

                    <div class="mb-3">
                        <label for="status" class="form-label">Trạng thái</label>
                        <form:select path="status" id="status" cssClass="form-select">
                            <form:option value="1" label="Hoạt động"/>
                            <form:option value="0" label="Ẩn"/>
                        </form:select>
                        <form:errors path="status" cssClass="invalid-feedback d-block"/>
                    </div>

                    <div class="d-flex gap-2">
                        <button type="submit" class="btn btn-primary">${isEdit ? 'Lưu thay đổi' : 'Thêm mới'}</button>
                        <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/admin/categories">Hủy</a>
                    </div>
                </form:form>
            </div>
        </div>
    </div>
</div>

</body>
</html>
