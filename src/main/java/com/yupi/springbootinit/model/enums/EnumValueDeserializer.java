package com.yupi.springbootinit.model.enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class EnumValueDeserializer extends JsonDeserializer<Enum<?>> {
    @Override
    public Enum<?> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 获取目标枚举类型
            Class<?> rawClass = ctxt.getContextualType().getRawClass();
            if (!Enum.class.isAssignableFrom(rawClass)) {
                return null;
            }
            
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) rawClass;
            
            // 遍历枚举值，查找匹配的中文描述
            for (Enum<?> enumConstant : enumType.getEnumConstants()) {
                if (enumConstant instanceof BaseEnum) {
                    BaseEnum baseEnum = (BaseEnum) enumConstant;
                    if (baseEnum.getText().equals(value)) {
                        return enumConstant;
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        
        return null;
    }
} 