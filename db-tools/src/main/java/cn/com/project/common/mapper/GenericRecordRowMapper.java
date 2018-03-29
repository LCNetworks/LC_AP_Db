/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.mapper;

import cn.com.project.common.dto.GenericRecord;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author han
 */
public class GenericRecordRowMapper 
    implements RowMapper{

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
        GenericRecord g = new GenericRecord();
        //g.setaField(rs.getString(1));
        
        
        int cols= rs.getMetaData().getColumnCount();
        String key="";
        for (int ii=1;ii<=cols;ii++){
            key +=(rs.getString(ii)==null?"":rs.getString(ii));
            if (ii<cols){
                key +=",";
            }
        }
        g.setaField(key);
        return g;
    }
}
