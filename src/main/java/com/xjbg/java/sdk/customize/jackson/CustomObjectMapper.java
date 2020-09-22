package com.xjbg.java.sdk.customize.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.xjbg.java.sdk.enums.DatePatternEnum;
import com.xjbg.java.sdk.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * @author kesc
 * @since 2019/5/15
 */
public class CustomObjectMapper extends ObjectMapper {
    private boolean camelCaseToLowerCaseWithUnderscores;
    private String dateFormatPattern;

    public void setCamelCaseToLowerCaseWithUnderscores(boolean camelCaseToLowerCaseWithUnderscores) {
        this.camelCaseToLowerCaseWithUnderscores = camelCaseToLowerCaseWithUnderscores;
    }

    public void setDateFormatPattern(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }

    public CustomObjectMapper() {
        this(false, DatePatternEnum.YYYYMMDDHHMMSS_BYSEP.getFormat());
    }

    public CustomObjectMapper(boolean camelCaseToLowerCaseWithUnderscores, String datePattern) {
        this.camelCaseToLowerCaseWithUnderscores = camelCaseToLowerCaseWithUnderscores;
        this.dateFormatPattern = datePattern;
        init();
    }

    private void init() {
        // 进行缩进输出
        configure(SerializationFeature.INDENT_OUTPUT, true);
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //jason转java时忽略不用的jason属性
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 将驼峰转为下划线
        if (camelCaseToLowerCaseWithUnderscores) {
            setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        } else {
            setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        }
        // 进行日期格式化
        if (StringUtil.isNotEmpty(dateFormatPattern)) {
            setDateFormat(new SimpleDateFormat(dateFormatPattern));
        }
        SimpleModule module = new CustomerModule();
        module.addSerializer(BigDecimal.class, new ToStringSerializer());
        registerModule(module);
    }

    static class CustomerModule extends SimpleModule {

        public CustomerModule() {
            addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
                @Override
                public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
                    return StringUtils.trim(jp.getValueAsString());
                }
            });
        }
    }
}
