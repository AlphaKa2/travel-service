package com.alphaka.travelservice;

import java.lang.reflect.Field;

public class TestUtil {
    public static void setField(Object target, String fieldName, Object value) {
        try {
            // 상위 클래스까지 탐색하여 필드 찾기
            Field field = getField(target.getClass(), fieldName);
            if (field == null) {
                throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy.");
            }
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("필드 설정에 실패했습니다.", e);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // 상위 클래스로 이동
            }
        }
        return null;
    }
}
