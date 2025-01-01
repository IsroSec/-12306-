package com.jiawa.train.generator.gen;

import com.jiawa.train.generator.util.DbUtil;
import com.jiawa.train.generator.util.Field;
import com.jiawa.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * ClassName: ServerGenerator
 * Package: com.jiawa.train.generator.gen
 * Description:
 *
 * @Author GalSec
 * @Create 2024/12/30 21:13
 * @Version 1.0
 */
public class ServerGenerator {
    static String servicePath="[module]/src/main/java/com/jiawa/train/[module]/";
    static String pomPath="generator\\pom.xml";
    static String module="";
    static {
        new File(servicePath).mkdirs();
    }
    public static void main(String[] args) throws Exception {
        String generatorPath = getGeneratorPath();

        module=generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module: " + module);
        servicePath=servicePath.replace("[module]", module);
        // new File(servicePath).mkdirs();
        System.out.println("servicePath: " + servicePath);

        Document document = new SAXReader().read("generator/" + generatorPath);
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText() + "/" + domainObjectName.getText());


        // 为DbUtil设置数据源
        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("url: " + connectionURL.getText());
        System.out.println("user: " + userId.getText());
        System.out.println("password: " + password.getText());
        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();


        // 示例：表名 jiawa_test
        // Domain = JiawaTest
        String Domain = domainObjectName.getText();
        // domain = jiawaTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = jiawa-test url
        String do_main = tableName.getText().replaceAll("_", "-");

        // 表中文名
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        //引入类的包
        Set<String> typeSet = getJavaTypes(fieldList);


        HashMap<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        param.put("do_main", do_main);
        param.put("tableNameCn", tableNameCn);
        param.put("fieldList", fieldList);
        param.put("typeSet", typeSet);
        param.put("module", module);
        System.out.println("map = " + param);

//        gen(Domain, param,"service","service");
//        gen(Domain, param,"controller","controller");
//        gen(Domain, param,"req","saveReq");
        gen(Domain, param, "req", "queryReq");
        gen(Domain, param, "resp", "queryResp");
//        FreemarkerUtil.initConfig("test.ftl");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("domain","Test");
//        FreemarkerUtil.generator("test.java", param);

    }

    private static Set<String> getJavaTypes(List<Field> fieldList) {
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            set.add(field.getJavaType());
        }
        return set;
    }

    private static void gen(String Domain, HashMap<String, Object> param,String packageName,String target) throws IOException, TemplateException {
        String toPath = servicePath  + packageName+"/";
        new File(toPath).mkdirs();
        FreemarkerUtil.initConfig(target+".ftl");
        String Target = target.substring(0, 1).toUpperCase()+ target.substring(1);
        String fileName=toPath + Domain + Target+".java";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        HashMap<String, String> map = new HashMap<>();
        map.put("pom","http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(pomPath);
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }
}
