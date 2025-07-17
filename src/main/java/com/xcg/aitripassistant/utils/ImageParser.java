package com.xcg.aitripassistant.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class ImageParser {
    private static Map<String,String> attractions = new HashMap<>();
    private static final String AK = "v0t8SBkCLsOfxOcwM7miADkCVBaOG2KT";
    @PostConstruct
    public void init() {
        //杭州景点
        attractions.put("西湖", "西湖是镶嵌在杭州的一颗璀璨明珠，以秀丽湖光山色、深厚人文底蕴和浪漫传说故事闻名于世，宛如一幅灵动的天然山水画卷。");
        attractions.put("灵隐寺", "中国佛教禅宗名刹之一，背靠北高峰，面朝飞来峰，两峰挟峙，林木耸秀。寺内建筑宏伟壮观，佛像庄严肃穆，香火旺盛，是祈福许愿的好地方。");
        attractions.put("千岛湖", "世界上岛屿最多的湖，因湖内拥有1078座翠岛而得名。湖水清澈见底，湖中小岛形态各异，游客可以乘船游览，欣赏湖光山色，还可以品尝当地的有机鱼头汤。");
        attractions.put("宋城", "一座大型宋文化主题公园，以‘建筑为形，文化为魂’为经营理念，仿宋代风格建造，再现了宋代都市的繁华景象。园内有《宋城千古情》大型歌舞表演，运用先进的声、光、电科技手段和舞台机械，演绎了良渚古人的艰辛、宋皇宫的辉煌、岳家军的惨烈、梁祝和白蛇许仙的千古绝唱。");
        attractions.put("西溪国家湿地公园", "国内第一个集城市湿地、农耕湿地、文化湿地于一体的国家级湿地公园，自然景观优美，生态资源丰富。游客可以乘坐摇橹船穿梭在湿地河道中，欣赏芦苇荡、水鸟等自然景观，感受大自然的宁静与和谐。");
        attractions.put("六和塔", "始建于北宋开宝三年（公元970年），塔身自下而上塔檐逐级缩小，塔檐翘角上挂了104只铁铃。登塔远眺，钱塘江大桥和江景尽收眼底，是观赏钱塘江潮和欣赏杭州全景的好地方。");

        //广州景点
        attractions.put("广州塔", "广州塔是中国第一高塔，又称‘小蛮腰’，集观光、餐饮、娱乐于一体。488米观景平台可360°俯瞰珠江新城与琶洲会展区，450米极速云霄跳楼机带来刺激体验，是广州的地标性建筑。");
        attractions.put("白云山风景名胜区", "白云山是广州的城市绿肺，作为全国超大城市中心区最大的人工湿地，四季景色各异。游客可乘坐缆车上山，徒步摩星岭段云道，春季赏禾雀花长廊，秋季在山顶广场俯瞰‘羊城新八景’之一的云山叠翠。");
        attractions.put("陈家祠", "陈家祠是岭南建筑艺术的巅峰之作，这座建于清代的宗祠建筑群被誉为‘岭南建筑艺术明珠’，其‘三雕两塑一彩’（石雕、木雕、砖雕、陶塑、灰塑、彩绘）工艺令人叹为观止，后院还展出广绣、榄雕等非遗技艺。");
        attractions.put("广东省博物馆", "广东省博物馆是大型综合类博物馆，自然展厅的‘须鲸骨骼’标本长达18米，历史展厅的‘南海I号’沉船文物复原宋代海上丝绸之路盛景。每周五延长开放至20:30，夜间场次人少景美，4D影院播放《恐龙时代》，增强沉浸感。");
        attractions.put("沙面岛", "沙面岛是珠江上的小岛，保存着150多座欧陆风情建筑，新巴洛克式的外贸博物馆与折衷主义的露德圣母堂形成奇妙对话。清晨6点前抵达可避开人流拍摄空镜大片，露德圣母堂前的草坪是拍摄婚纱的黄金机位。");

        // 北京景点

        attractions.put("故宫", "故宫是中国明清两代的皇家宫殿，位于北京中轴线中心，是世界上现存规模最大、保存最为完整的木质结构古建筑群之一，红墙黄瓦间尽显皇家气派。");
        attractions.put("长城（八达岭段）", "八达岭长城是明长城的重要隘口，地势险要，建筑雄伟，‘不到长城非好汉’的碑刻吸引着无数游客前来打卡，感受长城的雄伟与历史的厚重。");
        attractions.put("颐和园", "颐和园是中国清朝时期皇家园林，以昆明湖、万寿山为基址，汲取江南园林的设计手法而建成，被誉为‘皇家园林博物馆’，湖光山色间尽显江南水乡的温婉。");
        attractions.put("天坛公园", "天坛是明清两代皇帝‘祭天’‘祈谷’的场所，是中国现存最大的古代祭祀建筑群，祈年殿的蓝瓦金顶在阳光下熠熠生辉，彰显着古代建筑的智慧与艺术。");
        attractions.put("南锣鼓巷", "南锣鼓巷是北京最古老的街区之一，也是最具老北京风情的街巷，这里汇聚了众多特色小店、美食餐厅和创意工坊，是体验北京胡同文化和夜生活的好去处。");

        // 上海景点

        attractions.put("东方明珠广播电视塔", "东方明珠是上海的标志性文化景观之一，塔高约468米，可俯瞰上海城市全景，旋转餐厅、太空舱等设施为游客带来独特的观光体验。");
        attractions.put("外滩", "外滩位于黄浦江畔，是上海的标志性景点之一，这里矗立着52幢风格迥异的古典复兴大楼，素有外滩万国建筑博览群之称，夜晚的外滩灯光璀璨，美不胜收。");
        attractions.put("豫园", "豫园是江南古典园林，始建于明代嘉靖、万历年间，园内有江南三大名石之称的玉玲珑、点春堂等著名景点，展现了江南园林的精致与典雅。");
        attractions.put("上海迪士尼度假区", "上海迪士尼度假区是中国内地首座迪士尼主题乐园，拥有七大主题园区，充满童话色彩的城堡、精彩的游行表演和刺激的游乐设施，为游客带来梦幻般的体验。");
        attractions.put("田子坊", "田子坊是由上海特有的石库门建筑群改建而成的时尚地标性创意产业聚集区，这里汇聚了众多特色小店、艺术工作室和咖啡馆，是感受上海艺术氛围和创意文化的好去处。");

        // 成都景点

        attractions.put("大熊猫繁育研究基地", "这里是全球最大的大熊猫人工繁育基地，游客可以近距离观赏到可爱的大熊猫，了解它们的生活习性和保护情况，感受人与自然的和谐共生。");
        attractions.put("宽窄巷子", "宽窄巷子是成都遗留下来的较成规模的清朝古街道，由宽巷子、窄巷子和井巷子平行排列组成，这里汇聚了众多特色餐厅、茶馆和手工艺品店，是体验成都悠闲生活的好去处。");
        attractions.put("都江堰景区", "都江堰是世界文化遗产，是全世界迄今为止年代最久、唯一留存、仍在一直使用、以无坝引水为特征的宏大水利工程，两千多年来一直发挥着防洪灌溉的作用。");
        attractions.put("青城山", "青城山是中国道教名山之一，素有‘青城天下幽’的美誉，这里山青水秀，空气清新，是徒步登山、感受道教文化的好去处。");
        attractions.put("锦里古街", "锦里古街是成都知名的步行商业街，以明末清初川西民居作外衣，三国文化与成都民俗作内涵，集旅游购物、休闲娱乐为一体，夜晚的锦里古街灯火辉煌，热闹非凡。");
        //西安景点

        attractions.put("秦始皇兵马俑博物馆", "被誉为‘世界第八大奇迹’，数千个真人大小的陶俑、陶马组成庞大的地下军阵，生动展现了秦朝军队的威武雄姿，是研究秦朝历史文化的珍贵实物资料。");
        attractions.put("西安城墙", "中国现存规模最大、保存最完整的古代城垣之一，周长13.74千米。游客可以租一辆自行车，沿着城墙骑行一圈，俯瞰城内外的风景，感受古代城市的防御体系。");
        attractions.put("大雁塔", "现存最早、规模最大的唐代四方楼阁式砖塔，是佛塔这种古印度佛寺的建筑形式随佛教传入中原地区，并融入华夏文化的典型物证。周边的大雁塔北广场有亚洲最大的音乐喷泉，夜晚灯光与喷泉交相辉映，美轮美奂。");
        attractions.put("大唐不夜城", "以盛唐文化为背景，以唐风元素为主线，建有大雁塔北广场、玄奘广场、贞观广场、创领新时代广场四大广场，西安音乐厅、陕西大剧院、西安美术馆、曲江太平洋电影城等四大文化场馆，是感受大唐繁华夜景和体验唐文化的绝佳去处。");
        attractions.put("陕西历史博物馆", "中国第一座大型现代化国家级博物馆，馆藏文物上起远古人类初始阶段使用的简单石器，下至1840年前社会生活中的各类器物，时间跨度长达一百多万年，文物数量多、种类全，品位高、价值广。");

        //厦门景点
        attractions.put("鼓浪屿", "国家5A级旅游景区，岛上保留着许多具有中外建筑风格的建筑物，有‘万国建筑博览’之称。漫步在鼓浪屿的小巷中，欣赏着各种风格的建筑，感受着浓厚的文艺气息，还可以品尝到各种特色小吃。");
        attractions.put("南普陀寺", "始建于唐朝末年，位于五老峰下，面临碧澄海港。寺内建筑精美，香火旺盛，是闽南佛教胜地之一。寺后的五老峰山势险峻，是登山观景的好去处，登上山顶可以俯瞰厦门市区和大海的美景。");
        attractions.put("厦门大学", "被誉为‘中国最美大学’，校园内绿树成荫，建筑风格独特，有芙蓉湖、情人谷等景点。芙蓉隧道内的涂鸦墙是厦大的一大特色，吸引了众多游客前来打卡。");
        attractions.put("曾厝垵", "曾经是一个小渔村，如今已发展成为厦门最文艺的渔村之一。这里汇聚了各种特色小店、美食餐厅和民宿，游客可以在这里品尝到地道的厦门小吃，感受渔村的悠闲生活。");
        attractions.put("环岛路", "环绕厦门岛的海滨公路，沿途风景优美，有椰风寨、胡里山炮台等景点。游客可以租一辆自行车，沿着环岛路骑行，欣赏大海的美景，感受海风的吹拂。");

        //重庆景点
        attractions.put("洪崖洞", "以巴渝传统建筑特色的‘吊脚楼’风貌为主体，依山就势，沿江而建。夜晚的洪崖洞灯火辉煌，建筑倒映在江面上，仿佛现实版的‘千与千寻’场景，是重庆的标志性景点之一。");
        attractions.put("解放碑", "全称‘抗战胜利纪功碑暨人民解放纪念碑’，是中国唯一一座纪念中华民族抗日战争胜利的国家纪念碑。这里是重庆的商业中心，周围商场林立，美食众多，是购物、品尝美食的好去处。");
        attractions.put("武隆喀斯特旅游区", "拥有罕见的喀斯特自然景观，包括天生三桥、仙女山、芙蓉洞等景点。天生三桥是亚洲最大的天生桥群，气势磅礴；仙女山夏季凉爽宜人，是避暑胜地；芙蓉洞是一个大型石灰岩洞穴，洞内钟乳石形态各异，美不胜收。");
        attractions.put("长江索道", "被誉为‘万里长江第一条空中走廊’和‘山城空中公共汽车’，乘坐索道可以跨越长江，欣赏两岸的城市风光，感受重庆独特的山城魅力。");
        attractions.put("磁器口古镇", "始建于宋代，拥有‘一江两溪三山四街’的独特地貌，是嘉陵江边重要的水陆码头。古镇内保存了许多明清时期的建筑，街道两旁有各种特色小店和美食摊位，游客可以在这里品尝到地道的重庆小吃，感受古镇的韵味。");
    }

    public static Document parse(String imagePath) throws IOException {

        ClassPathResource resource = new ClassPathResource(imagePath);
        String filename = resource.getFilename();
        File file = resource.getFile();
        String contentType = Files.probeContentType(file.toPath());
        if (filename == null) {
            return Document.builder().build();
        }
        filename = filename.substring(0, filename.lastIndexOf("."));
        String description = attractions.get(filename);
        String uuid = UUID.randomUUID().toString();
        Map<String,Object> metadata = new HashMap<>();
        //location
        List<Double> location = getLocation(filename, AK);
      //  Map<String,Object> loc = new HashMap<>();
        String locStr = "";
        if (location != null) {
            /*loc.put("lat",location.get(0));
            loc.put("lng",location.get(1));*/
             locStr = "lat:"+location.get(0)+",lng:"+location.get(1);
        }
        metadata.put("scenic_name",  filename);
        metadata.put("location", locStr);
        metadata.put("description", description);
        Document doc = Document.builder()
                .text(filename)
                /*.media(Media.builder()
                        .mimeType(MimeType.valueOf(contentType))
                        .data(resource)
                        .build())*/
                .metadata(metadata)
                .id(uuid)
                .build();
        return doc;

    }

    public static List<Double> getLocation(String address, String ak) {
        try {
            // 编码地址以避免特殊字符问题
            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            String urlString = "http://api.map.baidu.com/geocoding/v3/?address=" + encodedAddress + "&output=json&ak=" + ak;

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // 读取响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // 解析JSON响应
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            if (jsonResponse.get("status").getAsInt() == 0) {
                JsonObject location = jsonResponse.getAsJsonObject("result").getAsJsonObject("location");
                double lng = location.get("lng").getAsDouble();
                double lat = location.get("lat").getAsDouble();
                System.out.println("地址: " + address + ", 经度: " + lng + ", 纬度: " + lat);
                return List.of(lng,lat);

            } else {
                System.out.println("查询失败，错误信息: " + jsonResponse.get("message").getAsString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}










