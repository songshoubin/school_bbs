/**
 * 
 */
package com.example.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.lang.Result;
import com.example.util.UploadUtil;

/**
 * @author song
 *
 */

@Controller
public class FileController {
	
	@Autowired
    UploadUtil uploadUtil;
	
	//上传头像
    @ResponseBody
    @PostMapping("/api/upload")
    public Map uploadAvatar(@RequestParam(value = "file") MultipartFile file,HttpServletRequest request) throws IOException {
    	Result result = uploadUtil.upload(UploadUtil.type_post, file);
    	String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() 
    							+ request.getContextPath() +result.getData();
    	System.out.println(returnUrl);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("url", returnUrl);
    	map.put("status", 0);
        return map;
    }

}
