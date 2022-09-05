package com.ecobank.srms.controllers;

import com.ecobank.srms.Service.BioMedDataService;
import com.ecobank.srms.Service.StudentService;
import com.ecobank.srms.dto.BioMedDataRequest;
import com.ecobank.srms.dto.BioMedDataResponse;
import com.ecobank.srms.dto.ProfileResponse;
import com.ecobank.srms.dto.StudentRequest;
import com.ecobank.srms.repository.BioMedDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("api/v1/student")
public class BioMedDataController {

        @Autowired
        private BioMedDataService bioMedDataService;


        @PostMapping(value = "/save_biodata")
        public ResponseEntity save(@RequestBody BioMedDataRequest bioMedDataRequest) throws IOException{
            return ResponseEntity.ok(bioMedDataService.save(bioMedDataRequest));
        }

        @PostMapping(value = "/profile")
        public ResponseEntity display(@RequestBody BioMedDataRequest bioMedDataRequest) throws Exception {
                return ResponseEntity.ok(bioMedDataService.display(bioMedDataRequest));
        }

        @PostMapping(value = "/upload_biodata_picture")
        public ResponseEntity upload(@RequestParam("image") MultipartFile bioMedPic) throws IOException {
                return ResponseEntity.ok(bioMedDataService.upload(bioMedPic));
        }


}
