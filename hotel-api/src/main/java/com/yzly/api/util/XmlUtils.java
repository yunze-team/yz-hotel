package com.yzly.api.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzly.api.annotations.Array;
import com.yzly.api.annotations.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.*;
import java.util.List;

public class XmlUtils {
    public static void parseXmlToObject(Object o,String xml) throws Exception {
        Document document = DocumentHelper.parseText(xml);
        Element root = document.getRootElement();
        Field[] responseFields = o.getClass().getDeclaredFields();
        for(Field responseField : responseFields) {
            if(responseField.isAnnotationPresent(Header.class)) {
                Element header = root.element(responseField.getAnnotation(Header.class).value().getValue());
                if (header == null) {
                    continue;
                }
                responseField.setAccessible(true);
                //解析body数据
                if (responseField.getAnnotation(Header.class).value() == HeaderType.BODY) {
                    parseBody(header, responseField, o);
                    continue;
                }
                Object obj = responseField.get(o);
                Field [] headerFields = obj.getClass().getDeclaredFields();
                Element data = header.element("data");
                Element struct = null;
                List<Element> headerElements = null;
                if(null != data) {
                    struct = data.element("struct");
                }
                if(null != struct) {
                    headerElements = header.element("data").element("struct").elements();
                }
                if(null != headerElements) {
                    for (Element headerElement : headerElements) {
                        for (Field headerField : headerFields) {
                            if (headerField.isAnnotationPresent(XMLData.class) && headerField.isAnnotationPresent(Fields.class)) {
                                initField(headerField, obj, headerElement);
                            }
                            headerField.setAccessible(true);
                            if(null != headerField.get(obj)) {
                                if (headerField.isAnnotationPresent(XMLData.class) && headerField.get(obj).getClass().isAnnotationPresent(Array.class)) {
                                    initArray(headerField, obj, headerElement);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static  void initField(Field field, Object object, Element element) throws IllegalAccessException{
        field.setAccessible(true);
        if (element.attributeValue("name").equals(field.getAnnotation(XMLData.class).value())) {
            Element fieldElemnet = element.element("field");
            if (null != fieldElemnet && null != fieldElemnet.getStringValue()) {
                field.set(object, fieldElemnet.getTextTrim());
            }
        }
    }

    public static  void initArray(Field field, Object obj, Element element) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if(null != element.element("array")) {
            field.setAccessible(true);
            Object dataObject = field.get(obj);
            Field arrayField = dataObject.getClass().getDeclaredFields()[0];
            Element arrayElement = element.element("array");
            arrayField.setAccessible(true);
            Object arrayObject = arrayField.get(dataObject);
            if (null != arrayField && arrayObject instanceof List) {
                Type type = arrayField.getGenericType();
                ParameterizedType pType = (ParameterizedType)type;
                Class clazz = (Class)pType.getActualTypeArguments()[0];
                List<Element> structElements = arrayElement.elements();
                for (Element structElement : structElements) {
                    Object object = clazz.newInstance();
                    List<Element> dataElements = structElement.elements();
                    if (object.getClass().isAnnotationPresent(Struct.class)) {
                        Field[] dataFields = object.getClass().getDeclaredFields();
                        for (Element dataElement : dataElements) {
                            for (Field dataField : dataFields) {
                                if (dataField.isAnnotationPresent(XMLData.class) && dataField.isAnnotationPresent(Fields.class)) {
                                    initField(dataField, object, dataElement);
                                }
                            }
                        }
                    }
                    Method method = arrayObject.getClass().getMethod("add", Object.class);
                    method.invoke(arrayObject, object);
                }
            }
        }
    }

    public static  void initStruct() {


    }

    public static  void parseBody(Element header,Field responseField,Object esbXmlResponse) throws IllegalAccessException {
        JSONObject json = new JSONObject();
        if(header.element("data") == null){
            return;
        }
        Element element = header.element("data").element("struct");
        List<Element> elements = header.elements();
        if(element != null){
            elements = element.elements();
        }
        for (Element data : elements) {
            if(data.element("array") == null){
                json.put(CommonUtil.uppcaseUnderlineToLowerCase(data.attributeValue("name")), data.element("field").getStringValue());
            }else{
                String arrayName = CommonUtil.uppcaseUnderlineToLowerCase(data.attributeValue("name"));
                JSONArray array = new JSONArray();
                List<Element> structs = data.element("array").elements();
                for (Element struct : structs) {
                    JSONObject jsonObject = new JSONObject();
                    List<Element> subDatas = struct.elements();
                    for (Element subData : subDatas) {
                        jsonObject.put(CommonUtil.uppcaseUnderlineToLowerCase(subData.attributeValue("name")), subData.element("field").getTextTrim());
                    }
                    array.add(jsonObject);
                }
                json.put(arrayName,array);
            }
        }
        responseField.set(esbXmlResponse,json);
    }

}
