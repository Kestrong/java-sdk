package com.xjbg.java.sdk.customize.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xjbg.java.sdk.enums.DatePatternEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @author kesc
 * @since 2019/5/15
 */
@Slf4j
public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        init();
    }

    private void init() {
        try {
            configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
            configure(SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL, true);
        } catch (Error var6) {
            log.error("SerializationFeature.WRITE_SELF_REFERENCES_AS_NULL not support your version, please upgrade jackson >=2.11");
        }

        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        setDateFormat(new SimpleDateFormat(DatePatternEnum.YYYYMMDDHHMMSS_BYSEP.getFormat()));
        SimpleModule module = new SimpleModule();
        module.addSerializer(BigDecimal.class, ToStringSerializer.instance);
        module.addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                return StringUtils.trim(jp.getValueAsString());
            }
        });
        registerModules(module);
    }

}
