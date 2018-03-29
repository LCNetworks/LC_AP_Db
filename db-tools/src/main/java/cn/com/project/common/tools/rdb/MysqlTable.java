package cn.com.project.common.tools.rdb;

import cn.com.project.common.dto.GenericRecord;
import cn.com.project.common.mapper.GenericRecordRowMapper;
import cn.com.inhand.tools.data.DataCollections;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by han on 18-3-21.
 */
public class MysqlTable extends Table {
    public MysqlTable(Object row, JdbcTemplate jt) {
        super(row,jt);
    }

    public MysqlTable(Object row, String pk, JdbcTemplate jt) {
        super(row,pk,jt);
    }
    @Override
    public DataCollections getDataCollections(String where, Integer pageNumber, Integer pageSize, String orderby) {
        return null;
    }


    @Override
    public String getListSql(String sqlWhere, Integer pageNumber, Integer pageSize, String orderby) {
        String sql = "select * from " + this.name + " where 1=1";

        if (sqlWhere == null){
            sqlWhere="";
        }

        if (pageNumber != null && pageSize != null) {
            if (orderby != null && !orderby.trim().equals("")) {
                sql += " " + sqlWhere + " order by " + orderby + " limit "+pageSize+" offset "+ ((pageNumber - 1) * pageSize);
            } else {
                sql += " " + sqlWhere + " limit "+pageSize+" offset "+ ((pageNumber - 1) * pageSize);
            }
        } else {
            if (orderby != null && !orderby.trim().equals("")) {
                sql += sqlWhere + " order by " + orderby;
            } else {
                sql += sqlWhere + " order by " + this.pkey;
            }
        }

        return sql;
    }


    /**
     * 对于自增的表,新增后将自动返回自增id(int/long型)
     *
     * @return 自增id
     */
    public Integer saveWithNewId() {
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

            ret = this.saveWithNewId(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.clear();
        return ret;
    }

    /**
     * 对于自增的表,新增后将自动返回自增id(int/long型)
     *
     * @param insertSql
     * @return
     */
    private Integer saveWithNewId(String insertSql) {
        final String sql = insertSql;
        final String pk = (this.pkey != null ? pkey : this.name + "_id");
        KeyHolder keyHolder = new GeneratedKeyHolder();
        this.jt.update(new PreparedStatementCreator() {
            public java.sql.PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                int i = 0;
                java.sql.PreparedStatement ps = conn.prepareStatement(sql, new String[]{pk});

                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                return ps;
            }
        }, keyHolder);
        return keyHolder.getKey().intValue();

    }


    @Override
    public boolean existRecord(String dbName, String field, String where) {
        String sql = "select " + field + " as a_field  from " + dbName + " where " + where + " limit 1";
        List<GenericRecord> list = jt.query(sql, new GenericRecordRowMapper());

        boolean result = (list != null && list.size() > 0);
        list.clear();
        return result;
    }
}
