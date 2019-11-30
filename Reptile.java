package org.lay.reptile;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author liushaowu
 * @date 2019/11/30 22:23
 */
public class Reptile {

    public static void main(String[] args) {
        // 爬取地址
        String url1 = "http://henan.chinatax.gov.cn/003/xxgk_301/tzgg_30114/30114_list_0.html?NVG=1&LM_ID=30114";
        // 构建输入流
        InputStream is = null;
        // 包装流,加快读取速度
        BufferedReader br = null;
        //用来保存读取页面的数据.
        StringBuffer html = new StringBuffer();
        //创建临时字符串用于保存每一次读的一行数据，然后html调用append方法写入temp;
        String temp = "";
        try {
            // 获取URL;
            URL url2 = new URL(url1);
            // 打开流，准备开始读取数据;
            HttpURLConnection urlConnection = (HttpURLConnection) url2.openConnection();
            urlConnection.addRequestProperty("User-Agent", "Mozilla/4.76");
            is = url2.openStream();
            // 将流包装成字符流，调用br.readLine()可以提高读取效率，每次读取一行;
            br = new BufferedReader(new InputStreamReader(is));
            // 读取数据,调用br.readLine()方法每次读取一行数据,并赋值给temp,如果没数据则值==null,跳出循环;
            while ((temp = br.readLine()) != null) {
                //将temp的值追加给html,这里注意的时String跟StringBuffere的区别前者不是可变的后者是可变的;
                html.append(temp);
            }
            // 接下来是关闭流,防止资源的浪费;
            if (is != null) {
                is.close();
                is = null;
            }
            //通过Jsoup解析页面,生成一个document对象;
            Document doc = Jsoup.parse(html.toString());
            //通过class的名字得到（即XX）,一个数组对象Elements里面有我们想要的数据,至于这个div的值呢你打开浏览器按下F12就知道了;
            Elements elements = doc.getElementsByClass("m_tab");
            Elements liData = elements.select("li");
            System.out.println("采集到数据：" + liData.size() +"条");
            for (Element element : liData) {
                //打印出每一个节点的信息;你可以选择性的保留你想要的数据,一般都是获取个固定的索引;
                String title = element.select("a").attr("title");
                String href = element.select("a").attr("href");
                String date = element.select("span").text();
                String issueUnit = url1 + href;
                CloseableHttpClient aDefault = HttpClients.createDefault();
                HttpGet httpGet = new HttpGet(issueUnit);
                CloseableHttpResponse response = aDefault.execute(httpGet);
                HttpEntity entity = response.getEntity();
                if (!StringUtils.isEmpty(href)) {
                    String res = EntityUtils.toString(entity);
                    // 二次解析
                    Document parse = Jsoup.parse(res);
                    Elements elements1 = parse.getElementsByClass("m_fb_info clearfix");
                }
                System.out.println(title);
                System.out.println(href);
                System.out.println(date);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}