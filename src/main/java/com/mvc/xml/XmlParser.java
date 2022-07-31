package com.mvc.xml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

/**
 * @Program: simplify_mvc
 * @Description: xml解析器
 * @Author: ZJamss
 * @Create: 2022-07-31 14:01
 **/
public class XmlParser {

    //解析mvc.xml获取扫描路径
    public static String getBasePackage(String path) {
        SAXReader saxReader = new SAXReader();
        final InputStream inputStream = XmlParser.class.getClassLoader().getResourceAsStream(path);
        try {
            final Document document = saxReader.read(inputStream);
            final Element rootElement = document.getRootElement();
            final Element componentsScan = rootElement.element("components-scan");
            final Attribute attribute = componentsScan.attribute("base-package");
            return attribute.getText();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }
}
