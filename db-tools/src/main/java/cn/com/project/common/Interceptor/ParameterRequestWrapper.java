/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.Interceptor;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 *
 * @author hanchuanjun
 */
public class ParameterRequestWrapper  extends HttpServletRequestWrapper {  
  
    private Map params;  
  
    public ParameterRequestWrapper(HttpServletRequest request, Map newParams) {  
        super(request);  
        this.params = newParams;  
    }  
  
    public Map getParameterMap() {  
        return params;  
    } 
    
    @Override
    public void finalize(){
        try {
            if (this.params != null){
                this.params.clear();
            }
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(ParameterRequestWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  
    public Enumeration getParameterNames() {  
        Vector l = new Vector(params.keySet());  
        return l.elements();  
    }  
  
    public String[] getParameterValues(String name) {  
        Object v = params.get(name);  
        if (v == null) {  
            return null;  
        } else if (v instanceof String[]) {  
            return (String[]) v;  
        } else if (v instanceof String) {  
            return new String[] { (String) v };  
        } else {  
            return new String[] { v.toString() };  
        }  
    }  
  
    public String getParameter(String name) {  
        Object v = params.get(name);  
        if (v == null) {  
            return null;  
        } else if (v instanceof String[]) {  
            String[] strArr = (String[]) v;  
            if (strArr.length > 0) {  
                return strArr[0];  
            } else {  
                return null;  
            }  
        } else if (v instanceof String) {  
            return (String) v;  
        } else {  
            return v.toString();  
        }  
    }  
}  