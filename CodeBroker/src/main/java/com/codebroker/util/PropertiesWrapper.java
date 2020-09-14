package com.codebroker.util;

import jodd.props.Props;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * 配置文件读取包装器.
 */
public class PropertiesWrapper {


    private final Props properties;

    public PropertiesWrapper(Props properties) {
        if (properties == null) {
            throw new NullPointerException("The argument must not be null");
        }
        this.properties = properties;
    }


    public static void checkNull(String variableName, Object value) {
        if (value == null) {
            throw new NullPointerException("The value of " + variableName + " must not be null");
        }
    }

    public Props getProperties() {
        return properties;
    }


    public String getProperty(String name) {
        return properties.getValue(name);
    }

    public String getProperty(String name, String defaultValue) {
        String value = properties.getValue(name);
        return value == null ? defaultValue : value;
    }

    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = properties.getValue(name);
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    public int getIntProperty(String name, int defaultValue) {
        String value = properties.getValue(name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw (NumberFormatException) new NumberFormatException(
                    "The value of the " + name + " property must be a valid " + "int: \"" + value + "\"").initCause(e);
        }
    }

    public int getRequiredIntProperty(String name) {
        String value = properties.getValue(name);
        if (value == null) {
            throw new IllegalArgumentException("The " + name + " property must be specified");
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw (NumberFormatException) new NumberFormatException(
                    "The value of the " + name + " property must be a valid " + "int: \"" + value + "\"").initCause(e);
        }
    }


    public int getIntProperty(String name, int defaultValue, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("The min must not be greater than the max");
        } else if (min > defaultValue || defaultValue > max) {
            throw new IllegalArgumentException("The default value must be between the min and the max");
        }
        int result = getIntProperty(name, defaultValue);
        if (min > result) {
            throw new IllegalArgumentException(
                    "The value of the " + name + " property must not be less " + "than " + min + ": " + result);
        } else if (result > max) {
            throw new IllegalArgumentException(
                    "The value of the " + name + " property must not be greater " + "than " + max + ": " + result);
        }
        return result;
    }

    public int getRequiredIntProperty(String name, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("The min must not be greater than the max");
        }
        int result = getRequiredIntProperty(name);
        if (min > result) {
            throw new IllegalArgumentException(
                    "The value of the " + name + " property must not be less " + "than " + min + ": " + result);
        } else if (result > max) {
            throw new IllegalArgumentException(
                    "The value of the " + name + " property must not be greater " + "than " + max + ": " + result);
        }
        return result;
    }

    public long getLongProperty(String name, long defaultValue) {
        String value = properties.getValue(name);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw (NumberFormatException) new NumberFormatException(
                    "The value of the " + name + " property must be a valid " + "long: \"" + value + "\"").initCause(e);
        }
    }

    public long getLongProperty(String name, long defaultValue, long min, long max) {
        if (min > max) {
            throw new IllegalArgumentException("The min must not be greater than the max");
        } else if (min > defaultValue || defaultValue > max) {
            throw new IllegalArgumentException("The default value must be between the min and the max");
        }
        long result = getLongProperty(name, defaultValue);
        if (min > result) {
            throw new IllegalArgumentException(
                    "The value of the " + name + " property must not be less " + "than " + min + ": " + result);
        } else if (result > max) {
            throw new IllegalArgumentException(
                    "The value of the " + name + " property must not be greater " + "than " + max + ": " + result);
        }
        return result;
    }

    public <T> T getClassInstanceProperty(String name, Class<T> type, Class<?>[] paramTypes, Object... args) {
        String className = properties.getValue(name);
        if (className == null) {
            return null;
        }
        return getClassInstance(name, className, type, paramTypes, args);
    }

    public <T> T getClassInstanceProperty(String name, String defaultClass, Class<T> type, Class<?>[] paramTypes,
                                          Object... args) {
        Object instance = getClassInstanceProperty(name, type, paramTypes, args);

        if (instance != null) {
            return (T) instance;
        }
        if (defaultClass == null) {
            return null;
        }
        return getClassInstance(name, defaultClass, type, paramTypes, args);
    }

    private <T> T getClassInstance(String name, String className, Class<T> type, Class<?>[] paramTypes,
                                   Object... args) {
        if (className == null) {
            throw new NullPointerException("null className");
        }
        try {
            return Class.forName(className).asSubclass(type).getConstructor(paramTypes).newInstance(args);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("The class " + className + getPropertyText(name) + " was not found", e);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("The class " + className + getPropertyText(name) + " does not implement " + type.getName(), e);
        } catch (NoSuchMethodException e) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Class<?> paramType : paramTypes) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(paramType.getName());
            }
            throw new IllegalArgumentException("The class " + className + getPropertyText(name) + " does not have a constructor with required parameters: " + sb, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException("Calling the constructor for the class " + className
                        + getPropertyText(name) + " throws: " + cause, cause);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Creating an instance of the class " + className + getPropertyText(name) + " throws: " + e, e);
        }
    }

    private String getPropertyText(String name) {
        return name != null ? ", specified by the property: " + name + "," : "";
    }

    /**
     * 以冒号为分隔符.
     *
     * @param <T>            the generic type
     * @param name           the name
     * @param type           the type
     * @param defaultElement the default element
     * @return the list property
     */
    public <T> List<T> getListProperty(String name, Class<T> type, T defaultElement) {

        checkNull("modeName", name);
        checkNull("type", type);

        List<T> list = new ArrayList<T>();
        String value = properties.getValue(name);
        if (value == null) {
            return list;
        }

        String[] values = value.split(":", -1);
        Class<?>[] constructorParams = new Class<?>[]{String.class};
        Constructor<T> constructor = null;
        try {
            constructor = type.getConstructor(constructorParams);
        } catch (NoSuchMethodException nsme) {
            throw new IllegalArgumentException("The class " + type.getName() + " does not have a "
                    + "constructor with the required parameter : String", nsme);
        }
        for (String v : values) {
            if (v.equals("")) {
                list.add(defaultElement);
                continue;
            }

            try {
                list.add(constructor.newInstance(v));
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Creating an instance of the class " + type.getName() + " throws: " + e, e);
            }
        }

        return list;
    }

    public <T extends Enum<T>> List<T> getEnumListProperty(String name, Class<T> enumType, T defaultElement) {
        checkNull("modeName", name);
        checkNull("enumType", enumType);

        List<T> list = new ArrayList<T>();
        String value = properties.getValue(name);
        if (value == null) {
            list.add(defaultElement);
            return list;
        }

        String[] values = value.split(":", -1);
        for (String v : values) {
            if (v.equals("")) {
                list.add(defaultElement);
                continue;
            }

            try {
                list.add(Enum.valueOf(enumType, v));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("A value in the list of items in the " + name + " property was \""
                        + v + "\", but must be one of: " + Arrays.toString(enumType.getEnumConstants()));
            }
        }

        return list;
    }

    public List<Class<?>> getClassListProperty(String name) {
        checkNull("modeName", name);

        List<Class<?>> list = new ArrayList<Class<?>>();
        String value = properties.getValue(name);
        if (value == null) {
            return list;
        }

        String[] values = value.split(":", -1);
        for (String v : values) {
            if (v.equals("")) {
                list.add(null);
                continue;
            }

            try {
                list.add(Class.forName(v));
            } catch (ClassNotFoundException cnfe) {
                throw new IllegalArgumentException("A value in the list of items in the " + name + " property was \""
                        + v + "\", but a class was not found for this value", cnfe);
            }
        }

        return list;
    }
}
