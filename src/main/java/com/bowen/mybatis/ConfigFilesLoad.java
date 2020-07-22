package com.bowen.mybatis;

import com.bowen.mybatis.constant.Constant;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.util.CommonUtis;
import com.bowen.mybatis.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Slf4j
public class ConfigFilesLoad {

    private static Map<String, MappedStatement> mappedStatements = new HashMap<>();
    private static Properties properties = new Properties();


    public static MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }

    /**
     * 获取字符型属性(默认值为空字符串)
     *
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        return getProperty(key, "");
    }

    /**
     * 获取字符型属性(可指定默认值)
     *
     * @param key
     * @param defaultValue
     *            默认值
     * @return
     */
    public static String getProperty(String key, String defaultValue) {

        return properties.containsKey(key) ? properties.getProperty(key) : defaultValue;
    }

    public static void loadFile(String fileName) {
        InputStream inputStream = ConfigFilesLoad.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dirName = properties.getProperty(Constant.MAPPER_LOCATION).replaceAll("\\.", "/");
        URL resources = ConfigFilesLoad.class.getClassLoader().getResource(dirName);

        File mappersDir = new File(resources.getFile());
        if (mappersDir.isDirectory()) {

            // 显示包下所有文件
            File[] mappers = mappersDir.listFiles();
            if (CommonUtis.isNotEmpty(mappers)) {
                for (File file : mappers) {
                    if (file.getName().endsWith(Constant.MAPPER_FILE_SUFFIX)){
                        // 只对XML文件解析
                        XmlUtil.readMapperXml(file,mappedStatements);
                    }
                }

            }
        }
    }

//
//
//    public static void main(String[] args) {
//        loadFile("conf.properties");
//    }
}
