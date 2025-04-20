package com.hdu.hduvboard.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface VbSysFileService {
    public String saveVerilogFile(HttpServletRequest request, MultipartFile verilogFile) throws IOException;

    public String saveBindFile(HttpServletRequest request, MultipartFile bindFile) throws IOException;
}
