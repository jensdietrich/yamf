package nz.ac.wgtn.yamf.checks.jbytecode.descr;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DescriptorUtil {

    public static String constructBytecodeDescriptor(Class[] paramTypes, Class returnType)  {
        List<String> params = Stream.of(paramTypes)
            .map(t -> encodeArray(t))
            .collect(Collectors.toList());
        return constructBytecodeDescriptor(params,returnType.getName());
    }

    private static String encodeArray (Class type) {
        return (type.isArray())? "[" + encodeArray(type.getComponentType()) : type.getName();
    }

    public static String constructBytecodeDescriptor(List<String> paramTypes, String returnType)  {
        String descriptor = paramTypes.stream()
                .map(t -> convertTypeNameToInternal(t))
                .collect(Collectors.joining("","(",")"));
        return descriptor + convertTypeNameToInternal(returnType);
    }

    /**
     * Convert types names back to the syntax used in descriptors.
     * Used when using reflection to construct meta objects.
     * @param type
     * @return
     */
    public static String convertTypeNameToInternal(String type) {
        if (Objects.equals(type,"void")) {
            return "V";
        }
        if (Objects.equals(type,"int")) {
            return "I";
        }
        if (Objects.equals(type,"boolean")) {
            return "Z";
        }
        if (Objects.equals(type,"char")) {
            return "C";
        }
        if (Objects.equals(type,"byte")) {
            return "B";
        }
        if (Objects.equals(type,"short")) {
            return "S";
        }
        if (Objects.equals(type,"float")) {
            return "F";
        }
        if (Objects.equals(type,"long")) {
            return "J";
        }
        if (Objects.equals(type,"double")) {
            return "D";
        }
        if (type.startsWith("[")) {
            return "[" + convertTypeNameToInternal(type.substring(1));
        }
        else {
            return "L" + type.replace(".","/") + ';';
        }
    }
}
