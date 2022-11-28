package com.ecobank.srms.Service;

import com.ecobank.srms.dto.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface BioMedDataService {


    BioMedDataResponse save(BioMedDataRequest bioMedDataRequest) throws IOException;

    BioMedDataResponse update(BioMedDataRequest bioMedDataRequest) throws IOException;

   ProfileResponse display(BioMedDataRequest bioMedDataRequest) throws Exception;

    File storeImage(MultipartFile img, String Filecat) throws IOException;

    BioMedDataResponse upload(UploadPictureRequest uploadPictureRequest)throws IOException;



    DisplayPictureResponse displayPic(DisplayPictureRequest displayPictureRequest) throws IOException;

}
