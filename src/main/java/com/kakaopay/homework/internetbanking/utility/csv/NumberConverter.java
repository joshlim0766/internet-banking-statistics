package com.kakaopay.homework.internetbanking.utility.csv;

import com.kakaopay.homework.exception.CsvParseException;
import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class NumberConverter<T extends Number> extends AbstractBeanField<T> {

    private final Class<T> clazz;
    private final Method valueOfMethod;

    public static class ShortConverter extends NumberConverter<Short> {
        public ShortConverter() {
            super(Short.class);
        }

        protected Short getDefaultValue () {
            return Short.valueOf((short) 0);
        }
    }

    public static class DoubleConverter extends NumberConverter<Double> {
        public DoubleConverter () {
            super(Double.class);
        }

        protected Double getDefaultValue () {
            return Double.valueOf(0.0f);
        }
    }

    protected NumberConverter (Class<T> clazz) {
        this.clazz = clazz;

        try {
            valueOfMethod = this.clazz.getMethod("valueOf", new Class[]{String.class});
        }
        catch (NoSuchMethodException e) {
            throw new CsvParseException("Failed to parse CSV : " + e.getMessage(), e);
        }
    }

    protected abstract T getDefaultValue ();

    @Override
    protected T convert(String value) throws CsvDataTypeMismatchException, CsvConstraintViolationException {
        T result = getDefaultValue();

        try {
            result = clazz.cast(valueOfMethod.invoke(null, new Object[] {value}));
        } catch (IllegalAccessException | InvocationTargetException e) {
            if ((e instanceof InvocationTargetException) == false ||
                    (e.getCause() instanceof NumberFormatException) == false) {
                throw new CsvParseException("Failed to parse CSV : " + e.getMessage(), e);
            }
        }

        return result;
    }
}
