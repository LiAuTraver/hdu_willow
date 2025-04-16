package com.hdu.hdufpga.service.impl;

import cn.hutool.core.io.FileUtil;
import com.hdu.hdufpga.exception.InvalidFileSuffixException;
import com.hdu.hdufpga.service.VbSysFileService;
import com.hdu.hdufpga.util.VbSysFileUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class VbSysFileServiceImpl implements VbSysFileService {
    public String saveVerilogFile(HttpServletRequest request, MultipartFile verilogFile) throws IOException {
        String originalFileName = verilogFile.getOriginalFilename();
        if (originalFileName == null) {
            throw new IOException("文件为空");
        }
        String verilogPattern = ".*?\\.v$";
        // 进行文件名校验，确认上传的是后缀为v的文件
        if (originalFileName.matches(verilogPattern)) {
            String token = request.getHeader("token");
            String filePath = VbSysFileUtil.getSavePath(token) + "/" + originalFileName;
            FileUtil.del(filePath);
            VbSysFileUtil.saveFile(verilogFile, filePath);
            return filePath;
        } else {
            throw new InvalidFileSuffixException("文件后缀不为.v");
        }
    }

    public String saveBindFile(HttpServletRequest request, MultipartFile bindFile) throws IOException {
        String originalFileName = bindFile.getOriginalFilename();
        if (originalFileName == null) {
            throw new IOException("文件为空");
        }
        String verilogPattern = ".*?\\.json$";
        // 进行文件名校验，确认上传的是后缀为v的文件
        if (originalFileName.matches(verilogPattern)) {
            String token = request.getHeader("token");
            String filePath = VbSysFileUtil.getSavePath(token) + "/" + originalFileName;
            FileUtil.del(filePath);
            VbSysFileUtil.saveFile(bindFile, filePath);
            return filePath;
        } else {
            throw new InvalidFileSuffixException("文件后缀不为.json");
        }
    }
}
