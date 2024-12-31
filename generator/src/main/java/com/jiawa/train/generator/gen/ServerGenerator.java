package com.jiawa.train.generator.gen;

import com.jiawa.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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


        // 示例：表名 jiawa_test
        // Domain = JiawaTest
        String Domain = domainObjectName.getText();
        // domain = jiawaTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = jiawa-test 生成getmapping路径的
        String do_main = tableName.getText().replaceAll("_", "-");

        HashMap<String, Object> param = new HashMap<>();
        param.put("Domain", Domain);
        param.put("domain", domain);
        System.out.println("map = " + param);

        gen(Domain, param,"service");
        gen(Domain, param,"controller");
//        FreemarkerUtil.initConfig("test.ftl");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("domain","Test");
//        FreemarkerUtil.generator("test.java", param);

    }

    private static void gen(String Domain, HashMap<String, Object> param,String target) throws IOException, TemplateException {
        String toPath = servicePath  + target+"/";
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
