package paramFuzzer;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class Requests {


    public static void setProxy(boolean proxy) {
        if (proxy){
            HttpRequest.proxyHost("127.0.0.1");
            HttpRequest.proxyPort(8080);
        }
    }

    public static String get(String url,String params) {

        setProxy(false);
        String param_url = String.format("%s?%s",url,params);

        try {
            Map<String,String> Headers=new HashMap<String,String>();

            Headers.put("User-Agent","paramFuzzer v0.1");

            HttpRequest res = HttpRequest.get(param_url).headers(Headers).followRedirects(false).readTimeout(5000);

            //System.out.println(String.format("StatCode:%s",res.code()));
            //System.out.println(String.format("Response:%s",res.body()));
            return res.body();
        }catch (Exception e){
            System.out.println(e);
            return "err";
        }
    }

    public static String post(String url,String params) {

        setProxy(false);

        try {

            Map<String,String> Headers=new HashMap<String,String>();

            Headers.put("User-Agent","paramFuzzer v0.1");
            String Data = params;

            HttpRequest res = HttpRequest.post(url).headers(Headers).send(Data).followRedirects(false).readTimeout(5000);

            //System.out.println(String.format("StatCode:%s",res.code()));
            //System.out.println(String.format("Response:%s",res.body()));



            return res.body();
        }catch (Exception e){
            System.out.println(e);
            return "err";
        }
    }





}
