package com.kakaopay.homework.internetbanking.utility.csv;

import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.Data;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class CsvRow {

    private static final int MIN_POSITION = 0;

    private static final int MAX_POSITION = 6;

    @CsvCustomBindByPosition(converter = NumberConverter.ShortConverter.class, position = 0)
    private short year;

    @CsvCustomBindByPosition(converter = NumberConverter.DoubleConverter.class, position = 1)
    private double rate;

    @CsvCustomBindByPosition(converter = NumberConverter.DoubleConverter.class, position = 2)
    private double smartPhoneRate;

    @CsvCustomBindByPosition(converter = NumberConverter.DoubleConverter.class, position = 3)
    private double desktopRate;

    @CsvCustomBindByPosition(converter = NumberConverter.DoubleConverter.class, position = 4)
    private double notebookRate;

    @CsvCustomBindByPosition(converter = NumberConverter.DoubleConverter.class, position = 5)
    private double etcRate;

    @CsvCustomBindByPosition(converter = NumberConverter.DoubleConverter.class, position = 6)
    private double smartPadRate;

    public Number getValue (int position) {
        if (position < MIN_POSITION ||  position > MAX_POSITION) return null;

        Class<? extends CsvRow> clazz = this.getClass();

        Field[] fields = clazz.getDeclaredFields();

        for (Field f : fields) {
            if (f.isAnnotationPresent(CsvCustomBindByPosition.class) == false) continue;

            CsvCustomBindByPosition annotation = f.getAnnotation(CsvCustomBindByPosition.class);
            if (annotation.position() != position) continue;

            f.setAccessible(true);

            try {
                return (Number) f.get(this);
            }
            catch (Exception e) {}
        }

        return null;
    }

    public Map.Entry<String, Number> getValue (int position, List<String> headers) {
        if (position < MIN_POSITION ||  position > MAX_POSITION) return null;

        if (position >= headers.size()) return null;

        if (headers.size() - 1 != MAX_POSITION) return null;

        final String key = headers.get(position);
        final Number value = getValue(position);

        return new Map.Entry<String, Number>() {

            @Override
            public String getKey() {
                return key;
            }

            @Override
            public Number getValue() {
                return value;
            }

            @Override
            public Number setValue(Number value) {
                return null;
            }
        };
    }
}
