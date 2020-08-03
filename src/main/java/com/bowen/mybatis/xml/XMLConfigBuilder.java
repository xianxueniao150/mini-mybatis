package com.bowen.mybatis.xml;

import com.bowen.mybatis.constant.Constant;
import com.bowen.mybatis.entity.Configuration;
import com.bowen.mybatis.entity.MyDataSource;
import com.bowen.mybatis.entity.TypeHandlerRegistry;
import com.bowen.mybatis.plugin.Interceptor;
import com.bowen.mybatis.util.ClassUtil;
import com.bowen.mybatis.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;

/**
 * @author: zhaobowen
 * @create: 2020-07-26 22:14
 * @description:
 **/
@Slf4j
public class XMLConfigBuilder {

    private Configuration configuration;



    public XMLConfigBuilder(Configuration configuration) {
        this.configuration=configuration;
    }
    public  void buildConfig(String fileName) {

        InputStream inputStream = XMLConfigBuilder.class.getClassLoader().getResourceAsStream(fileName);

        // 创建一个读取器
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding(Constant.CHARSET_UTF8);

        // 读取文件内容
        Document document = null;
        try {
            document = saxReader.read(inputStream);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        // 获取xml中的根元素
        Element rootElement = document.getRootElement();

        // 不是beans根元素的，文件不对
        if (!Constant.XML_CONFIG_ROOT_LABEL.equals(rootElement.getName())) {
            throw new RuntimeException("config xml文件根元素错误");
        }

        String namespace = rootElement.attributeValue(Constant.XML_CONFIG_ROOT_LABEL);

        for (Element element : rootElement.elements()) {
            String eleName = element.getName();
            switch (eleName){
                case "typeHandlers" :
                    typeHandlerElement(element);
                    break;
                case "environments" :
                    environmentsElement(element);
                    break;
                case "mappers"  :
                    mapperElement(element);
                    break;
                case "plugins" :
                    pluginElement(element);
                default:
                    break;
            }
        }
    }

    private void pluginElement(Element element) {
        Iterator<Element> plugin = element.elementIterator("plugin");
        while(plugin.hasNext()){
            Element plugEle = plugin.next();
            String interceptor = plugEle.attributeValue("interceptor");
            Interceptor interceptorClass = (Interceptor) ReflectionUtil.newInstance(interceptor);
            configuration.addInterceptor(interceptorClass);
        }
    }

    private  void mapperElement(Element element) {
        XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(configuration);
        Iterator<Element> mapper = element.elementIterator("mapper");
        while(mapper.hasNext()){
            Element mapperEle = mapper.next();
            xmlMapperBuilder.readMapperXml(mapperEle.attributeValue("resource"));
        }
    }

    private   void environmentsElement(Element element) {
        MyDataSource myDataSource = new MyDataSource();
        Iterator<Element> property = element.elementIterator("property");
        while(property.hasNext()){
            Element propEle = property.next();
            String name = propEle.attributeValue("name");
            String value = propEle.attributeValue("value");
            switch (name){
                case "url" :
                    myDataSource.setUrl(value);
                    break;
                case "username" :
                    myDataSource.setUsername(value);
                    break;
                case "password"  :
                    myDataSource.setPassword(value);
                    break;
                default:
                    break;
            }
        }
        configuration.setDataSource(myDataSource);
    }


    private  void typeHandlerElement(Element element) {
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        Iterator<Element> typeHandler = element.elementIterator("typeHandler");
        while(typeHandler.hasNext()){
            Element handler = typeHandler.next();
            String handlerTypeName = handler.attributeValue("handler");
            Class<?> typeHandlerClass = ClassUtil.loadClass(handlerTypeName);
            typeHandlerRegistry.register(typeHandlerClass);
        }
    }
}
