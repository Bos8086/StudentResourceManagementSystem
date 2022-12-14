package com.ecobank.srms.controllers;
import com.ecobank.srms.Service.TeacherService;
import com.ecobank.srms.dto.AdminRegisterRequest;
import com.ecobank.srms.dto.TeacherLoginRequest;
import com.ecobank.srms.dto.TeacherRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("api/v1/teacher")
public class TeacherController {
    @Autowired
    TeacherService teacherService;

    @PostMapping(value = "/register")
    public ResponseEntity register(@Valid @RequestBody TeacherRegisterRequest teacherRegisterRequest) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.register(teacherRegisterRequest));
    }

    @PostMapping(value = "/login")
    public ResponseEntity login(@Valid @RequestBody TeacherLoginRequest teacherLoginRequest) throws IOException {
        return ResponseEntity.ok(teacherService.login(teacherLoginRequest));
    }
}
