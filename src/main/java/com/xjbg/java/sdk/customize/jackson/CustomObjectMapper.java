package com.xjbg.java.sdk.customize.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xjbg.java.sdk.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author kesc
 * @since 2019/5/15
 */
public class CustomObjectMapper extends ObjectMapper {
    private boolean camelCaseToLowerCaseWithUnderscores = false;
    private String dateFormatPattern;

    public void setCamelCaseToLowerCaseWithUnderscores(boolean camelCaseToLowerCaseWithUnderscores) {
        this.camelCaseToLowerCaseWithUnderscores = camelCaseToLowerCaseWithUnderscores;
    }

    public void setDateFormatPattern(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }

    public CustomObjectMapper() {
        //jason转java时忽略不用的jason属性
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CustomObjectMapper(boolean camelCaseToLowerCaseWithUnderscores) {
        //jason转java时忽略不用的jason属性
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (camelCaseToLowerCaseWithUnderscores) {
            setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        }
    }

    public void init() {
        // 排除值为空属性
//        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 进行缩进输出
        configure(SerializationFeature.INDENT_OUTPUT, true);
        // 将驼峰转为下划线
        if (camelCaseToLowerCaseWithUnderscores) {
            setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
        }
        // 进行日期格式化
        if (StringUtil.isNotEmpty(dateFormatPattern)) {
            DateFormat dateFormat = new SimpleDateFormat(dateFormatPattern);
            setDateFormat(dateFormat);
        }
        SimpleModule module = new CustomerModule();
        module.addSerializer(BigDecimal.class, new ToStringSerializer());
        registerModule(module);


    }

    class CustomerModule extends SimpleModule {

        public CustomerModule() {
            addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
                @Override
                public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                        JsonProcessingException {
                    return StringUtils.trim(jp.getValueAsString());
                }
            });
        }
    }
}
