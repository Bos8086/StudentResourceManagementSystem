package com.ecobank.srms.Service;

import com.ecobank.srms.dto.*;
import com.ecobank.srms.encryption.EncryptionService;
import com.ecobank.srms.exceptions.GenericException;
import com.ecobank.srms.model.Department;
import com.ecobank.srms.model.Student;
import com.ecobank.srms.model.ViewStudent;
import com.ecobank.srms.repository.DepartmentRepository;
import com.ecobank.srms.repository.IdVerificationRepository;
import com.ecobank.srms.repository.StudentRepository;
//import org.modelmapper.ModelMapper;
import com.ecobank.srms.utils.Credentials;
import com.ecobank.srms.utils.JwtUtils;
import com.ecobank.srms.utils.ResponseCodes;
import com.ecobank.srms.utils.Token;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


@Service
public class StudentServiceImpl implements StudentService {

    @Value("${client.id}")
    private String id;

    @Value("${client.sourcecode}")
    private String sourcecode;

    @Value("${client.secret}")
    private String secret;



    @Autowired

    private StudentRepository studentRepository;
    @Autowired
    private DepartmentServiceImpl departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private IdVerificationRepository idVerificationRepository;



    @Autowired
    HttpServletRequest httpServletRequest;



    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EncryptionService encryptionService;
    Logger logger = Logger.getLogger(StudentServiceImpl.class.getName());


    @Override
    public StudentResponse Register(StudentRequest studentRequest) throws IOException {
        boolean isPresent = studentRepository.findPersonByJambNo(studentRequest.getJambNo()).isPresent();

        boolean isPresent_email = studentRepository.findPersonByEmail(studentRequest.getEmail()).isPresent();

        boolean isPresent_verify = idVerificationRepository.findByuserId(studentRequest.getJambNo()).isPresent();

        Department department = departmentRepository.findByDeptName(studentRequest.getDepartment());




        //Student isPresent = studentRepository.findByUserName(studentRequest.getUserName());

        if ((isPresent))
            throw new GenericException(ResponseCodes.ALREADY_EXIST, "This registration exists please sign in", HttpStatus.BAD_REQUEST);

//        return StudentResponse.builder().message("This registration exists please sign in").build();

        if ((isPresent_email))
            throw new GenericException(ResponseCodes.ALREADY_EXIST, "This Email exists", HttpStatus.BAD_REQUEST);

        //return StudentResponse.builder().message("This Email exists").build();

//        if(!(isPresent_verify))
      //  throw new GenericException(ResponseCodes.NOT_FOUND, "Access Not Granted, Contact Support", HttpStatus.NOT_FOUND);



        if ((department==null)){

            throw new GenericException(ResponseCodes.NOT_FOUND, "This Department has not been created/ does not exist", HttpStatus.NOT_FOUND);

            //            return StudentResponse.builder().message(
//                    "This Department has not been created/ does not exist"
//            ).build();
        }
        //return "The Registration `number is existing, please sign in";

            Student student = new Student();

        logger.info("student1:  " + student);
            ModelMapper modelMapper = new ModelMapper();
            String Password = studentRequest.getPassword();
            logger.info("password" + studentRequest.getPassword());
            String confirmPassword = studentRequest.getConfirmPassword();
            logger.info("Confirm password" + studentRequest.getConfirmPassword());
            if (!(Password.equals(confirmPassword))){
                throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Password must match confirm Password", HttpStatus.UNAUTHORIZED);

                //return StudentResponse.builder().message("Password must match confirm Password").build();
            }
                BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
                final String encodedPassword = bcryptPasswordEncoder.encode(studentRequest.getPassword());
                studentRequest.setPassword(encodedPassword);
                Long Dept_Id = departmentService.getDeptId(studentRequest.getDepartment());
                Date date = new Date();
                studentRequest.setDate_Created(date);
                studentRequest.setDept_id(Dept_Id);
                logger.info("Matric No: " + studentRequest.getJambNo());
                logger.info(" Password: " + studentRequest.getPassword());
                logger.info("Level: " + studentRequest.getLevel());
                logger.info("Department:" + studentRequest.getDepartment().toUpperCase());
                logger.info("Email: " + studentRequest.getEmail());


                modelMapper.map(studentRequest, student);
                logger.info("studentRequest:  " + studentRequest);

                logger.info("student:  " + student);

                studentRepository.save(student);
                return StudentResponse.builder().message("Thank you for registering")
                        .jambNo(studentRequest.getJambNo())
                        .level(studentRequest.getLevel())
                        .department(studentRequest.getDepartment().toUpperCase())
                        .build();

        }


    @Override
    public StudentResponse Login(LoginRequest loginRequest) throws IOException {


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Student student;



        Token token;
        student = studentRepository.findByJambNo(loginRequest.getJambNo());
        if (student == null) {

            throw new GenericException(ResponseCodes.NOT_FOUND, "The User Doesn't exist", HttpStatus.NOT_FOUND);
           // return StudentResponse.builder().message("The User Doesn't exist").build();
        } else {
            if (!passwordEncoder.matches(loginRequest.getPassword(), student.getPassword())) {

                throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Incorrect Password", HttpStatus.UNAUTHORIZED);
                //return StudentResponse.builder().message("Incorrect Password").build();
            } else {
                token = extractToken(httpServletRequest);

                return StudentResponse.builder().message("Login Successful")
                        .jambNo(loginRequest.getJambNo())
                        .token(String.valueOf(token.getAccessToken()))
                        .department(student.getDepartment())
                        .level(student.getLevel())
                        .email(student.getEmail())
                        .build();
            }
        }

    }

    @Override
    public ChangePasswordResponse updateCurrentPassword(ChangePasswordRequest changePasswordRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Student currentStudent = studentRepository.findByJambNo(changePasswordRequest.getJambNo());

        String currentPassword = changePasswordRequest.getCurrentPassword();
        String newPassword = changePasswordRequest.getNewPassword();
        String confirmPassword = changePasswordRequest.getConfirmPassword();

        if (currentStudent == null) {
            throw new GenericException(ResponseCodes.NOT_FOUND, "Please Register, User does not exist", HttpStatus.NOT_FOUND);

            //return new ChangePasswordResponse("Please Register, User does not exist");
        } else {

            if (passwordEncoder.matches(newPassword, currentStudent.getPassword())) {
                throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Old password cannot be the same as new password", HttpStatus.UNAUTHORIZED);

                //return new ChangePasswordResponse("Old password cannot be the same as new password");
            }

            if(!(passwordEncoder.matches(currentPassword,currentStudent.getPassword()))){
                throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Original Password is Incorrect", HttpStatus.UNAUTHORIZED);

                //return  new ChangePasswordResponse("Original Password is Incorrect");
            }

            else {
                if (confirmPassword.equals(newPassword)) {
                    currentStudent.setPassword(passwordEncoder.encode(newPassword));
                    studentRepository.save(currentStudent);
                    return new ChangePasswordResponse("Password successfully changed");
                } else {
                    throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Password must match", HttpStatus.UNAUTHORIZED);

                    //return new ChangePasswordResponse("Password must match");
                }
             }
        }
    }


@Override
    public Token extractToken(HttpServletRequest httpServletRequest) {

        Token token = new Token();
        try {
            Credentials credentials = encryptionService.extractKeys(httpServletRequest);
            encryptionService.isCredentialValid(id, secret, sourcecode);


            System.out.println("keys==="+credentials.toString());
            String clientId = credentials.getClientid();

            System.out.println("________id>>>"+clientId);

            //token = jwtUtils.createToken(clientId);
            token = jwtUtils.createToken(id);

        } catch (Exception ex) {
            logger.info("User Add Failed" + ex);

        }
        return token;
    }

    @Override
    public ResetPasswordResponse reset(ResetPasswordRequest resetPasswordRequest) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Student currentStudent = studentRepository.findByJambNo(resetPasswordRequest.getJambNo());
        String newPassword = resetPasswordRequest.getNewPassword();
        String confirmPassword = resetPasswordRequest.getConfirmPassword();

        if (currentStudent == null) {
            throw new GenericException(ResponseCodes.NOT_FOUND, "Please Register, User does not exist", HttpStatus.NOT_FOUND);

            //return new ResetPasswordResponse("Please Register, User does not exist");
        } else {
            if (passwordEncoder.matches(newPassword, currentStudent.getPassword()))
            {
                throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Old password cannot be the same as new password", HttpStatus.UNAUTHORIZED);

                //return new ResetPasswordResponse("Old password cannot be the same as new password");
            } else
            {
                if (confirmPassword.equals(newPassword)) {
                    currentStudent.setPassword(passwordEncoder.encode(newPassword));
                    studentRepository.save(currentStudent);
                    return new ResetPasswordResponse("Password successfully Reset");
                } else {
                    throw new GenericException(ResponseCodes.INVALID_CREDENTIAL, "Password must match", HttpStatus.UNAUTHORIZED);

//                    return new ResetPasswordResponse("Password must match");
                }
            }
        }
    }

    @Override
    public Object displayStud() {
        List<Student> stud = studentRepository.findAll();
        List<ViewStudent> viewstudentList = new ArrayList<>();

        if (stud==null){
            throw new GenericException(ResponseCodes.NOT_FOUND, "There are no Departments", HttpStatus.NOT_FOUND);
            //return "There are no Departments";
        }
        else{
            for (int i = 0; i < stud.size(); i++){
                ViewStudent viewStudent = new ViewStudent();
                viewStudent.setJambNo(stud.get(i).getJambNo());
                viewStudent.setDepartment(stud.get(i).getDepartment());
                viewStudent.setEmail(stud.get(i).getEmail());
                viewStudent.setDate_Created(stud.get(i).getDate_Created());
                viewStudent.setLevel(stud.get(i).getLevel());
                viewstudentList.add(viewStudent);

                }

            }
            return viewstudentList;
        }


    @Override
    public AdminCountStudDisplayResponse countStud() {
        Long stud = studentRepository.count();

        return AdminCountStudDisplayResponse.builder()
                .message("Successful")
                .response("These are the number of Students")
                .count(stud)
                .build();
    }

    @Override
    public List<Object> displayCountStudbyDept() {
        List<Object> Object = studentRepository.findByDepartmentAndStudent();

        return Collections.singletonList(Object);
    }

    @Override
    public AdminFindStudentResponse showCountLevelByDepartment(AdminFindStudentRequest adminFindStudentRequest) {
        Department dept = departmentRepository.findByDeptName(adminFindStudentRequest.getDeptName());

        if (dept==null){
            throw new GenericException(ResponseCodes.NOT_FOUND, "The Department does not exist", HttpStatus.NOT_FOUND);


//            return AdminFindStudentResponse
//                    .builder()
//                    .response("Failed")
//                    .code("99")
//                    .message("The Department does not exist").build();
        }

        List deptList = studentRepository.findLevelByDepartmentAndStudent(adminFindStudentRequest.getDeptName());

        if (deptList==null || deptList.isEmpty())
        {
            throw new GenericException(ResponseCodes.NOT_FOUND, "There are no students in these department", HttpStatus.NOT_FOUND);


//            return AdminFindStudentResponse
//                    .builder()
//                    .response("Failed")
//                    .message("There are no students in these department")
//                    .code("99")
//                    .build();
        }

        return AdminFindStudentResponse.builder()
                .response("Successful")
                .code("00")
                .message("These are the number of students in the department by level")
                .list(deptList)
                .build();

    }

    @Override
    public AdminFindStudentResponse showCountLevelByDepartment(String deptName) {
        Department dept = departmentRepository.findByDeptName(deptName);

        if (dept==null){
            throw new GenericException(ResponseCodes.NOT_FOUND, "The Department does not exist", HttpStatus.NOT_FOUND);

        }

        List deptList = studentRepository.findLevelByDepartmentAndStudent(deptName);

        if (deptList==null || deptList.isEmpty())
        {
            throw new GenericException(ResponseCodes.NOT_FOUND, "There are no students in these department", HttpStatus.NOT_FOUND);
        }

        return AdminFindStudentResponse.builder()
                .response("Successful")
                .code("00")
                .message("These are the number of students in the department by level")
                .list(deptList)
                .build();
    }

    @Override
    public AdminStudentGeneralResponse ShowCountNewStudents() {

       Long newStudent = studentRepository.findNewStudentByGivenDate();

        if (newStudent == 0){
            throw new GenericException(ResponseCodes.NOT_FOUND, "There are 0 students available from after date", HttpStatus.NOT_FOUND);
        }

       return AdminStudentGeneralResponse.builder()
               .code("00")
               .count(newStudent)
               .message("These are the number of students after 2022-10-17")
               .response("Successful")
               .build();
    }

    public AdminStudentGeneralResponse ShowCountOldStudents() {
        Long newStudent = studentRepository.findOldStudentByGivenDate();

        if (newStudent == 0){
            throw new GenericException(ResponseCodes.NOT_FOUND, "There are 0 students available from before date", HttpStatus.NOT_FOUND);
        }
        return AdminStudentGeneralResponse.builder()
                .code("00")
                .count(newStudent)
                .message("These are the number of students before 2022-10-17")
                .response("Successful")
                .build();
    }

}




