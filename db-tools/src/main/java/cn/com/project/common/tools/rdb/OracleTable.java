/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.tools.rdb;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author hanchuanjun
 */
public class OracleTable extends Table {
    public OracleTable() {
        super();
    }    

    public OracleTable(Object row, JdbcTemplate jt) {
        super(row,jt);
    }

    public OracleTable(Object row, String pk, JdbcTemplate jt) {
        super(row,pk,jt);
    }



    @Override
    public Integer saveWithNewId() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean existRecord(String dbName, String field, String where) {
//        String sql = "select count(" + (field==null?"*":field) + ")  from " + dbName + " where " + where;
//        List<GenericRecord> list = jt.query(sql, new CountRecordRowMapper());
//
//        boolean result = (list != null && list.size() > 0);
//        list.clear();
        
        int total = jt.queryForObject(this.getCountSql(where),
                    new Object[]{},
                    new int[]{},
                    Integer.class);
        return total > 0;
    }

    @Override
    public String getListSql(String sqlWhere, Integer pageNumber, Integer pageSize, String orderby) {
        String sql = "select * from " + this.name + " where 1=1";

        if (pageNumber != null && pageSize != null) {
            if (orderby != null && !orderby.trim().equals("")) {
                sql = "select * from  (select tt.*,rownum as rowno from ("
                + "select t.* from "
                + this.name +" t" 
                + " where 1=1 "+sqlWhere +" order by "+orderby+") tt"
                        + " where rownum <="+(pageNumber*pageSize)+") table_alias "
                        + "where table_alias.rowno >="+((pageNumber-1)*pageSize);
            } else {
               
                sql = "select * from  (select t.*,rownum rn from "
                + this.name +" t" 
                + " where 1=1 "+sqlWhere +" and rownum <="+(pageNumber*pageSize)+") table_alias "
                        + "where table_alias.rn >="+((pageNumber-1)*pageSize);
            }
        } else {
            if (orderby != null && !orderby.trim().equals("")) {
                sql += sqlWhere + " order by " + orderby;
            } else {
                if (this.pkey != null){
                    sql += sqlWhere + " order by " + this.pkey;
                }
            }
        }

        return sql;
    }
    
}
