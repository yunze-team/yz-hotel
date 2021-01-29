package com.yzly.spider.service;

import com.yzly.spider.processor.FLMiaoPageProcessor;
import com.yzly.spider.webmagic.HttpClientDownloader;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author lazyb
 * @create 2021/1/27
 * @desc
 **/
@Service
@CommonsLog
public class FLMiaoSpiderService {

    public void spiderPageTest() {
        Spider.create(new FLMiaoPageProcessor()).
                setDownloader(new HttpClientDownloader()).
                addUrl("https://www.flmiao.xyz").
                addPipeline(new JsonFilePipeline("E:\\data\\flmiao")).thread(5).run();
    }

    public void showSupportSSL() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);

        SSLSocketFactory factory = (SSLSocketFactory) context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket();

        String[] protocols = socket.getSupportedProtocols();

        System.out.println("Supported Protocols: " + protocols.length);
        for (int i = 0; i < protocols.length; i++) {
            log.info(" " + protocols[i]);
        }

        protocols = socket.getEnabledProtocols();

        System.out.println("Enabled Protocols: " + protocols.length);
        for (int i = 0; i < protocols.length; i++) {
            log.info(" " + protocols[i]);
        }
    }

}
