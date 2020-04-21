package com.yzly.api.common;

/**
 * @Description:TODO
 * @Auther tanyuan
 * version V1.0
 **/

import com.yzly.api.annotations.*;
import com.yzly.api.constants.xiecheng.XmlRequest;
import com.yzly.api.exception.YzException;
import com.yzly.api.factories.xiecheng.XmlRequestFactory;
import com.yzly.api.models.Body;
import com.yzly.api.util.CommonUtil;
import com.yzly.api.util.XmlUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *@ClassName XmlRequestHandler
 *@Description 处理将实体类转换为ESBxml报文
 *@Auth frank
 *@Date 2019-11-18 10:30
 *@Version 1.0
 **/
public class XmlRequestHandler<T> {

    private XmlRequestHandler() {
    }

    private static class ESBXmlHandlerInstance {
        private final static XmlRequestHandler instance = new XmlRequestHandler();
    }

    public static XmlRequestHandler getInstance() {
        return ESBXmlHandlerInstance.instance;
    }


    private static Logger logger = LoggerFactory.getLogger(XmlRequestHandler.class);

    public XmlRequest transform(String xml) throws YzException {
        XmlRequest XmlRequest = XmlRequestFactory.getInstance().createXmlRequest();
        try {
            XmlUtils.parseXmlToObject(XmlRequest,xml);
        }catch (Exception e) {
            logger.error(e.getMessage());
            throw new YzException(e.getMessage());
        }
        return XmlRequest;
    }

    public String parseToXml(XmlRequest XmlRequest) throws YzException {
        try {
            // 创建Document
            Document document = DocumentHelper.createDocument();
            // 创建一个根节点
            Element rootElement = document.addElement("soap:Envelope");
            Field[] fields = XmlRequest.getClass().getDeclaredFields();
            for(Field nodeField : fields) {
                if(nodeField.isAnnotationPresent(Header.class)) {
                    createHeader(nodeField, rootElement, XmlRequest);
                }
            }
            String xml = document.asXML();
            logger.info("parse to XML---------------->" + xml);
            return xml;
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
            throw new YzException(e.getMessage());
        }
    }

    private Element createHeader(Field field, Element rootElement, Object object) throws YzException, IllegalAccessException {
        String value = field.getAnnotation(Header.class).value().getValue();
        Element header = rootElement.addElement(value);
        if(field.isAnnotationPresent(XMLData.class)) {
            createData(field, header, object);
        }
        return header;

    }
    private Element createData(Field field, Element rootElement, Object object) throws YzException, IllegalAccessException {
        String elementName = CommonUtil.getElementName(field);
        Element data = rootElement;
        if(!(field.isAnnotationPresent(Header.class)&&field.getAnnotation(Header.class).value() == HeaderType.BODY)){
            data = rootElement.addElement("data");
            data.addAttribute("name", elementName);
        }
        field.setAccessible(true);
        if (field.isAnnotationPresent(Fields.class)) {
            createField(field, data, object);
            return data;
        }
        if (field.get(object) == null) {
            return rootElement;
        }
        if(field.get(object).getClass().isAnnotationPresent(Array.class) ||
                field.get(object) instanceof Collection) {
            if (field.get(object) instanceof Body) {
                createArray(data, field.get(object));
            }else{
                createBodyArray(field, object, data);
            }
            return data;
        }
//        if(field.get(object).getClass().isAnnotationPresent(Struct.class)) {
        createStruct(data, field.get(object));
//            return data;
//        }
        return data;

    }

    private void createBodyArray(Field field, Object object, Element data) throws IllegalAccessException, YzException {
        Object list = field.get(object);
        if(list instanceof Collection){
            Collection c = (Collection)list;
            Type type = field.getGenericType();
            if (null == type) {
                return;
            }
            ParameterizedType pt = (ParameterizedType) type;
            Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0];
            Element array = data.addElement("array");
            Iterator it = c.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                createStruct(array, obj);
            }
        }
    }

    private Element createStruct(Element rootElement, Object object) throws YzException, IllegalAccessException {
        Element struct = rootElement;
        if (object.getClass().isAnnotationPresent(Struct.class)) {
            struct = rootElement.addElement("struct");
        }
        Field[] fields = object.getClass().getDeclaredFields();
        for(Field field : fields) {
            if(field.isAnnotationPresent(XMLData.class)) {
                createData(field, struct, object);
            }
        }
        return struct;
    }

    private void createField(Field field, Element element, Object object) throws IllegalAccessException{
        Element nodeField = element.addElement("field");
        if (-1 < field.getAnnotation(Fields.class).length()) {
            nodeField.addAttribute("length", String.valueOf(field.getAnnotation(Fields.class).length()));
        }
        if (-1 < field.getAnnotation(Fields.class).scale()) {
            nodeField.addAttribute("scale", String.valueOf(field.getAnnotation(Fields.class).scale()));
        }
        if (!"".equals(field.getAnnotation(Fields.class).type())) {
            nodeField.addAttribute("type", field.getAnnotation(Fields.class).type());
        }
        field.setAccessible(true);
        Object obj = field.get(object);
        nodeField.setText(obj == null ? "" : obj.toString());
    }
    private void createArray(Element element, Object object) throws IllegalAccessException, YzException {
        Element array = element.addElement("array");
        Field field = object.getClass().getDeclaredFields()[0];
        field.setAccessible(true);
        if(field.get(object) instanceof List) {
            for(Object obj : (List)field.get(object)) {
                if (obj.getClass().isAnnotationPresent(Struct.class)) {
                    createStruct(array, obj);
                }
            }
        } else {
            logger.error("类型转换失败，属性[#{}]必须是[java.util.List<>]类型", field.getName());
            throw new YzException("类型转换失败，属性[{}]必须是[{}]类型");
        }
    }
}

