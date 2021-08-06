package com.database.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String字符串的高级操作
 * @author Will Shi
 * @since 2021/8/6
 */
@Slf4j
public class HighStringUtil {
    /**
     * 提取圆括号里面的内容
     * @author Will Shi
     * @since 2021/8/6
     */
    public static List<String> extractParenthesisContent(String string){
        List<String> list=new ArrayList<>();
        String pattern="(\\([^\\)]+\\))"; //正则表达式，匹配括号内容
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(string);
        while(m.find()){
            String group = m.group();
            list.add(group.substring(1,group.length()-1));
        }
        return list;
    }

    public static void main(String[] args) {
        String s="dsaf(323)ldsao,(sd)"; //示例文本
        List<String> strings = extractParenthesisContent(s);
        log.info("{}", JSON.toJSONString(strings));
    }
}
