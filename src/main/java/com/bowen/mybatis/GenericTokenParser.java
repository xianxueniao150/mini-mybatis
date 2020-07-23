package com.bowen.mybatis;

import com.bowen.mybatis.entity.MappedStatement;

import java.util.List;

/**
 * @author: zhaobowen
 * @create: 2020-07-23 11:03
 * @description:
 **/
public class GenericTokenParser {
    //有一个开始和结束记号
    private final String openToken;
    private final String closeToken;
    private MappedStatement mappedStatement;

    public GenericTokenParser(String openToken, String closeToken,  MappedStatement mappedStatement ) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.mappedStatement = mappedStatement;
    }


    public String parse(String text) {
        StringBuilder builder = new StringBuilder();
        if (text != null && text.length() > 0) {
            char[] src = text.toCharArray();
            int offset = 0;
            int start = text.indexOf(openToken, offset);
            //#{favouriteSection,jdbcType=VARCHAR}
            //这里是循环解析参数，参考GenericTokenParserTest,比如可以解析${first_name} ${initial} ${last_name} reporting.这样的字符串,里面有3个 ${}
            while (start > -1) {
                int end = text.indexOf(closeToken, start);
                if (end == -1) {
                    builder.append(src, offset, src.length - offset);
                    offset = src.length;
                } else {
                    builder.append(src, offset, start - offset);
                    offset = start + openToken.length();
                    String content = new String(src, offset, end - offset);
                    //得到一对大括号里的字符串后，调用handler.handleToken,比如替换变量这种功能
                    List<String> parameters = mappedStatement.getParameters();
                    parameters.add(content);
                    builder.append("?");
                    offset = end + closeToken.length();
                }
                start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
                builder.append(src, offset, src.length - offset);
            }
        }
        return builder.toString();
    }
}
