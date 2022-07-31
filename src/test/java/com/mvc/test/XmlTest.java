package com.mvc.test;

import com.mvc.xml.XmlParser;
import org.junit.Test;

/**
 * @Program: simplify_mvc
 * @Description:
 * @Author: ZJamss
 * @Create: 2022-07-31 14:05
 **/
public class XmlTest {
    @Test
    public void readXml(){
        System.out.println(XmlParser.getBasePackage("mvc.xml"));
    }
}
