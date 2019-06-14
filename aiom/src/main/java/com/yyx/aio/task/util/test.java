package com.yyx.aio.task.util;

/**
 * @author: zhk
 * @Date :          2019/6/10 11:37
 */
public class test {

    public static void main(String[] args) throws Exception {
        /*String url = "url";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String desKey = "";
        String param = "{\n" +
                "    \"columnNames\": [\n" +
                "        \"location_id\",\n" +
                "        \"store_id\",\n" +
                "        \"store_name\",\n" +
                "        \"b_date\",\n" +
                "        \"s_receivable\",\n" +
                "        \"s_real_income\",\n" +
                "        \"s_bill_num\",\n" +
                "        \"s_discount_total\",\n" +
                "        \"s_chargeback\",\n" +
                "        \"s_chargeback_num\",\n" +
                "        \"s_time\",\n" +
                "        \"s_refresh_time\"\n" +
                "    ],\n" +
                "    \"keyCol\": \"store_id,b_date\",\n" +
                "    \"records\": [\n" +
                "        [\n" +
                "            \"000\",\n" +
                "            \"000\",\n" +
                "            \"测试\",\n" +
                "            \"2019-05-20 00:00:00\",\n" +
                "            \"0.0\",\n" +
                "            \"0.0\",\n" +
                "            \"2.0\",\n" +
                "            \"0.0\",\n" +
                "            \"0.0\",\n" +
                "            \"0.93\",\n" +
                "            \"1.0\",\n" +
                "            \"2019-05-20 16:37:46\",\n" +
                "            \"2019-05-20 16:37:46\"\n" +
                "        ]\n" +
                "    ],\n" +
                "    \"tableName\": \"Summary\"\n" +
                "}";
        String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIsGBo7H2RwlwS0p01THCCA8vX6keZ143G+pP1MMtDve9lPPgRt2IAUAmGc/79a9O69C1u5j+ebdK9a5BfjXwQcyEgV2nRlJjr83O0zwoTp6Mc4WuT5ACNGrHUdijxBW9O+pZRmql5nZES8HrkKb0EtsF6PRguqmFsxg1t3eeqSQIDAQAB";
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", Base64Utils.encode(DESUtil.encrypt(param,desKey)));
        map.put("corporationCode",RSAUtil.encrypt(RSAUtil.loadPublicKey(publicKeyStr),"000124".getBytes()));
        HttpEntity<String> httpEntity = new HttpEntity<>(JSON.toJSONString(map), headers);
        String result = restTemplate.postForObject(url, httpEntity, String.class);
        System.err.print("结果=>" + result);*/


        String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIsGBo7H2RwlwS0p01THCCA8vX6keZ143G+pP1MMtDve9lPPgRt2IAUAmGc/79a9O69C1u5j+ebdK9a5BfjXwQcyEgV2nRlJjr83O0zwoTp6Mc4WuT5ACNGrHUdijxBW9O+pZRmql5nZES8HrkKb0EtsF6PRguqmFsxg1t3eeqSQIDAQAB";
        String encrypt = RSAUtil.encrypt(RSAUtil.loadPublicKey(publicKeyStr), "000062".getBytes());
        System.err.print("结果=>" +encrypt);
        System.err.print("结果=>" +Base64Utils.encode(encrypt.getBytes()));

    }
}
