package com.bowen.mybatis.constant;

public interface Constant {

    /******** 在properties文件中配置信息 **************/
    String MAPPER_LOCATION = "mapper.location";

    String DB_DRIVER_CONF = "db.driver";

    String DB_URL_CONF = "db.url";

    String DB_USERNAME_CONF = "db.username";

    String db_PASSWORD = "db.password";

    String XML_CONFIG_ROOT_LABEL = "configuration";

    String XML_CONFIG_TYPEHANDLERS = "typeHandlers";

    String XML_CONFIG_TYPEHANDLER = "typeHandler";


    /************ mapper xml  ****************/

    String CHARSET_UTF8 = "UTF-8";

    String MAPPER_FILE_SUFFIX = ".xml";

    String XML_ROOT_LABEL = "mapper";

    String XML_SELECT_NAMESPACE = "namespace";

    String XML_SELECT_RESULTTYPE = "resultType";

    String XML_ELEMENT_ID = "id";

}
