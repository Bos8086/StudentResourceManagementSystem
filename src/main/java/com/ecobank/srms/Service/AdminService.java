package com.ecobank.srms.Service;

import com.ecobank.srms.dto.*;

import java.io.IOException;

public interface AdminService {

    AdminRegisterResponse register(AdminRegisterRequest adminRegisterRequest) throws IOException;

    AdminLoginResponse login (AdminLoginRequest adminLoginRequest) throws IOException;

    ResetPasswordResponse reset(AdminResetPasswordRequest adminResetPasswordRequest) throws IOException;

    AdminCreateCourseResponse create(AdminCreateCourseRequest adminCreateCourseRequest)throws IOException;


    AdminCreateDepartmentResponse createDept(AdminCreateDepartmentRequest adminCreateDepartmentRequestRequest);

    AdminCreateStudentResponse createStud(AdminCreateStudentRequest adminCreateStudentRequest);


    Object displayStudDept(AdminFindStudentRequest adminFindStudentRequest);

    Object displayStudDept(String deptName);

    AdminFindStudentLevelResponse displayStudLevel(AdminFindStudentLevelRequest adminFindStudentLevelRequest);

    AdminFindStudentLevelResponse displayStudLevel(String level);

    AdminChangePasswordResponse changePassword(AdminChangePasswordRequest adminChangePasswordRequest);
}
