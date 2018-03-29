/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.tools.rdb;

import cn.com.inhand.tools.data.DataCollections;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author hanchuanjun
 */
public abstract class Table {
    protected Object row;
    protected String name;
    //private DataSource dataSource;
    protected JdbcTemplate jt;
    protected String pkey;

    public Table() {

    }

    public String getPkey() {
        return pkey;
    }

    public void setPkey(String pkey) {
        this.pkey = pkey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer excuteUpdate(String sql) {
        return this.jt.update(sql);
    }

    public void setJt(JdbcTemplate jt) {
        this.jt = jt;
    }

    public Table(Object row, JdbcTemplate jt) {
        this.row = row;
        this.jt = jt;
        this.name = this.convertTblName(row);
        this.pkey = this.name + "_id";
    }

    public Table(Object row, String pk, JdbcTemplate jt) {
        this.row = row;
        this.jt = jt;
        this.name = this.convertTblName(row);
        this.pkey = this.convertName(pk);
    }

    public Object getRow() {
        return row;
    }

    public void setRow(Object row) {
        this.row = row;
    }

    /**
     * 返回结果集合
     *
     * @param where
     * @param pageNumber
     * @param pageSize
     * @param orderby
     * @return
     */
    public DataCollections getDataCollections(String where, Integer pageNumber, Integer pageSize, String orderby) {
        DataCollections result = new DataCollections();
        if (orderby != null && !orderby.trim().equals("")) {
            orderby = this.convertName(orderby);
        }
        if (where != null && !where.trim().equals("")) {
            where = this.convertName(where);
        }
        String sql = this.getListSql(where, pageNumber, pageSize, orderby);
        String countSql = this.getCountSql(where);
        try {
            List<Object> list = jt.query(sql,
                    new Object[]{},
                    new int[]{},
                    this.createRowMapper());
            int total = jt.queryForObject(countSql,
                    new Object[]{},
                    new int[]{},
                    Integer.class);
            result.setPageNumber(pageNumber == null ? 1 : pageNumber);

            result.setPageSize(pageSize == null ? total : pageSize);
            result.setList(list);
            result.setTotal(total);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract String getListSql(String sqlWhere, Integer pageNumber, Integer pageSize, String orderby);
    /**
     * 返回符合条件的记录
     *
     *
     * @param rowMapper rowMapper对象
     * @return
     */
    public List getResults(List<String> fnames, RowMapper rowMapper) {

        Map<String, String> map = this.findClassAttributes();
        String where = this.convertWhere(fnames, map);
        String sql = "select * from " + name + (where == null ? "" : " where " + where);
        try {
            List list = jt.query(sql,
                    new Object[]{},
                    new int[]{},
                    rowMapper);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }

    /**
     * 返回符合条件的记录
     *
     * @param fnames 对象的需要作为条件查询的属性名集合
     * @return
     */
    public List getResults(List<String> fnames) {

        Map<String, String> map = this.findClassAttributes();
        String where = this.convertWhere(fnames, map);
       
        String sql = "select * from " + name + (where == null ? "" : " where " + where);
        try {
            List list = jt.query(sql,
                    new Object[]{},
                    new int[]{},
                    this.createRowMapper());
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }   
    }

    /**
     * 返回符合条件的记录
     *
     * @param where sql语句中的where 条件
     * @return
     */
    public List getResults(String where) {

        where = this.convertName(where);
        String sql = "select * from " + name + (where == null || where.equals("") ? "" : " where " + where);
        List list = jt.query(sql,
                new Object[]{},
                new int[]{},
                this.createRowMapper());
        return list;
    }

    public List getResultsBySQL(String sql) {
        sql = this.convertName(sql);
        List list = jt.query(sql,
                new Object[]{},
                new int[]{},
                this.createRowMapper());
        return list;
    }

    /**
     * 根据row对象生成对应的RowMapper
     *
     * @return 根据row对象生成对应的RowMapper
     */
    public RowMapper createRowMapper() {
        class AplusTableRowMapper implements RowMapper {

            Table table;

            public AplusTableRowMapper(Table table) {
                this.table = table;
            }

            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                try {
                    Object o = this.setupObjectByResultSet(rs);

                    return o;
                } catch (Exception ex) {
                    Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }

            /**
             * 将查到的行记录赋值给obj对象
             *
             * @param rs
             */
            protected Object setupObjectByResultSet(ResultSet rs) throws Exception {
                Object obj = table.getRow().getClass().newInstance();
                Class c = obj.getClass();

                Field[] fields = c.getDeclaredFields();
                for (Field field : fields) {
                    String fieldName = field.getName();
                    String type = field.getType().toString();
                    String firstLetter = fieldName.substring(0, 1).toUpperCase();
                    String setter = "set" + firstLetter + fieldName.substring(1);
                    //System.out.println("----filed:"+fieldName+"   type:"+type);
                    Class[] cs = new Class[1];
                    Object[] args = new Object[1];
                    if (type.indexOf("Integer") >= 0) {
                        cs[0] = Integer.class;
                        args[0] = rs.getInt(table.convertName(fieldName));
                    } else if (type.indexOf("Long") >= 0) {
                        cs[0] = Long.class;
                        args[0] = rs.getLong(table.convertName(fieldName));
                    } else if (type.indexOf("Float") >= 0) {
                        cs[0] = Float.class;
                        args[0] = rs.getFloat(table.convertName(fieldName));
                    } else if (type.indexOf("Double") >= 0) {
                        cs[0] = Double.class;
                        args[0] = rs.getDouble(table.convertName(fieldName));
                    } else if (type.indexOf("Date") >= 0) {
                        cs[0] = Date.class;
                        args[0] = rs.getDate(table.convertName(fieldName));
                    }else if (type.indexOf("Timestamp") >= 0) {
                        cs[0] = Timestamp.class;
                        args[0] = rs.getTimestamp(table.convertName(fieldName));
                    } else if (type.indexOf("Datetime") >= 0) {
                        cs[0] = Timestamp.class;
                        args[0] = rs.getTimestamp(table.convertName(fieldName));
                    } else if (type.indexOf("Blob") >= 0) {
                        cs[0] = byte[].class;
                        Blob blob = rs.getBlob(table.convertName(fieldName));
                        args[0] = blob.getBytes(0l, (int) blob.length());
                    } else {
                        cs[0] = String.class;
                        args[0] = rs.getString(table.convertName(fieldName));
                    }

                    Method method = row.getClass().getMethod(setter, cs);
                    Object value = method.invoke(obj, args);
                }
                return obj;
            }
        }
        AplusTableRowMapper rowMapper = new AplusTableRowMapper(this);
        return rowMapper;
    }

    /**
     * 返回符合条件的记录
     *
     * @param where sql语句中的where 条件
     * @param rowMapper
     * @return
     */
    public List getResults(String where, RowMapper rowMapper) {

        Map<String, String> map = this.findClassAttributes();
        String sql = "select * from " + name + (where == null ? "" : " where " + where);
        List list = jt.query(sql,
                new Object[]{},
                new int[]{},
                rowMapper);
        return list;
    }

    /**
     * 计算符合row对象的需要作为条件查询的对象总数
     *
     * @param fnames row对象的需要作为条件查询的属性名集合
     * @return 符合条件的记录数
     */
    public Integer getResultCount(List<String> fnames) {

        Map<String, String> map = this.findClassAttributes();
        String where = this.convertWhere(fnames, map);
        String sql = "select Count(*) from " + name + (where == null ? "" : " where " + where);
        int total = jt.queryForObject(sql,
                new Object[]{},
                new int[]{},
                Integer.class);
        return total;
    }

    /**
     * 生成查询列表语句
     *
     * @param fnames row对象的需要作为条件查询的属性名集合
     * @return sql语句
     */
    public String createFindSql(List<String> fnames) {
        Map<String, String> map = this.findClassAttributes();
        String where = this.convertWhere(fnames, map);
        String sql = "select * from " + name + (where == null ? "" : " where " + where);
        return sql;
    }

    /**
     * 查询一个与row对象匹配必要字段条件的相同类对象
     *
     * @param fnames row对象的需要作为条件查询的属性名集合
     * @return 要查询的对象
     */
    public Object get(List<String> fnames) {

        Map<String, String> map = this.findClassAttributes();
        String where = this.convertWhere(fnames, map);
        String sql = "select * from " + name + (where == null ? "" : " where " + where);
        /*        Object o = jt.queryForObject(sql,
         new Object[]{},
         new int[]{},
         this.row.getClass());
         return o;
         */
        Object o = null;
        List list = jt.query(sql, this.createRowMapper());
        if (list != null && list.size() > 0) {
            o = list.get(0);
            list.clear();
        }
        return o;
    }

    /**
     * 删除与row对象中匹配的数据库中的行记录
     *
     * @param fnames row对象的需要作为条件查询的属性名集合
     * @return
     */
    public Integer delete(List<String> fnames) {
        Integer ret = null;
        String sql = "";
        String where = "";
        String fields = "";
        String values = "";

        Map<String, String> map = this.findClassAttributes();
        if (fnames == null || fnames.size() <= 0) {
            return null;
        }
        where = this.convertWhere(fnames, map);
        try {

            sql = "delete " + name + " where " + where;
            ret = jt.update(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 转换为sql的where条件语句
     *
     * @param fnames row对象的需要作为条件查询的属性名集合
     * @param map row对象的属性值集合
     * @return
     */
    public String convertWhere(List<String> fnames, Map<String, String> map) {
        if (fnames == null || fnames.size() <= 0) {
            return null;
        }
        String where = this.convertName(fnames.get(0));
        Object v = fnames.get(0);
        if (map.get(v.toString()) == null) {
            where += " is null";
        } else {
            where += "=" + map.get(v.toString());
        }
        for (int ii = 1; ii < fnames.size(); ii++) {
            where += " and  " + this.convertName(fnames.get(ii));
            v = fnames.get(ii);
            if (map.get(v.toString()) == null) {
                where += " is null";
            } else {
                where += "=" + map.get(v.toString());
            }
        }

        return where;
    }

    /**
     * 更新与row对象对应的表里对应行记录
     *
     * @param fnames row对象中作为where条件的属性名集合
     * @return
     */
    public Integer update(List<String> fnames) {
        Integer ret = null;
        String sql = "";
        String where = "";
        String expression = "";
        String primarykey = this.convertName2ClassFiedName(this.pkey);
        Map<String, String> map = this.findClassAttributes();
        if (fnames == null || fnames.size() <= 0) {
            String v = map.get(primarykey);
            if (v != null) {
                where = "" + pkey + "=" + v;
            } else {
                return -1;
            }
        } else {
            where = this.convertWhere(fnames, map);
        }

        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (pkey == null || (pkey != null && !primarykey.equalsIgnoreCase(key))) {
                expression += this.convertName(key) + "=" + map.get(key);
                if (it.hasNext()) {
                    expression += ",";
                } else {
                    break;
                }
            }
        }
        if (expression.endsWith(",")) {
            expression = expression.substring(0, expression.length() - 1);
        }
        sql = "update " + name + " set " + expression + " where " + where;
        ret = jt.update(sql);

        return ret;
    }

    /**
     * 向数据库对应的表新增一个对应row对象的记录
     *
     * @return 成功则返回1,表示插入1条记录,如果非1则表示失败
     */
    public Integer save() {
        Integer ret = null;
        Map<String, String> map = this.findClassAttributes();

        try {
            String fields = "";
            String values = "";
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();

                fields += this.convertName(key);// + ",";
                values += map.get(key);//+ ",";

                if (it.hasNext()) {
                    fields += ",";
                    values += ",";
                } else {
                    break;
                }
            }
            String sql = "insert into " + name + " (" + fields + ")values(" + values + ")";

            ret = jt.update(sql);
            //ret = this.saveWithNewId(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.clear();
        return ret;
    }

    /**
     * 对于自增的表,新增后将自动返回自增id(int/long型)
     *
     * @return 自增id
     */
    public abstract Integer saveWithNewId();

    

    /**
     * 获得row对象中的属性名-属性值类型map
     *
     * @return
     */
    public Map<String, String> findClassAttributes() {
        Class c = row.getClass();
        Map<String, String> map = new HashMap<String, String>();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            String type = field.getType().toString();

            Object v = this.calcFieldValueByName(field.getName());
            if (v != null) {
                if (type.indexOf("Integer") >= 0
                        || type.indexOf("Long") >= 0
                        || type.indexOf("Float") >= 0
                        || type.indexOf("Double") >= 0) {
                    map.put(field.getName(), v.toString());
                } else {
                    map.put(field.getName(), "'" + v.toString() + "'");
                }
            }

        }

        return map;
    }

    /**
     * 根据row对象属性的值类型生成数据库表中的字段值
     *
     * @param fieldName
     * @return
     */
    public Object calcFieldValueByName(String fieldName) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = row.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(row, new Object[]{});
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将查询得到的行记录赋值到对象中.列->对象属性
     *
     * @param rs
     */
    public void setupValueByFiledName(ResultSet rs) {
        Class c = row.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldName = field.getName();
                String type = field.getType().toString();
                String firstLetter = fieldName.substring(0, 1).toUpperCase();
                String setter = "set" + firstLetter + fieldName.substring(1);

                Class[] cs = new Class[1];
                Object[] objs = new Object[1];
                if (type.indexOf("Integer") >= 0) {
                    cs[0] = Integer.class;
                    objs[0] = rs.getInt(this.convertName(fieldName));
                } else if (type.indexOf("Long") >= 0) {
                    cs[0] = Long.class;
                    objs[0] = rs.getLong(this.convertName(fieldName));
                } else if (type.indexOf("Float") >= 0) {
                    cs[0] = Float.class;
                    objs[0] = rs.getFloat(this.convertName(fieldName));
                } else if (type.indexOf("Double") >= 0) {
                    cs[0] = Double.class;
                    objs[0] = rs.getDouble(this.convertName(fieldName));
                } else if (type.indexOf("Date") >= 0) {
                    cs[0] = Date.class;
                    objs[0] = rs.getDate(this.convertName(fieldName));
                } else if (type.indexOf("Blob") >= 0) {
                    cs[0] = byte[].class;
                    Blob blob = rs.getBlob(this.convertName(fieldName));
                    objs[0] = blob.getBytes(0l, (int) blob.length());
                } else {

                    cs[0] = String.class;
                    objs[0] = rs.getString(this.convertName(fieldName));
                }

                Method method = row.getClass().getMethod(setter, cs);

                Object value = method.invoke(row, objs);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(AplusTable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String args[]){
        AplusTable tbl = new AplusTable();
        String result = " where 1=1 and Beq ='iMma' and bEq='imma' and BEQ='i\"dfd' or BeQ=\"a_12Be\""
                +" or BEq = \"aQ_2'df'QD\"";
        System.out.println(result);
        System.out.println(tbl.convertName(result));
    }

    /**
     * 转换类属性方式到表字段方式,如 ihUserInfo => ih_user_info
     *
     * @param name
     * @return
     */
    public String convertName(String name) {
        if (name == null || name.equals("")) {
            return null;
        }
        String result = "";
        result += name.substring(0, 1);
        int flag1 = 0;
        char charFlag = 0;
        for (int ii = 1; ii < name.length(); ii++) {
            char c = name.charAt(ii);
            if (c == '\'' || c == '\"') {
                //if (flag1 % 2 == 0) {
                    if (charFlag ==0 || charFlag == c){
                        flag1 = (flag1 + 1) % 2;
                        
                        if (flag1 % 2 == 1){
                            charFlag = c;
                        }else{
                            charFlag = 0;
                        }
                    }
                    //System.out.println("char:"+charFlag+" and count:"+flag1+" and offset="+ii);
                //}
                result +=c;
            } else {
                if (flag1 % 2 == 0) {

                    if (c >= 'A' && c <= 'Z' && name.charAt(ii - 1) != ' ') {
                        result += "_" + c;
                    } else {
                        result += c;
                    }
                } else {
                    result += c;
                }

            }
        }

        return result.toLowerCase();
    }

    /**
     * 转换类属性方式到表字段方式,如 ihUserInfo => ih_user_info
     *
     * @param name
     * @return
     */
    public String convertName2ClassFiedName(String name) {
        String result = "";
        name = name.toLowerCase();
        result += name.substring(0, 1);
        for (int ii = 1; ii < name.length(); ii++) {
            char c = name.charAt(ii);
            if (c == '_') {
                if (ii < name.length() - 1) {
                    result += (char) (name.charAt(ii + 1) - 32);
                    ii++;
                }
            } else {
                result += c;
            }
        }

        return result;
    }

    /**
     * 根据类名转换为表名
     *
     * @param o
     * @return
     */
    public String convertTblName(Object o) {
        String cname = o.getClass().getSimpleName();
        return this.convertName(cname);
    }

    /**
     * 查一下数据库是否存在符合条件的记录
     *
     * @param dbName
     * @param field
     * @param where
     * @return
     */
    public abstract boolean existRecord(String dbName, String field, String where);

    public String getCountSql(String sqlWhere) {
        String sql = "select count(*) from " + this.name + " where 1=1" + sqlWhere;
        return sql;
    }

    
}

