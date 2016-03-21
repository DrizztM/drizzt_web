package drizzt.common.base;

import static drizzt.common.tools.TemplateModelUtils.converBoolean;
import static drizzt.common.tools.TemplateModelUtils.converDate;
import static drizzt.common.tools.TemplateModelUtils.converDouble;
import static drizzt.common.tools.TemplateModelUtils.converInteger;
import static drizzt.common.tools.TemplateModelUtils.converLong;
import static drizzt.common.tools.TemplateModelUtils.converMap;
import static drizzt.common.tools.TemplateModelUtils.converShort;
import static drizzt.common.tools.TemplateModelUtils.converString;
import static drizzt.common.tools.TemplateModelUtils.converStringArray;
import static org.apache.commons.logging.LogFactory.getLog;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * 
 * BaseMethod FreeMarker自定义方法基类
 *
 */
public abstract class BaseMethod implements TemplateMethodModelEx {
    protected final Log log = getLog(getClass());
    private static TemplateModel getModel(int index, List<TemplateModel> arguments) {
        if (index < arguments.size()) {
            return arguments.get(index);
        }
        return null;
    }

    public TemplateHashModel getMap(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converMap(getModel(index, arguments));
    }

    public String getString(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converString(getModel(index, arguments));
    }

    public Integer getInteger(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converInteger(getModel(index, arguments));
    }

    public Short getShort(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converShort(getModel(index, arguments));
    }

    public Long getLong(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converLong(getModel(index, arguments));
    }

    public Double getDouble(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converDouble(getModel(index, arguments));
    }

    public String[] getStringArray(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converStringArray(getModel(index, arguments));
    }

    public Integer[] getIntegerArray(int index, List<TemplateModel> arguments) throws TemplateModelException {
        String[] arr = getStringArray(index, arguments);
        if (null != arr) {
            Integer[] ids = new Integer[arr.length];
            int i = 0;
            try {
                for (String s : arr) {
                    ids[i++] = Integer.valueOf(s);
                }
                return ids;
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public Long[] getLongArray(int index, List<TemplateModel> arguments) throws TemplateModelException {
        String[] arr = getStringArray(index, arguments);
        if (null != arr) {
            Long[] ids = new Long[arr.length];
            int i = 0;
            try {
                for (String s : arr) {
                    ids[i++] = Long.valueOf(s);
                }
                return ids;
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    public Boolean getBoolean(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converBoolean(getModel(index, arguments));
    }

    public Date getDate(int index, List<TemplateModel> arguments) throws TemplateModelException {
        return converDate(getModel(index, arguments));
    }
}
