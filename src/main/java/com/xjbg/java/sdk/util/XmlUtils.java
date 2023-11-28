package com.xjbg.java.sdk.util;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * xml处理工具类
 *
 * @author kesc
 * @since 2018-08-10 14:29
 */
@Slf4j
public class XmlUtils {

    public static String format(Object obj) throws IOException, JAXBException {
        return format(obj, obj.getClass());
    }

    public static String format(Object obj, Class<?>... classesToBeBound) throws IOException, JAXBException {
        return format(obj, Boolean.FALSE, classesToBeBound);
    }

    public static String formatPretty(Object obj) throws IOException, JAXBException {
        return formatPretty(obj, obj.getClass());
    }

    public static String formatPretty(Object obj, Class<?>... classesToBeBound) throws IOException, JAXBException {
        return format(obj, Boolean.TRUE, classesToBeBound);
    }

    private static String format(Object obj, boolean pretty, Class<?>... classesToBeBound) throws IOException, JAXBException {
        try (StringWriter writer = new StringWriter()) {
            JAXBContext context = JAXBContext.newInstance(classesToBeBound);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, pretty);
            marshaller.marshal(obj, writer);
            return writer.toString();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(String xml, Class<T> targetClass) throws JAXBException {
        return (T) parse(xml, new Class<?>[]{targetClass});
    }

    public static Object parse(String xml, Class<?>... classesToBeBound) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(classesToBeBound);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return unmarshaller.unmarshal(new StringReader(xml));
    }
}
