package cn.com.project.common.tools.rdb;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by han on 18-3-21.
 */
public class RDBTableFactory {
    public static Table getTable(String dbType,Object row, String pk, JdbcTemplate jt)throws UnsupportedOperationException{
        if ("mysql".equalsIgnoreCase(dbType)){
            return new MysqlTable(row,pk,jt);
        }else if("mssql".equalsIgnoreCase(dbType)){
            return new AplusTable(row,pk,jt);
        }else if("oracle".equalsIgnoreCase(dbType)){
            return new OracleTable(row,pk,jt);
        }else{
            throw new UnsupportedOperationException("Unsupported database type!");
        }
    }
    public static Table getTable(String dbType,Object row, JdbcTemplate jt)throws UnsupportedOperationException{
        if ("mysql".equalsIgnoreCase(dbType)){
            return new MysqlTable(row,jt);
        }else if("mssql".equalsIgnoreCase(dbType)){
            return new AplusTable(row,jt);
        }else if("oracle".equalsIgnoreCase(dbType)){
            return new OracleTable(row,jt);
        }else{
            throw new UnsupportedOperationException("Unsupported database type!");
        }
    }
}
