/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.mapper;

import cn.com.project.common.dto.CountRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author han
 */
public class CountRecordRowMapper implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        CountRecord r = new CountRecord();
        int cols= rs.getMetaData().getColumnCount();
        String key="";
        if (cols <=1){
            r.setKey(key);
            r.setCount(rs.getInt(1));
            return r;
        }
        for (int ii=1;ii<cols;ii++){
            key +=(rs.getString(ii)==null?"":rs.getString(ii));
            if (ii<cols-1){
                key +=":";
            }
        }
        r.setKey(key);
        r.setCount(rs.getInt(cols));
        return r;
    }
    
}
