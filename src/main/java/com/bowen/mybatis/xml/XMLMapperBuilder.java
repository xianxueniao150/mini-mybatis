package com.bowen.mybatis.xml;

import com.bowen.mybatis.constant.Constant;
import com.bowen.mybatis.entity.Configuration;
import com.bowen.mybatis.entity.MappedStatement;
import com.bowen.mybatis.enums.SqlType;
import com.bowen.mybatis.parsing.GenericTokenParser;
import com.bowen.mybatis.parsing.ParameterMappingTokenHandler;
import com.bowen.mybatis.util.CommonUtis;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;

/**
 * @author: zhaobowen
 * @create: 2020-07-27 11:38
 * @description:
 **/
@Slf4j
public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration=configuration;
    }

    public  void readMapperXml(String filePath) {
        URL resources = XMLMapperBuilder.class.getClassLoader().getResource(filePath);

        File file = new File(resources.getFile());

        // 创建一个读取器
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding(Constant.CHARSET_UTF8);

        // 读取文件内容
        Document document = null;
        try {
            document = saxReader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // 获取xml中的根元素
        Element rootElement = document.getRootElement();

        // 不是beans根元素的，文件不对
        if (!Constant.XML_ROOT_LABEL.equals(rootElement.getName())) {
            throw new RuntimeException("mapper xml文件根元素不是mapper");
        }

        String namespace = rootElement.attributeValue(Constant.XML_SELECT_NAMESPACE);

        for (Element element : rootElement.elements()) {
            String eleName = element.getName();

            MappedStatement statement = new MappedStatement();

            if (SqlType.SELECT.value().equals(eleName)) {
                String resultType = element.attributeValue(Constant.XML_SELECT_RESULTTYPE);
                statement.setResultType(resultType);
                statement.setSqlCommandType(SqlType.SELECT);
            } else if (SqlType.UPDATE.value().equals(eleName)) {
                statement.setSqlCommandType(SqlType.UPDATE);
            } else {
                // 其他标签自己实现
                System.err.println("不支持此xml标签解析:" + eleName);
            }

            //设置SQL的唯一ID
            String sqlId = namespace + "." + element.attributeValue(Constant.XML_ELEMENT_ID);

            statement.setSqlId(sqlId);
            statement.setNamespace(namespace);
            String originalSql = CommonUtis.stringTrim(element.getStringValue());
            ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler(statement);
            GenericTokenParser parser = new GenericTokenParser("#{", "}",tokenHandler);
            String sql = parser.parse(originalSql);
            statement.setSql(sql);

            configuration.addMappedStatement(sqlId, statement);
            log.debug("statement:{}", statement);
        }
    }
}
