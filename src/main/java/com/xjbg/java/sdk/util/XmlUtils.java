package com.xjbg.java.sdk.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * xml处理工具类
 *
 * @author: huangpp02
 * @time: 2018-08-10 14:29
 */
@Slf4j
public class XmlUtils {

    public static String format(Object obj) {
        try (StringWriter writer = new StringWriter()) {
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(obj, writer);
            return writer.toString();
        } catch (Exception e) {
            log.warn("格式化失败：", e);
            return null;
        }
    }

    public static <T> T parse(String xml, Class<T> targetClass) {
        try {
            JAXBContext context = JAXBContext.newInstance(targetClass);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (Exception e) {
            log.warn("反序列化失败：", e);
            return null;
        }
    }
}
