/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.mapper;

import cn.com.inhand.tools.utilities.Parameter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author hanchuanjun
 */
public class KVRowMapper 
    implements RowMapper {

    @Override
    public Object mapRow(ResultSet rs, int i) throws SQLException {
       Parameter p = new Parameter();
        //g.setaField(rs.getString(1));
        
        
        int cols= rs.getMetaData().getColumnCount();
        String key="";
        for (int ii=1;ii<=cols-1;ii++){
            key +=(rs.getString(ii)==null?"":rs.getString(ii));
            if (ii<cols-1){
                key +=".";
            }
        }
        p.setName(key);
        p.setValue(rs.getString(cols));
        return p;
    }
    
}
