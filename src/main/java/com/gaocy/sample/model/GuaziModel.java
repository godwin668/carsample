package com.gaocy.sample.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gaocy.sample.spider.SpiderBase;
import com.gaocy.sample.util.HttpClientUtil;
import com.gaocy.sample.vo.ModelVo;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by godwin on 2017-06-06.
 */
public class GuaziModel {

    public static Map<String, ModelVo> seriesMap = new HashMap<String, ModelVo>();

    static {
        loadSeries();
    }

    private static void loadSeries() {
        ClassLoader classLoader = GuaziModel.class.getClassLoader();
        File file = new File(classLoader.getResource("model/guazi_series.txt").getFile());
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            System.out.println(lines.size());
            for (String line : lines) {
                if (line.matches("\\d+.*")) {
                    String[] modelArr = line.split("\t");
                    String brandId = modelArr[0];
                    String brandName = modelArr[1];
                    String seriesId = modelArr[2];
                    String seriesName = modelArr[3];
                    ModelVo series = new ModelVo();
                    series.setBrandId(brandId);
                    series.setBrandName(brandName);
                    series.setSeriesId(seriesId);
                    series.setSeriesName(seriesName);
                    // System.out.println(JSON.toJSONString(series));
                    seriesMap.put(seriesId, series);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<String, ModelVo> getSeriesMap() {
        return seriesMap;
    }

    public static ModelVo getSeries(String seriesId) {
        return seriesMap.get(seriesId);
    }

    public static void main(String[] args) {

    }

    /**
     * generate guazi series
     */
    public static void genSeries() {
        String brandJson = "[{\"brandId\":\"1198\",\"brandName\":\"宝马\"},{\"brandId\":\"1197\",\"brandName\":\"奔驰\"},{\"brandId\":\"1199\",\"brandName\":\"奥迪\"},{\"brandId\":\"1207\",\"brandName\":\"大众\"},{\"brandId\":\"1205\",\"brandName\":\"福特\"},{\"brandId\":\"1195\",\"brandName\":\"丰田\"},{\"brandId\":\"1196\",\"brandName\":\"本田\"},{\"brandId\":\"1202\",\"brandName\":\"日产\"},{\"brandId\":\"1199\",\"brandName\":\"奥迪\"},{\"brandId\":\"1217\",\"brandName\":\"阿尔法·罗密欧\"},{\"brandId\":\"1218\",\"brandName\":\"阿斯顿·马丁\"},{\"brandId\":\"1219\",\"brandName\":\"安驰\"},{\"brandId\":\"103764\",\"brandName\":\"Alpina\"},{\"brandId\":\"103803\",\"brandName\":\"AC Schnitzer\"},{\"brandId\":\"103811\",\"brandName\":\"奥驰\"},{\"brandId\":\"103819\",\"brandName\":\"安凯客车\"},{\"brandId\":\"1196\",\"brandName\":\"本田\"},{\"brandId\":\"1197\",\"brandName\":\"奔驰\"},{\"brandId\":\"1198\",\"brandName\":\"宝马\"},{\"brandId\":\"1206\",\"brandName\":\"别克\"},{\"brandId\":\"1212\",\"brandName\":\"比亚迪\"},{\"brandId\":\"1221\",\"brandName\":\"宝龙\"},{\"brandId\":\"1222\",\"brandName\":\"宾利\"},{\"brandId\":\"1223\",\"brandName\":\"北汽\"},{\"brandId\":\"1224\",\"brandName\":\"保时捷\"},{\"brandId\":\"1225\",\"brandName\":\"奔腾\"},{\"brandId\":\"1226\",\"brandName\":\"标致\"},{\"brandId\":\"2259\",\"brandName\":\"布加迪\"},{\"brandId\":\"2270\",\"brandName\":\"北京\"},{\"brandId\":\"2272\",\"brandName\":\"宝骏\"},{\"brandId\":\"102721\",\"brandName\":\"巴博斯\"},{\"brandId\":\"103774\",\"brandName\":\"保斐利\"},{\"brandId\":\"103779\",\"brandName\":\"北汽威旺\"},{\"brandId\":\"103780\",\"brandName\":\"北汽幻速\"},{\"brandId\":\"103783\",\"brandName\":\"北汽绅宝\"},{\"brandId\":\"103798\",\"brandName\":\"宝沃汽车\"},{\"brandId\":\"103807\",\"brandName\":\"比速汽车\"},{\"brandId\":\"103826\",\"brandName\":\"北汽新能源\"},{\"brandId\":\"1227\",\"brandName\":\"长安\"},{\"brandId\":\"1228\",\"brandName\":\"长城\"},{\"brandId\":\"1229\",\"brandName\":\"昌河\"},{\"brandId\":\"2266\",\"brandName\":\"川汽野马\"},{\"brandId\":\"103772\",\"brandName\":\"传祺\"},{\"brandId\":\"103787\",\"brandName\":\"长安商用\"},{\"brandId\":\"103799\",\"brandName\":\"成功汽车\"},{\"brandId\":\"103812\",\"brandName\":\"长安跨越\"},{\"brandId\":\"1207\",\"brandName\":\"大众\"},{\"brandId\":\"1230\",\"brandName\":\"东风\"},{\"brandId\":\"1231\",\"brandName\":\"大迪\"},{\"brandId\":\"1232\",\"brandName\":\"大宇\"},{\"brandId\":\"1233\",\"brandName\":\"大发\"},{\"brandId\":\"1234\",\"brandName\":\"东南\"},{\"brandId\":\"1235\",\"brandName\":\"道奇\"},{\"brandId\":\"1239\",\"brandName\":\"东风风行\"},{\"brandId\":\"2262\",\"brandName\":\"东风风神\"},{\"brandId\":\"102712\",\"brandName\":\"东风小康\"},{\"brandId\":\"103765\",\"brandName\":\"DS\"},{\"brandId\":\"103769\",\"brandName\":\"东风风度\"},{\"brandId\":\"103813\",\"brandName\":\"缔途\"},{\"brandId\":\"1195\",\"brandName\":\"丰田\"},{\"brandId\":\"1205\",\"brandName\":\"福特\"},{\"brandId\":\"1215\",\"brandName\":\"福田\"},{\"brandId\":\"1236\",\"brandName\":\"福迪\"},{\"brandId\":\"1237\",\"brandName\":\"法拉利\"},{\"brandId\":\"1238\",\"brandName\":\"富奇\"},{\"brandId\":\"1240\",\"brandName\":\"菲亚特\"},{\"brandId\":\"102709\",\"brandName\":\"福汽启腾\"},{\"brandId\":\"1241\",\"brandName\":\"光冈\"},{\"brandId\":\"1242\",\"brandName\":\"GMC\"},{\"brandId\":\"1260\",\"brandName\":\"广汽吉奥\"},{\"brandId\":\"102708\",\"brandName\":\"观致\"},{\"brandId\":\"1247\",\"brandName\":\"黑豹\"},{\"brandId\":\"1248\",\"brandName\":\"哈飞\"},{\"brandId\":\"1252\",\"brandName\":\"海马\"},{\"brandId\":\"1253\",\"brandName\":\"悍马\"},{\"brandId\":\"1254\",\"brandName\":\"华普\"},{\"brandId\":\"1255\",\"brandName\":\"红旗\"},{\"brandId\":\"1256\",\"brandName\":\"华泰\"},{\"brandId\":\"1259\",\"brandName\":\"汇众\"},{\"brandId\":\"1316\",\"brandName\":\"华北\"},{\"brandId\":\"102471\",\"brandName\":\"黄海\"},{\"brandId\":\"102707\",\"brandName\":\"恒天\"},{\"brandId\":\"103777\",\"brandName\":\"华阳\"},{\"brandId\":\"103778\",\"brandName\":\"哈弗\"},{\"brandId\":\"103782\",\"brandName\":\"海格\"},{\"brandId\":\"103795\",\"brandName\":\"华颂\"},{\"brandId\":\"103797\",\"brandName\":\"汉腾\"},{\"brandId\":\"103808\",\"brandName\":\"华凯\"},{\"brandId\":\"103823\",\"brandName\":\"华泰新能源\"},{\"brandId\":\"1200\",\"brandName\":\"吉利\"},{\"brandId\":\"1216\",\"brandName\":\"Jeep\"},{\"brandId\":\"1261\",\"brandName\":\"金杯\"},{\"brandId\":\"1262\",\"brandName\":\"捷豹\"},{\"brandId\":\"1263\",\"brandName\":\"金程\"},{\"brandId\":\"1264\",\"brandName\":\"江淮\"},{\"brandId\":\"1265\",\"brandName\":\"江铃\"},{\"brandId\":\"2265\",\"brandName\":\"九龙\"},{\"brandId\":\"102711\",\"brandName\":\"解放\"},{\"brandId\":\"103773\",\"brandName\":\"佳星\"},{\"brandId\":\"103781\",\"brandName\":\"江南\"},{\"brandId\":\"103804\",\"brandName\":\"金旅\"},{\"brandId\":\"103805\",\"brandName\":\"金龙\"},{\"brandId\":\"103824\",\"brandName\":\"江铃集团轻汽\"},{\"brandId\":\"103825\",\"brandName\":\"江铃集团新能源\"},{\"brandId\":\"1268\",\"brandName\":\"凯迪拉克\"},{\"brandId\":\"1269\",\"brandName\":\"克莱斯勒\"},{\"brandId\":\"2258\",\"brandName\":\"开瑞\"},{\"brandId\":\"102464\",\"brandName\":\"科尼赛克\"},{\"brandId\":\"102466\",\"brandName\":\"凯佰赫\"},{\"brandId\":\"102705\",\"brandName\":\"卡威\"},{\"brandId\":\"102706\",\"brandName\":\"卡尔森\"},{\"brandId\":\"103775\",\"brandName\":\"凯翼\"},{\"brandId\":\"103809\",\"brandName\":\"凯马\"},{\"brandId\":\"103827\",\"brandName\":\"康迪\"},{\"brandId\":\"1209\",\"brandName\":\"铃木\"},{\"brandId\":\"1270\",\"brandName\":\"猎豹\"},{\"brandId\":\"1271\",\"brandName\":\"兰博基尼\"},{\"brandId\":\"1272\",\"brandName\":\"罗孚\"},{\"brandId\":\"1273\",\"brandName\":\"陆风\"},{\"brandId\":\"1274\",\"brandName\":\"力帆\"},{\"brandId\":\"1276\",\"brandName\":\"路虎\"},{\"brandId\":\"1277\",\"brandName\":\"林肯\"},{\"brandId\":\"1278\",\"brandName\":\"雷克萨斯\"},{\"brandId\":\"1279\",\"brandName\":\"雷诺\"},{\"brandId\":\"1281\",\"brandName\":\"劳斯莱斯\"},{\"brandId\":\"2269\",\"brandName\":\"理念\"},{\"brandId\":\"102469\",\"brandName\":\"路特斯\"},{\"brandId\":\"103776\",\"brandName\":\"劳伦士\"},{\"brandId\":\"103800\",\"brandName\":\"领志\"},{\"brandId\":\"1211\",\"brandName\":\"马自达\"},{\"brandId\":\"1282\",\"brandName\":\"迈巴赫\"},{\"brandId\":\"1284\",\"brandName\":\"MINI\"},{\"brandId\":\"1285\",\"brandName\":\"玛莎拉蒂\"},{\"brandId\":\"1286\",\"brandName\":\"美亚\"},{\"brandId\":\"102716\",\"brandName\":\"迈凯伦\"},{\"brandId\":\"103788\",\"brandName\":\"MG\"},{\"brandId\":\"103818\",\"brandName\":\"摩根\"},{\"brandId\":\"2271\",\"brandName\":\"纳智捷\"},{\"brandId\":\"103806\",\"brandName\":\"南京金龙\"},{\"brandId\":\"103814\",\"brandName\":\"南骏\"},{\"brandId\":\"1288\",\"brandName\":\"欧宝\"},{\"brandId\":\"1289\",\"brandName\":\"讴歌\"},{\"brandId\":\"102481\",\"brandName\":\"欧朗\"},{\"brandId\":\"1290\",\"brandName\":\"庞蒂克\"},{\"brandId\":\"102482\",\"brandName\":\"帕加尼\"},{\"brandId\":\"1201\",\"brandName\":\"奇瑞\"},{\"brandId\":\"1275\",\"brandName\":\"青年莲花\"},{\"brandId\":\"1291\",\"brandName\":\"庆铃\"},{\"brandId\":\"1292\",\"brandName\":\"起亚\"},{\"brandId\":\"102478\",\"brandName\":\"启辰\"},{\"brandId\":\"103821\",\"brandName\":\"前途\"},{\"brandId\":\"1202\",\"brandName\":\"日产\"},{\"brandId\":\"1293\",\"brandName\":\"荣威\"},{\"brandId\":\"2260\",\"brandName\":\"瑞麒\"},{\"brandId\":\"102701\",\"brandName\":\"RUF\"},{\"brandId\":\"1204\",\"brandName\":\"三菱\"},{\"brandId\":\"1214\",\"brandName\":\"萨博\"},{\"brandId\":\"1294\",\"brandName\":\"斯巴鲁\"},{\"brandId\":\"1296\",\"brandName\":\"双环\"},{\"brandId\":\"1297\",\"brandName\":\"斯柯达\"},{\"brandId\":\"1299\",\"brandName\":\"双龙\"},{\"brandId\":\"2253\",\"brandName\":\"世爵\"},{\"brandId\":\"2273\",\"brandName\":\"上汽大通\"},{\"brandId\":\"102474\",\"brandName\":\"思铭\"},{\"brandId\":\"102476\",\"brandName\":\"Smart\"},{\"brandId\":\"102700\",\"brandName\":\"陕汽通家\"},{\"brandId\":\"103766\",\"brandName\":\"Scion\"},{\"brandId\":\"103767\",\"brandName\":\"SPRINGO\"},{\"brandId\":\"103786\",\"brandName\":\"赛宝\"},{\"brandId\":\"103801\",\"brandName\":\"赛麟\"},{\"brandId\":\"103802\",\"brandName\":\"斯威\"},{\"brandId\":\"103810\",\"brandName\":\"时代\"},{\"brandId\":\"103815\",\"brandName\":\"四川现代\"},{\"brandId\":\"1300\",\"brandName\":\"天马\"},{\"brandId\":\"1301\",\"brandName\":\"通田\"},{\"brandId\":\"2257\",\"brandName\":\"通用\"},{\"brandId\":\"102715\",\"brandName\":\"特斯拉\"},{\"brandId\":\"102717\",\"brandName\":\"腾势\"},{\"brandId\":\"103816\",\"brandName\":\"唐骏汽车\"},{\"brandId\":\"1203\",\"brandName\":\"沃尔沃\"},{\"brandId\":\"1304\",\"brandName\":\"五十铃\"},{\"brandId\":\"1305\",\"brandName\":\"五菱\"},{\"brandId\":\"2264\",\"brandName\":\"威麟\"},{\"brandId\":\"102714\",\"brandName\":\"威兹曼\"},{\"brandId\":\"103768\",\"brandName\":\"万丰\"},{\"brandId\":\"103822\",\"brandName\":\"蔚来\"},{\"brandId\":\"103829\",\"brandName\":\"WEY\"},{\"brandId\":\"1208\",\"brandName\":\"现代\"},{\"brandId\":\"1213\",\"brandName\":\"雪铁龙\"},{\"brandId\":\"1307\",\"brandName\":\"雪佛兰\"},{\"brandId\":\"1308\",\"brandName\":\"新凯\"},{\"brandId\":\"1309\",\"brandName\":\"新雅途\"},{\"brandId\":\"2255\",\"brandName\":\"西雅特\"},{\"brandId\":\"102465\",\"brandName\":\"夏利\"},{\"brandId\":\"103785\",\"brandName\":\"西安奥拓\"},{\"brandId\":\"1210\",\"brandName\":\"一汽\"},{\"brandId\":\"1244\",\"brandName\":\"云雀\"},{\"brandId\":\"1310\",\"brandName\":\"英菲尼迪\"},{\"brandId\":\"1311\",\"brandName\":\"依维柯\"},{\"brandId\":\"1313\",\"brandName\":\"扬子\"},{\"brandId\":\"2251\",\"brandName\":\"英伦\"},{\"brandId\":\"2261\",\"brandName\":\"永源\"},{\"brandId\":\"103784\",\"brandName\":\"英致\"},{\"brandId\":\"103828\",\"brandName\":\"烟台舒驰\"},{\"brandId\":\"1315\",\"brandName\":\"中华\"},{\"brandId\":\"1318\",\"brandName\":\"众泰\"},{\"brandId\":\"1319\",\"brandName\":\"中兴\"},{\"brandId\":\"103770\",\"brandName\":\"中欧\"},{\"brandId\":\"103771\",\"brandName\":\"中顺\"},{\"brandId\":\"103796\",\"brandName\":\"知豆\"},{\"brandId\":\"103817\",\"brandName\":\"重汽王牌\"},{\"brandId\":\"103820\",\"brandName\":\"之诺\"}]";
        String brandUrlTemplate = "https://www.guazi.com/bj/sell?act=ajaxgettaginfo&brandId=<brandId>";

        List<Map<String, String>> brandMapList = JSON.parseObject(brandJson, List.class);
        System.out.println("size: " + brandMapList.size());
        // SpiderBase.logToFile("guazi_series", "品牌ID\t品牌名称\t车系ID\t车系名称");
        for (Map<String, String> brandMap : brandMapList) {
            String brandId = brandMap.get("brandId");
            String brandName = brandMap.get("brandName");
            System.out.println(brandId + " - " + brandName);
            String brandUrl = brandUrlTemplate.replaceFirst("<brandId>", brandId);
            String brandContent = HttpClientUtil.get(brandUrl);
            if (StringUtils.isNotBlank(brandContent) && brandContent.contains("msg")) {
                JSONObject brandObj = JSON.parseObject(brandContent);
                JSONArray seriesArr = brandObj.getJSONArray("msg");
                for (Object series : seriesArr) {
                    JSONObject seriesObj = (JSONObject) series;
                    String seriesId = (String) seriesObj.get("id");
                    String seriesName = (String) seriesObj.get("name");
                    String info = brandId + "\t" + brandName + "\t" + seriesId + "\t" + seriesName;
                    SpiderBase.logToFile("guazi_series", info);
                }
            } else {
                System.err.println("[ERROR GET URL]:" + brandUrl + ", result:" + brandContent);
            }
            System.out.println(brandContent);
        }
    }
}