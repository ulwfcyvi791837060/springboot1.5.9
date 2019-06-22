package com.yyx.aio.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 文件上传 Controller
 * </p>
 *
 * @package: com.xkcoding.upload.controller
 * @description: 文件上传 Controller
 * @author: yangkai.shen
 * @date: Created in 2018/11/6 16:33
 * @copyright: Copyright (c) 2018
 * @version: V1.0
 * @modified: yangkai.shen
 */
@RestController
@RequestMapping("/upload")
public class UploadController {
	@Value("${spring.servlet.multipart.location}")
	private String fileTempPath;

	private Logger log = LoggerFactory.getLogger(getClass());

	@PostMapping(value = "/local", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Dict local(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return Dict.create().set("code", 400).set("message", "文件内容为空");
		}
		String fileName = file.getOriginalFilename();
		String rawFileName = StrUtil.subBefore(fileName, ".", true);
		String fileType = StrUtil.subAfter(fileName, ".", true);
		String localFilePath = StrUtil.appendIfMissing(fileTempPath, "/") + rawFileName + "-" + DateUtil.current(false) + "." + fileType;
		try {
			file.transferTo(new File(localFilePath));
		} catch (IOException e) {
			//D:/spring-boot-aiom-upload/tmp/model-1560489341059.txt
			log.error("【文件上传至本地】失败，绝对路径：{}", localFilePath);
			return Dict.create().set("code", 500).set("message", "文件上传失败");
		}

		log.info("【文件上传至本地】绝对路径：{}", localFilePath);
		return Dict.create().set("code", 200).set("message", "上传成功").set("data", Dict.create().set("fileName", fileName).set("filePath", localFilePath));
	}


}
