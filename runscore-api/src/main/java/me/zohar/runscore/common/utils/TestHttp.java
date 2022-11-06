package me.zohar.runscore.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.Date;


public class TestHttp {
    public static String HttpRestClient(String url, HttpMethod method, JSONObject json) throws IOException {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10*1000);
        requestFactory.setReadTimeout(10*1000);
        RestTemplate client = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON_UTF8);
        HttpEntity<String> requestEntity = new HttpEntity<String>(json.toString(), headers);
        //  执行HTTP请求
        ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
        return response.getBody();
    }

    public static  void  main(String args[]){
        try{
            //api url地址
            String url = "http://144.34.163.64:9000/api/v1/cmd";
            //post请求
            HttpMethod method =HttpMethod.POST;
            JSONObject json = new JSONObject();
            json.put("cmd", "start");
            json.put("card", "12114");
            json.put("bank", "msb");
            json.put("name", "aadfs");
            json.put("pwd", "123456");
            System.out.print("发送数据："+json.toString());
            //发送http请求并返回结果
            String result = HttpUtils.HttpRestClient(url,method,json);

            JSONObject jsonValue= JSONObject.parseObject(result);

            System.out.print("接收反馈："+jsonValue.toJSONString());
        }catch (Exception e){
        }
    }

}
