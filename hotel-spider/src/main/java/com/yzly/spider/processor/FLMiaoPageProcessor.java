package com.yzly.spider.processor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.apachecommons.CommonsLog;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author lazyb
 * @create 2021/1/27
 * @desc
 **/
@CommonsLog
public class FLMiaoPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private String INDEX_URL = "https://www\\.flmiao\\.xyz/\\d+?\\d*";

    @Override
    public void process(Page page) {
        String html = page.getRawText();
        Document document = Jsoup.parse(html);
        if (!page.getUrl().regex(INDEX_URL).match()) {
            // 到首页，获取每页详情
            Elements imgs = document.getElementsByClass("waitpic");
            List<String> urls = new ArrayList<>();
            for (Element img : imgs) {
                String href = img.parent().attr("href");
                urls.add(href);
                // 先只取一页
                //break;
            }
            page.addTargetRequests(urls);
        } else {
            // 到详情页
            // 获取下载按钮
            Elements mypassdown = document.getElementsByClass("Mypassdown");
            if (mypassdown == null) {
                log.info("mypassdown is null");
            } else {
                String url = page.getUrl().toString();
                String regex = "\\d+?\\d*";
                Pattern pattern = Pattern.compile(regex);
                Matcher m = pattern.matcher(url);
                String pid = "";
                while (m.find()) {
                    pid = m.group();
                }
                log.info(pid);
                String dataDown = mypassdown.first().attr("data-down");
                log.info(dataDown);
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                MultiValueMap<String, String> postParameters = new LinkedMultiValueMap<>();
                postParameters.add("pid", pid);
                postParameters.add("down", dataDown);
                postParameters.add("action", "Post_down_ajax");
                HttpEntity<MultiValueMap<String, String>> requestEntity  = new HttpEntity<MultiValueMap<String, String>>(postParameters, headers);
                String res = restTemplate.postForObject("https://www.flmiao.xyz/wp-admin/admin-ajax.php", requestEntity, String.class);
                JSONObject resJson = JSONObject.parseObject(res);
                if (resJson.getInteger("status") == 1) {
                    String data = resJson.getString("data");
                    log.info(data);
//                    String source = decode(data);
//                    log.info(source);
                    try {
                        String source = pydecode(data);
                        page.putField("source", source);
                        log.info(source);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }

    private String pydecode(String data) throws Exception {
        ClassPathResource resource = new ClassPathResource("decode.py");
        InputStream in = resource.getInputStream();
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.execfile(in);
        PyFunction pyFunction = interpreter.get("decode", PyFunction.class);
        PyObject pyObject = pyFunction.__call__(new PyString(data));
        return pyObject.asString();
    }

    private String i(String a, String c, int[] d) {
        String jisuan = a;
        int g = 0;
        int index = 0;
        int i = jisuan.length();
        String j = "";
        int k = 0;
        int l = 0;
        int m = 0;
        while (i > index) {
            l = jisuan.charAt(index);
            if (256 > l) {
                l = d[l];
            } else {
                l = -1;
            }
            g = (g << 6) + l;
            k += 6;
            while (k >= 8) {
                k -= 8;
                m = g >> k;
                j += c.charAt(m);
                g ^= m << k;
            }
            index += 1;
        }
        return j;
    }

    private String decode(String data) {
        data = data.replaceAll("\\*!agf", "=");
        data = data.replaceAll("&a\\^f", "b");
        String b = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
        String c = "";
        int[] d = new int[256];
        int[] e = new int[256];
        for (int i = 0; i < 256; i++) {
            d[i] = -1;
            e[i] = -1;
        }
        int f = 0;
        String h = "";
        for (int i = 0; i < 256; i++) {
            f = i;
            h = new String(Character.toChars(f));
            c += h;
            e[f] = f;
            try {
                d[f] = b.indexOf(h);
            } catch (Exception ex) {
                d[f] = -1;
            }
        }
        String pattern = "[a-zA-Z0-9]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(data);
        while (m.find()) {
            data += m.group();
        }
        return i(data, c, d);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
