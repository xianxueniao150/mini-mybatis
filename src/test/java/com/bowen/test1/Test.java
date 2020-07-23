package com.bowen.test1;

/**
 * @author: zhaobowen
 * @create: 2020-07-23 10:42
 * @description:
 **/
public class Test {
    public static void main(String[] args) {
        String string = "aaa ; ddd ; ";
        StringBuilder builder = new StringBuilder(string);
        int lastIndexOf = builder.lastIndexOf("; ");
        System.out.println(lastIndexOf);
        System.out.println(builder.length()-1);
        if(lastIndexOf!=-1 && lastIndexOf==builder.length()-2){
            builder.deleteCharAt(lastIndexOf);
        }
        System.out.println(builder.toString());


    }


}
