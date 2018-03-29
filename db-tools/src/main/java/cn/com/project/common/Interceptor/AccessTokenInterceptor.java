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
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author hanchuanjun
 */
public class AccessTokenInterceptor extends HandlerInterceptorAdapter {
    //NursingSystem@Renji2016 96dfc16e154aff86f340097f89222006
    //A+Web@Renji2016 1c16409c061db16ab2d0a70c20bfd94d

    private String oauthServerUrl = "";
    private ObjectMapper objectMapper;

    private String nursing_api_key;
    private String aplus_web_api_key;
    
    private String filterSwitch;

    public String getNursing_api_key() {
        return nursing_api_key;
    }

    public void setNursing_api_key(String nursing_api_key) {
        this.nursing_api_key = nursing_api_key;
    }

    public String getAplus_web_api_key() {
        return aplus_web_api_key;
    }

    public void setAplus_web_api_key(String aplus_web_api_key) {
        this.aplus_web_api_key = aplus_web_api_key;
    }

    public void setOauthServerUrl(String oauthServerUrl) {
        this.oauthServerUrl = oauthServerUrl;

    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setFilterSwitch(String filterSwitch) {
        this.filterSwitch = filterSwitch;
    }
    
    

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        Map<String, Object> map = null;
        if (this.filterSwitch == null || !this.filterSwitch.equalsIgnoreCase("on")){
            return true;
        }
    //1、请求到登录页面 放行  
//    if(request.getServletPath().startsWith(loginUrl)) {  
//        return true;  
//    }  

        //2、TODO 比如退出、首页等页面无需登录，即此处要放行 允许游客的请求  
        //3、如果用户已经登录 放行    
        String accessToken = request.getParameter("access_token");
        if (accessToken != null) {
            try {
                String json = this.getUserInfo(accessToken);

                map = this.objectMapper.readValue(json, Map.class);

                String result = (String) map.get("result");
                if (result != null && result.equals("ok")) {
                    Map<String, Object> userInfo = (Map) map.get("user");
                    String uId = (String) userInfo.get("id");

                    String uId_in_url = request.getParameter("uId");
                    if (uId_in_url != null && !uId_in_url.equalsIgnoreCase(uId)) {

                        response.setContentType("application/json;utf-8");
                        map = new HashMap<String, Object>();
                        map.put("result", "error");
                        map.put("code", 500);
                        map.put("description", "Illegal uId in requset parameters!");
                        response.getOutputStream().print(objectMapper.writeValueAsString(map));

                        return false;
                    }
//                    Map m = new HashMap(request.getParameterMap());
//                    m.put("uId", uId);
//                    ParameterRequestWrapper wrapRequest = new ParameterRequestWrapper(request, m);
//
//                    request = wrapRequest; //这是rquest就和本身的request一样了 

                    //request.getParameterMap().put("uId", uId);
                    map.clear();
                    //userInfo.clear();
                    return true;
                } else {
                    response.setContentType("application/json;utf-8");
                    response.getOutputStream().print(json);

                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();

                response.setContentType("application/json;utf-8");
                map = new HashMap<String, Object>();
                map.put("result", "error");
                map.put("code", 500);
                map.put("description", "server error:" + e.getMessage());
                response.getOutputStream().print(objectMapper.writeValueAsString(map));

                return false;
            }
        } else {
            String apiKey = request.getParameter("api_key");

            if (apiKey == null) {
                response.setContentType("application/json;utf-8");
                map = new HashMap<String, Object>();
                map.put("result", "error");
                map.put("code", 401);
                map.put("description", "Unauthized access without a valid access_token!");
                response.getOutputStream().print(objectMapper.writeValueAsString(map));

                return false;
            } else {
                //A+Web@Renji2016 1c16409c061db16ab2d0a70c20bfd94d
                if (nursing_api_key != null && !nursing_api_key.equals("") && apiKey.equalsIgnoreCase(nursing_api_key)) {
                    return true;

                } else if (aplus_web_api_key != null && !aplus_web_api_key.equals("") && apiKey.equalsIgnoreCase(this.aplus_web_api_key)) {

                    return true;
                } else {
                    response.setContentType("application/json;utf-8");
                    map = new HashMap<String, Object>();
                    map.put("result", "error");
                    map.put("code", 401);
                    map.put("description", "Invalid access_token!");
                    response.getOutputStream().print(objectMapper.writeValueAsString(map));
                    return false;
                }
            }
        }

    }

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

        /*GenericURL url = GenericURL.createGenericURL(this.oauthServerUrl + "/" + accessToken);
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
