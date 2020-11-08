package paramFuzzer;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;

public class Main {

    public static ArrayList<String> ReadParams(String filename) {
        ArrayList<String> paramList = new ArrayList<String>();
        try {
            //String filename = "/Users/CoolCat/tools/burp/fuzzDicts/paramDict/php.txt";
            File file = new File(filename);
            String line = null;
            if (file.exists()) {
                BufferedReader in = new BufferedReader(new FileReader(filename));
                while ((line = in.readLine()) != null) {
                    //System.out.println(line);
                    line = line.replaceAll("\n", "").replaceAll("\r", "");
                    paramList.add(line);
                }
                in.close();
            }else {
                System.out.println("[!]params.txt not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(paramList);
        return paramList;
    }

    public static boolean simhash(boolean Dynamic, String res, String raw1, String raw2) {

        boolean flag = false;
        if (!Dynamic) {
            if (!res.equals(raw2)) {
                flag = true;
            }
        } else {

            //字符长度差异高于3个字符串就视作变化了，对付时间戳很好用
            if (raw1.length() - raw2.length() == 0) {
                if (res.length() != raw1.length())
                    flag = true;
            }

            //差异大于20%就视作返回内容变化了
            CompareStrSimUtil simhash = new CompareStrSimUtil();
            float raw = simhash.simCalc(raw1, raw2);
            //System.out.println("raw=========>" + raw);
            float sim = simhash.simCalc(res, raw2);

            //if (res.contains("CoolCat")){
            //System.out.println(true);
            //System.out.println(raw-sim);
            //}
            //System.out.println("sim=========>" + sim+"\n");
            if (raw > sim + 0.2) {
                flag = true;
            }


            //System.out.println("动态算法");
        }
        return flag;

    }

    public static void split(String[] requrl, String res, Requests requests, boolean Dynamic, String raw1, String raw2) {
        //System.out.println(requrl[0]);

        String part1 = "";
        String part2 = "";

        String[] params = requrl[1].split("&");
        if (params.length > 2) {
            part1 = requrl[1].split(params[params.length / 2])[0] + params[params.length / 2];
            String temp_part2 = requrl[1].split(params[params.length / 2])[1];
            StringBuilder temp_part22 = new StringBuilder(temp_part2).replace(0, 1, "");
            part2 = temp_part22.toString();
        } else if (params.length == 2) {
            part1 = params[0];
            part2 = params[1];
        } else {
            System.out.printf("[+]%s?%s\n", requrl[0], params[0]);
        }

        res = requests.get(requrl[0], part1);
        boolean flag2 = simhash(Dynamic, res, raw1, raw2);

        if (flag2) {
            System.out.println("[+]split:" + part1.length());
            String url = requrl[0] + "?" + part1;
            String[] requrl2 = url.split("\\?");
            split(requrl2, res, requests, Dynamic, raw1, raw2);
        }

        res = requests.get(requrl[0], part2);
        boolean flag3 = simhash(Dynamic, res, raw1, raw2);
        if (flag3) {
            System.out.println("[+]split:" + part2.length());
            String url = requrl[0] + "?" + part2;
            String[] requrl3 = url.split("\\?");
            split(requrl3, res, requests, Dynamic, raw1, raw2);
        }

    }

    public static void split2(String requrl, String res, Requests requests, boolean Dynamic, String raw1, String raw2, String allParams) {

        //System.out.println(allParams);

        String part1 = "";
        String part2 = "";

        String[] params = allParams.split("&");
        if (params.length > 2) {
            part1 = allParams.split(params[params.length / 2])[0] + params[params.length / 2];
            String temp_part2 = allParams.split(params[params.length / 2])[1];
            StringBuilder temp_part22 = new StringBuilder(temp_part2).replace(0, 1, "");
            part2 = temp_part22.toString();
        } else if (params.length == 2) {
            part1 = params[0];
            part2 = params[1];
        } else if (params.length < 2) {
            System.out.printf("[+]%s\tPOST: %s\n", requrl, params[0]);
            //System.exit(0);
        }

        res = requests.post(requrl, part1);
        boolean flag2 = simhash(Dynamic, res, raw1, raw2);

        if (flag2) {
            System.out.println("[+]split:" + part1.split("&").length);
            split2(requrl, res, requests, Dynamic, raw1, raw2, part1);
        }

        res = requests.post(requrl, part2);
        boolean flag3 = simhash(Dynamic, res, raw1, raw2);
        if (flag3) {
            System.out.println("[+]split:" + part2.split("&").length);
            split2(requrl, res, requests, Dynamic, raw1, raw2, part2);
        }

    }

    public static void main(String[] args){

        String logo = "\n" +
                "                                 ______                      \n" +
                "       version:0.1              |  ____|                     \n" +
                " _ __   __ _ _ __ __ _ _ __ ___ | |__ _   _ ___________ _ __ \n" +
                "| '_ \\ / _` | '__/ _` | '_ ` _ \\|  __| | | |_  /_  / _ \\ '__|\n" +
                "| |_) | (_| | | | (_| | | | | | | |  | |_| |/ / / /  __/ |   \n" +
                "| .__/ \\__,_|_|  \\__,_|_| |_| |_|_|   \\__,_/___/___\\___|_|   \n" +
                "| |                     Coding by CoolCat                        \n" +
                "|_|            https://github.com/TheKingOfDuck                   \n";

        System.out.println(logo);

        try {
            //System.out.println("aa");
            CommandLineParser parser = new BasicParser();
            Options options = new Options();

            //url参数为必须
            options.addOption("u", "url", true, "Target Url");
            //m参数为必须
            options.addOption("m", "method", true, "Requests Method");
            //param参数
            options.addOption("p", "param", true, "Params File");

            options.addOption("v", "value", true, "Params value");
            //read参数 读取数据包暂不考虑
            //options.addOption("r","read",true,"Requests Package");
            //help参数
            options.addOption("h", "help", false, "Help Info");

            CommandLine commandLine = parser.parse(options, args);

            String helpinfo = "[?]CommandLine:\n" +
                    "\t-u\t--url\t\ttarget url\n" +
                    "\t-m\t--method\trequests method\n" +
                    "\t-p\t--param\t\tparam file\n" +
                    "\t-v\t--value\t\tparam value\n" +
                    "\t-h\t--help\t\thelp info\n\n" +
                    "eg:java -jar paramFuzzer.jar -u http://test.com/index.php -m get -p params.txt -v hello";
            if (commandLine.hasOption("h")) {
                System.out.println(helpinfo);
                System.exit(0);
            }


            String param_file = "params.txt";
            if (commandLine.hasOption("p")) {
                param_file = commandLine.getOptionValue("p");
            } else {
                param_file = "params.txt";
            }
            ArrayList paramList = ReadParams(param_file);
            //System.out.println(paramList);
            String paramValue = "1";

            if (commandLine.hasOption("v")) {
                paramValue = commandLine.getOptionValue("v");
                //System.out.println(paramValue);
            }

            if (commandLine.hasOption("u") & commandLine.hasOption("m")) {
                String method = commandLine.getOptionValue("m");
                String url = commandLine.getOptionValue("u");
                Requests requests = new Requests();
                String raw1 = requests.get(url, "paramFuzzer=1.0");
                String raw2 = requests.get(url, "paramFuzzer=2.0");

                //如果返回包有变化就用相似度算法进行比较
                boolean Dynamic = true;
                if (raw1.equals(raw2)) {
                    Dynamic = false;
                }

                System.out.printf("[+]Dynamic Response: %s\n\tProgram's judgment is not correct every time\n", Dynamic);


                if (method.toLowerCase().equals("get")) {
                    //System.out.println(paramList.size());

                    ////GET最大长度限制是8198
                    for (int i = 0; i < paramList.size(); i++) {

                        if (url.contains("?")) {
                            url = url + "&" + paramList.get(i) + "=" + paramValue;
                        } else {
                            url = url + "?" + paramList.get(i) + "=" + paramValue;
                        }
                        //System.out.println(url);

                        if (url.length() > 8000) {
                            //System.out.println(url);

                            String[] requrl = url.split("\\?");
                            //System.out.println(requrl[1]);

                            String res = requests.get(requrl[0], requrl[1]);
                            boolean flag = simhash(Dynamic, res, raw1, raw2);

                            if (flag) {
                                System.out.println("[+]Got:" + url.length());
                                split(requrl, res, requests, Dynamic, raw1, raw2);
                            }
                            url = commandLine.getOptionValue("u");
                        }
                        if (i == paramList.size() - 1) {
                            //System.out.println(url);

                            String[] requrl = url.split("\\?");
                            //System.out.println(requrl[1]);

                            String res = requests.get(requrl[0], requrl[1]);
                            boolean flag = simhash(Dynamic, res, raw1, raw2);

                            if (flag) {
                                System.out.println("[+]Got:" + url.length());
                                split(requrl, res, requests, Dynamic, raw1, raw2);
                            }
                        }

                    }

                } else if (method.toLowerCase().equals("post")) {

                    String tempParams = "paramFuzzer=byCoolCat";
                    int m = 0;
                    for (int i = 0; i < paramList.size(); i++) {
                        m++;
                        if (m > 998) {
                            //System.out.println(tempParams);

                            String res3 = requests.post(url, tempParams);
                            boolean flag = simhash(Dynamic, res3, raw1, raw2);

                            if (flag) {
                                System.out.println("[+]Got:" + url);
                                split2(url, res3, requests, Dynamic, raw1, raw2, tempParams);
                            }

                            tempParams = "paramFuzzer=byCoolCat";
                            m = 0;
                        }

                        //System.out.println(m);

                        tempParams = tempParams + "&" + paramList.get(i) + "=" + paramValue;

                        if (i == paramList.size() - 1) {
                            //System.out.println(tempParams);
                            String res3 = requests.post(url, tempParams);
                            boolean flag = simhash(Dynamic, res3, raw1, raw2);

                            if (flag) {
                                System.out.println("[+]Got:" + url);
                                split2(url, res3, requests, Dynamic, raw1, raw2, tempParams);
                            }
                        }

                    }
                }else {
                    System.out.printf("[!]%s method not support",commandLine.getOptionValue("m"));
                    System.exit(0);
                }
            } else {
                System.out.println("[!]java -jar paramFuzzer.jar -u http://test.com:8888/index.php -m get -p params.txt -v CoolCat");
                System.exit(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
