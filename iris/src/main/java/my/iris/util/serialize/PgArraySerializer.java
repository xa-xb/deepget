package my.iris.util.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.hibernate.type.SqlTypes;
import org.postgresql.jdbc.PgArray;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

public class PgArraySerializer extends JsonSerializer<PgArray> {
    static final int[] DOUBLE_TYPES;
    static {
        var doubleTypes = new int[]{SqlTypes.DOUBLE, SqlTypes.FLOAT, SqlTypes.REAL};
        Arrays.sort(doubleTypes);
        DOUBLE_TYPES = doubleTypes;
    }

    /**
     * only support integer[] bigint[]
     *
     * @param pgArray            pgArray
     * @param jsonGenerator      jsonGenerator
     * @param serializerProvider serializerProvider
     * @throws IOException IOException
     */
    @Override
    public void serialize(PgArray pgArray, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        Object arrayObj;
        try {
            arrayObj = pgArray.getArray();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        int baseType;
        try {
            baseType = pgArray.getBaseType();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        switch (arrayObj) {
            case Number[] numArray -> {
                boolean isDouble = Arrays.binarySearch(DOUBLE_TYPES, baseType) >= 0;
                if (isDouble) {
                    double[] arr = new double[numArray.length];
                    for (int i = 0; i < numArray.length; i++) {
                        arr[i] = numArray[i].doubleValue();
                    }
                    jsonGenerator.writeArray(arr, 0, arr.length);
                } else {
                    long[] arr = new long[numArray.length];
                    for (int i = 0; i < numArray.length; i++) {
                        arr[i] = numArray[i].longValue();
                    }
                    jsonGenerator.writeArray(arr, 0, arr.length);
                }
            }
            case String[] stringArray -> {
                jsonGenerator.writeArray(stringArray, 0, stringArray.length);
            }
            default -> {
                throw new RuntimeException("unsupported type" + arrayObj.getClass());
            }
        }

    }
}

