package com.skycaster.geomapper.util;

import com.skycaster.geomapper.data.StaticData;

/**
 * Created by 廖华凯 on 2017/7/12.
 */

public class NetUtil {

    public static String generatePanoramaUrl(double lat,double lng,int width,int height){
//        参数名	必选	默认值	描述
//        ak	是	无	用户的访问密钥。只支持浏览器端AK和Android/IOS SDK的AK，服务端AK不支持sn校验方式。
//        mcode	否	无	安全码。若为Android/IOS SDK的ak, 该参数必需。
//        width	否	400	图片宽度，范围[10,1024]
//        height	否	300	图片高度，范围[10,512]
//        location	是	无	全景位置点坐标。坐标格式：lng<经度>，lat<纬度>，例如116.313393,40.047783。
//        coordtype	否	bd09ll	全景位置点的坐标类型，目前支持bd09ll（百度坐标），wgs84ll（GPS坐标）和gcj02（google，高德，soso坐标）。
//        poiid	是	无	poi的id，该属性通常通过place api接口获取，poiid与panoid、location一起设置全景的显示场景，优先级为：poiid>panoid>location。其中根据poiid获取的全景视角最佳。
//        panoid	是	无	全景图id，panoid与poiid、location一起设置全景的显示场景，优先级为：poiid>panoid>location。
//        heading	否	0	水平视角，范围[0,360]
//        pitch	否	0	垂直视角，范围[0,90]。
//        fov	否	90	水平方向范围，范围[10,360]，fov=360即可显示整幅全景图

//        状态码	含义
//        0	正常
//        507	coordtype赋值错误，目前只支持bd09ll,wgs84ll
//        509	ak为必选参数，没有加ak时返回错误。其他ak验证码，请 查看
//        508	请求ak验证服务失败
//        302	坐标的格式不对，经纬度应该以逗号分隔
//        303	百度经纬度坐标转换为百度墨卡托坐标错误
//        304	wgs84经纬度坐标转换为百度墨卡托坐标错误
//        402	请求坐标转panoid服务，返回错误，可能是该点没有panoid
//        401	请求坐标转panoid服务失败
//        403	没有panoid 和 location 参数
//        404	根据poi id获取pano id服务请求失败
//        405	根据poi id获取pano id服务返回无效的pano id
//        501	超出了width的范围
//        502	超出了height的范围
//        503	超出了heading 的范围
//        506	超出了fov的范围
//        601	请求街景服务失败
//        0	正常
//        1	服务器内部错误	该服务响应超时或系统内部错误，请留下联系方式
//        10	上传内容超过8M	Post上传数据不能超过8M
//        101	AK参数不存在	请求消息没有携带AK参数
//        102	MCODE参数不存在，mobile类型mcode参数必需	对于Mobile类型的应用请求需要携带mcode参数，该错误码代表服务器没有解析到mcode
//        200	APP不存在，AK有误请检查再重试	根据请求的ak，找不到对应的APP
//        201	APP被用户自己禁用，请在控制台解禁
//        202	APP被管理员删除	恶意APP被管理员删除
//        203	APP类型错误	当前API控制台支持Server(类型1), Mobile(类型2, 新版控制台区分为Mobile_Android(类型21)及Mobile_IPhone（类型22））及Browser（类型3），除此之外其他类型认为是APP类型错误
//                210	APP IP校验失败	在申请SERVER类型应用的时候选择IP校验，需要填写IP白名单，如果当前请求的IP地址不在IP白名单或者不是0.0.0.0/0就认为IP校验失败
//        211	APP SN校验失败	SERVER类型APP有两种校验方式IP校验和SN校验，当用户请求的SN和服务端计算出来的SN不相等的时候提示SN校验失败
//        220	APP Referer校验失败	浏览器类型的APP会校验referer字段是否存且切在referer白名单里面，否则返回该错误码
//        230	APP Mcode码校验失败	服务器能解析到mcode，但和数据库中不一致，请携带正确的mcode
//        240	APP 服务被禁用	用户在API控制台中创建或设置某APP的时候禁用了某项服务
//        250	用户不存在	根据请求的user_id, 数据库中找不到该用户的信息，请携带正确的user_id
//        251	用户被自己删除	该用户处于未激活状态
//        252	用户被管理员删除	恶意用户被加入黑名单
//        260	服务不存在	服务器解析不到用户请求的服务名称
//        261	服务被禁用	该服务已下线
//        301	永久配额超限，限制访问	配额超限，如果想增加配额请联系我们
//        302	天配额超限，限制访问	配额超限，如果想增加配额请联系我们
//        401	当前并发量已经超过约定并发配额，限制访问	并发控制超限，请控制并发量或联系我们
//        402	当前并发量已经超过约定并发配额，并且服务总并发量也已经超过设定的总并发配额，限制访问	并发控制超限，请控制并发量或联系我们
        StringBuilder sb=new StringBuilder();
        sb.append("http://api.map.baidu.com/panorama/v2?mcode=")
                .append(StaticData.BAIDU_SECURITY_CODE)
                .append("&ak=")
                .append(StaticData.BAIDU_AK)
                .append("&width=")
                .append(width)
                .append("&height=")
                .append(height)
                .append("&location=")
                .append(lng)
                .append(",")
                .append(lat)
                .append("&fov=180");
        return sb.toString();
    }
}
