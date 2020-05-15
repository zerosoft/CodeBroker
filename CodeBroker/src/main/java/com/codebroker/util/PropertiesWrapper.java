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

    /**
     * The properties.
     */
    private final Props properties;

    /**
     * Instantiates a new properties wrapper.
     *
     * @param properties the properties
     */
    public PropertiesWrapper(Props properties) {
        if (properties == null) {
            throw new NullPointerException("The argument must not be null");
        }
        this.properties = properties;
    }

    /**
     * Check null.
     *
     * @param variableName the variable name
     * @param value        the value
     */
    public static void checkNull(String variableName, Object value) {
        if (value == null) {
            throw new NullPointerException("The value of " + variableName + " must not be null");
        }
    }

    /**
     * Gets the properties.
     *
     * @return the properties
     */
    public Props getProperties() {
        return properties;
    }

    /**
     * Gets the property.
     *
     * @param name the name
     * @return the property
     */
    public String getProperty(String name) {
        return properties.getValue(name);
    }

    /**
     * Gets the property.
     *
     * @param name         the name
     * @param defaultValue the default value
     * @return the property
     */
    public String getProperty(String name, String defaultValue) {
        String value = properties.getValue(name);
        return value == null ? defaultValue : value;
    }

    /**
     * Gets the boolean property.
     *
     * @param name         the name
     * @param defaultValue the default value
     * @return the boolean property
     */
    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = properties.getValue(name);
        return value == null ? defaultValue : Boolean.valueOf(value);
    }

    /**
     * Gets the int property.
     *
     * @param name         the name
     * @param defaultValue the default value
     * @return the int property
     */
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

    /**
     * Gets the required int property.
     *
     * @param name the name
     * @return the required int property
     */
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

    /**
     * Gets the int property.
     *
     * @param name         the name
     * @param defaultValue the default value
     * @param min          the min
     * @param max          the max
     * @return the int property
     */
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

    /**
     * Gets the required int property.
     *
     * @param name the name
     * @param min  the min
     * @param max  the max
     * @return the required int property
     */
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

    /**
     * Gets the long property.
     *
     * @param name         the name
     * @param defaultValue the default value
     * @return the long property
     */
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

    /**
     * Gets the long property.
     *
     * @param name         the name
     * @param defaultValue the default value
     * @param min          the min
     * @param max          the max
     * @return the long property
     */
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

    /**
     * Gets the class instance property.
     *
     * @param <T>        the generic type
     * @param name       the name
     * @param type       the type
     * @param paramTypes the param types
     * @param args       the args
     * @return the class instance property
     */
    public <T> T getClassInstanceProperty(String name, Class<T> type, Class<?>[] paramTypes, Object... args) {
        String className = properties.getValue(name);
        if (className == null) {
            return null;
        }
        return getClassInstance(name, className, type, paramTypes, args);
    }

    /**
     * Gets the class instance property.
     *
     * @param <T>          the generic type
     * @param name         the name
     * @param defaultClass the default class
     * @param type         the type
     * @param paramTypes   the param types
     * @param args         the args
     * @return the class instance property
     */
    @SuppressWarnings("unchecked")
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

    /**
     * Gets the class instance.
     *
     * @param <T>        the generic type
     * @param name       the name
     * @param className  the class name
     * @param type       the type
     * @param paramTypes the param types
     * @param args       the args
     * @return the class instance
     */
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

    /**
     * Gets the property text.
     *
     * @param name the name
     * @return the property text
     */
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

    /**
     * Gets the enum list property.
     *
     * @param <T>            the generic type
     * @param name           the name
     * @param enumType       the enum type
     * @param defaultElement the default element
     * @return the enum list property
     */
    public <T extends Enum<T>> List<T> getEnumListProperty(String name, Class<T> enumType, T defaultElement) {
        checkNull("modeName", name);
        checkNull("enumType", enumType);

        List<T> list = new ArrayList<T>();
        String value = properties.getValue(name);
        if (value == null) {
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

    /**
     * Gets the class list property.
     *
     * @param name the name
     * @return the class list property
     */
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
