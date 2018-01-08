/*
 * *****************************************************************************
 *               MCTI - Ministério da Ciência, Tecnologia e Inovação             
 *                INPE - Instituto Nacional de Pesquisas Espaciais               
 * -----------------------------------------------------------------------------
 * André Aparecido de S. Ivo <andre.ivo@inpe.br>        
 * *****************************************************************************
 */
package sysvap.gui.helper.propertyeditor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sysvap.core.data.SysLookupMEF;
import sysvap.gui.helper.propertyeditor.combofield.enumtype.SysIComboFieldEnum;

/**
 *
 * @author Ivo
 */
public class SysReflection {

    public static Field getFieldByName(SysIPropEditor objExplore, String name) {
        Field result = null;
        try {
            try {
                result = objExplore.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException ex) {
                if (objExplore.getClass().getSuperclass() != null) {
                    result = objExplore.getClass().getSuperclass().getDeclaredField(name);
                }
            }
            return result;
        } catch (Exception ex) {
            
        }
        return null;
    }

    public static Class getClass(SysIPropEditor objExplore, String name) {
        try {
            try {
                objExplore.getClass().getDeclaredField(name);
                return objExplore.getClass();
            } catch (NoSuchFieldException ex) {
                if (objExplore.getClass().getSuperclass() != null) {
                    objExplore.getClass().getSuperclass().getDeclaredField(name);
                    return objExplore.getClass().getSuperclass();
                }
            }
        } catch (Exception ex) {
            System.out.println("Error getFieldByName : " + ex.getMessage());
        }
        return null;
    }

    public static Object getValueField(SysIPropEditor objExplore, Field field) {
        String method = "get".concat(field.getName().substring(0, 1).toUpperCase().trim().concat(field.getName().substring(1).trim()));
        Object result = null;
        try {
            if (objExplore != null) {
                Class cls = Class.forName(getClass(objExplore, field.getName()).getName());
                Method meth = cls.getMethod(method);
                result = meth.invoke(objExplore);
            }
        } catch (Exception ex) {
            System.out.println("Error getValueField: " + ex.getMessage());
        }
        return result;
    }

    public static Object getValueField(SysIPropEditor objExplore, String field) {
        String method = "get".concat(field.substring(0, 1).toUpperCase().trim().concat(field.substring(1).trim()));
        Object result = null;
        try {
            if (objExplore != null) {
                Class cls = Class.forName(getClass(objExplore, field).getName());
                Method meth = cls.getMethod(method);
                result = meth.invoke(objExplore);
            }
        } catch (Exception ex) {
            System.out.println("Error getValueField: " + ex.getMessage());
        }
        return result;
    }

    public static void setValueField(SysIPropEditor objExplore, Field field, Object value) throws IllegalArgumentException, IllegalAccessException {
        String method = "set".concat(field.getName().substring(0, 1).toUpperCase().trim().concat(field.getName().substring(1).trim()));
        try {
            if (objExplore != null) {
                Class cls = Class.forName(getClass(objExplore, field.getName()).getName());
                Method meth = cls.getMethod(method, value.getClass());
                meth.invoke(objExplore, value);
            }
        } catch (Exception ex) {
            System.out.println("Error setValueField: " + ex.getMessage());
        }
    }

    public static void setValueFieldForLookup(SysLookupMEF objExplore, Object value) throws IllegalArgumentException, IllegalAccessException {
        String method = "setSelectedItem";
        try {
            if (objExplore != null) {
                Class cls = Class.forName(objExplore.getClass().getName());
                Method meth = cls.getMethod(method, Object.class);
                meth.invoke(objExplore, value);
            }
        } catch (Exception ex) {
            System.out.println("Error setValueFieldForLookup: " + ex);
        }
    }

    public static Class<?>[] getInterfaces(Field field) {
        return field.getType().getInterfaces();
    }

    public static Class<?>[] getInterfaces(Class<?> field) {
        return field.getInterfaces();
    }

    public static Boolean hasInterface(Field field, Class<?> clazz) {
        Class<?>[] interf = getInterfaces(field);
        for (Class<?> item : interf) {
            if (item == clazz) {
                return true;
            }
        }

        return false;
    }

    public static Boolean hasInterface(Class<?> field, Class<?> clazz) {
        Class<?>[] interf = getInterfaces(field);
        for (Class<?> item : interf) {
            if (item == clazz) {
                return true;
            }
        }

        return false;
    }

    public static Object getEnumByValue(Class<?> clazz, String value) {
        Object[] enumArray = clazz.getEnumConstants();
        //String[] result = new String[enumArray.length];
        for (int i = 0; i < enumArray.length; i++) {
            Object fld = enumArray[i];
            SysIComboFieldEnum comboFieldValue = (SysIComboFieldEnum) fld;

            if (comboFieldValue.getValue().equals(value)) {
                return fld;
            }
        }
        return null;
    }
}
