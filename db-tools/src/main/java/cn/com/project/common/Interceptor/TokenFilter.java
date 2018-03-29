/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.Interceptor;

import cn.com.inhand.tools.net.GenericURL;
import cn.com.inhand.tools.net.client.Client;
import cn.com.inhand.tools.net.client.http.GenericHttpGet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author hanchuanjun
 */
public class TokenFilter  implements Filter {
    private String oauthServerUrl = "";
    private ObjectMapper objectMapper;

    public void setOauthServerUrl(String oauthServerUrl) {
        this.oauthServerUrl = oauthServerUrl;
                
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        
        String access_token = request.getParameter("");
        
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,content-type");
        
        //Access-Control-Allow-Headers
        chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) {}

    public void destroy() {}

        private String getUserInfo(String accessToken) {
        try {
            String url = this.oauthServerUrl + "/" + accessToken;
            RestTemplate rt = new RestTemplate();
            String result = rt.getForObject(url, String.class);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

/*
        GenericURL url = GenericURL.createGenericURL(this.oauthServerUrl + "/" + accessToken);
        Client client = new Client();
        GenericHttpGet protocol = new GenericHttpGet();
        client.setProcessor(protocol);
        client.setUrl(url);

        try {

            client.connect();
            return new String(client.getResult());
            //System.out.println(""+client.result.length+":"+Utility.toHexString(Utility.arrayCopy(client.result,client.result.length-1000,1000)));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }*/
    }
    
}
