package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class GetPageLinks {
    public static void main(String[] args) throws IOException {
        Validate.isTrue(args.length == 1, "Usage: supply url to fetch");
        String url = args[0];
        System.out.println("Fetching ..." + url);

        Document document = Jsoup.connect(url).get();
//        System.out.println(document);//解析HTML得到一个文档

        Elements links = document.select("a[href]");
//        System.out.println(links);  //选择a[href]元素

        Elements medias = document.select("[src]");
//        System.out.println(medias);  //一般图片、脚本之类的是以src形式嵌入

        Elements imports = document.select("link[href]");
//        System.out.println(imports); 

        print("Links: (%d)", links.size());
        for(Element link : links){
            print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
        }

        print("Medias: (%d)", medias.size());
        for(Element media : medias){
            print(" * %s: <%s>", media.tagName(), media.attr("abs:src"));
        }

        print("Imports: (%d)", imports.size());
        for(Element imp : imports){
            print(" * %s <%s> (%s)", imp.tagName(),imp.attr("abs:href"), imp.attr("rel"));
        }
    }

    private static void print(String msg, Object... args){
        System.out.println(String.format(msg, args));
    }

    private static String trim(String str, int width){
        if(str.length() > width)
            return str.substring(0, width+1) + ".";
        else return str;
    }
}