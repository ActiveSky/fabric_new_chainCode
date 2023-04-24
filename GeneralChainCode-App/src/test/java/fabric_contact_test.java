import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gvssimux.fabricgateway.FabricGateway;
import com.gvssimux.pojo.GeneralChainCode;
import com.gvssimux.pojo.QueryResult;
import com.gvssimux.pojo.QueryResultList;
import org.hyperledger.fabric.client.Contract;
import org.junit.Test;

import java.util.List;

public class fabric_contact_test {

    /*测试提交数据*/
    @Test
    public void test1() throws Exception {
        FabricGateway fabricGateway = new FabricGateway();
        Contract contract = fabricGateway.getContract();
        String value_1 = "{\n\"id\":\"1\",\n\"idB\":\"1\",\n\"idS\":\"47\",\n\"totalPrice\":\"50000\"\n}";
        String value_2 = "{\n\"id\":\"2\",\n\"idB\":\"2\",\n\"idS\":\"47\",\n\"totalPrice\":\"45000\"\n}";
        String value_3 = "{\n\"id\":\"1\",\n\"num\":\"100\",\n\"price\":\"1\",\n\"orderStatus\":\"0\"\n}";

        byte[] bytes=contract.submitTransaction("createData","test0013",value_3);
        String s = new String(bytes);
        System.out.println("test11");
        System.out.println(s);
    }

    /*测试查询数据*/
    @Test
    public void test2() throws Exception {
        FabricGateway fabricGateway = new FabricGateway();
        Contract contract = fabricGateway.getContract();
        String values = "{\"author\":\"ShanZJ002\",\"email\":\"gvssimux@qq.com\",\"name\":\"GeneralChainCode\",\"url\":\"www.gvssimux.com\"}";
        byte[] bytes = contract.evaluateTransaction("queryData", "test009");
        System.out.println(new String(bytes));

    }


    /*测试修改数据*/
    @Test
    public void test3() throws Exception {
        FabricGateway fabricGateway = new FabricGateway();
        Contract contract = fabricGateway.getContract();
        String values = "{\"author\":\"Gvssimux\",\"email\":\"simux@qq.com\",\"name\":\"GeneralChainCode\",\"url\":\"www.simux.com\"}";
        contract.submitTransaction("updateData","test003",values);
    }


    /*测试删除数据*/
    @Test
    public void test4() throws Exception {
        FabricGateway fabricGateway = new FabricGateway();
        Contract contract = fabricGateway.getContract();
        String values = "{\"author\":\"ShanZJ002\",\"email\":\"gvssimux@qq.com\",\"name\":\"GeneralChainCode\",\"url\":\"www.gvssimux.com\"}";
        contract.submitTransaction("deleteData","test001");
    }


    /*测试富查询数据*/
    /*富查询邮箱为gvssimux@qq.com 的作者*/
    @Test
    public void test5() throws Exception {
        FabricGateway fabricGateway = new FabricGateway();
        Contract contract = fabricGateway.getContract();
        String str = "{\"selector\":{\"email\":\"gvssimux@qq.com\"}, \"use_index\":[]}";// 富查询字符串
        byte[] richQueries = contract.submitTransaction("richQuery", str);
        JSONObject jsonObject = JSONObject.parseObject(new String(richQueries));
        List<QueryResult> resultValueList = JSON.toJavaObject(jsonObject, QueryResultList.class).getResultList();
        for (QueryResult a: resultValueList) {
            System.out.println("for循环打印");
            JSONObject jsonObject2 = JSONObject.parseObject(a.getJson());
            GeneralChainCode data = JSON.toJavaObject(jsonObject2, GeneralChainCode.class);
            System.out.println(data.getUrl());
            System.out.println(data.getEmail());
            System.out.println(data.getAuthor());
        }
    }
}


