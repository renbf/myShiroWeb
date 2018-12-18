package com.yl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCode {
	
	private static final Logger logger = LoggerFactory.getLogger(CreateCode.class);
    // 定义一个开关flag=false，不覆盖
    private final static boolean flag = false;
    // 1.那些domain需要生成代码
//    private static String[] tableNamds = { "my_test" };
    // 2.定义固定的目录路径:都是使用相对路径,规范：路径前面都不加/,路径的后面都加/
    private final static String SRC = "src/main/";
    private final static String PACKAGE = "java/com/yl/";
    private final static String WEBAPP = "webapp/";
    // 3.有那些模板需要生成
    private static String[] templates = { "ClassNameServiceImpl.vm", "IClassNameService.vm", "ClassNameController.vm","ClassName.vm","ClassNameDao.vm","ClassNameMapper.vm","className.jsp"};
    // 4.模板文件对应的生成文件路径
    private static String[] files = { SRC + PACKAGE + "service/impl/",SRC + PACKAGE + "service/", SRC + PACKAGE + "controller/", SRC + PACKAGE + "modle/", SRC + PACKAGE + "dao/", SRC + PACKAGE + "dao/mapper/", SRC + WEBAPP + "WEB-INF/pages/"};
//    // 3.有那些模板需要生成
//    private static String[] templates = { "ClassNameServiceImpl.vm", "IClassNameService.vm", "ClassNameController.vm","ClassName.vm","ClassNameDao.vm","ClassNameMapper.vm"};
//    // 4.模板文件对应的生成文件路径
//    private static String[] files = { SRC + PACKAGE + "service/impl/",SRC + PACKAGE + "service/", SRC + PACKAGE + "controller/", SRC + PACKAGE + "modle/", SRC + PACKAGE + "dao/", SRC + PACKAGE + "dao/mapper/"};

    private static String[] columns = {"int","varchar","datetime"};
    private static String[] columnsJdbc = {"INTEGER","VARCHAR","TIMESTAMP"};
    private static String[] columnsJava = {"Integer","String","Date"};
    public static void main(String[] args) throws Exception {
        if (templates.length != files.length) {
            throw new RuntimeException("templates.length != files.length");
        }
        //读取输入模板文件
        Map<String,List<BeanMapper>> tablesMap = readFileByLines(SRC+WEBAPP+"template/table.txt");
        //2018/12/4 17:39
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm");

        // 实例化Velocity上下文
        VelocityContext context = new VelocityContext();
        //表名首字母大写的类名
        String[] className = new String[tablesMap.size()];
        //表名首字母小写的类名
        String[] classNameField = new String[tablesMap.size()];
        String[] tableNames = new String[tablesMap.size()];
        Set<String> tableNameSet = tablesMap.keySet();
        tableNames = tableNameSet.toArray(tableNames);
        for(int i =0;i < tableNames.length;i++){
            className[i] = columnNameToJavaName(tableNames[i],true);
            classNameField[i] = columnNameToJavaName(tableNames[i],false);
        }

        // 5.外循环：className
        for (int i = 0; i < className.length; i++) {
            context.put("now",sdf.format(new Date()));
            context.put("beanMapperList", tablesMap.get(tableNames[i]));
            context.put("className", className[i]);
            context.put("classNameField", classNameField[i]);
            context.put("table_name", tableNames[i]);
            context.put("entity", "method");
            // 定义domain的首字母小写
            // 6.处理domain首字母小写
            String lowerCaseEntity = className[i].substring(0, 1).toLowerCase() + className[i].substring(1);
//            context.put("lowerCaseEntity", lowerCaseEntity);
            // 7.内循环：templates和files
            for (int j = 0; j < templates.length; j++) {
                // 8.实例化文件存放的路径
                File file = null;
                // 9.处理特殊的文件名称
                if(templates[j].endsWith(".vm")){
                    String templateName = templates[j].substring(0,templates[j].length()-3);
                    if(templateName.indexOf("ClassName") >= 0){
                        templateName = templateName.replace("ClassName",className[i]);
                        if(templateName.indexOf("Mapper") >= 0){
                            templateName =  templateName.concat(".xml");
                        }else{
                            templateName =  templateName.concat(".java");
                        }
                        file = new File(files[j] + templateName);
                    }
                }else{
                    if(templates[j].endsWith(".jsp")){
                        String templateName = templates[j];
                        if(templateName.indexOf("className") >= 0){
                            templateName = templateName.replace("className",classNameField[i]);
                            file = new File(files[j] + templateName);
                        }
                    }
                }

                // 12.开关：默认情况下已经存在的文件不需要生成代码 true:覆盖所有代码
                // 如果flag==false并且当前生存文件是存在的
                if (!flag && file.exists()) {
                    // return;
                    // break;
                    continue;// 本次跳过，继续下一次循环，
                }

                // 10.判断父目录是否存在
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                logger.info(file.getAbsolutePath());
                // 获取模版
                Template template = Velocity.getTemplate(SRC+WEBAPP+"template/" + templates[j], "UTF-8");
                // 流
                FileWriter writer = new FileWriter(file);
                // 合并
                template.merge(context, writer);
                // 11.必须关闭流，写入内容
                writer.close();
            }
        }

    }

    public static Map<String,List<BeanMapper>> readFileByLines(String fileName){
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
        	Map<String,List<BeanMapper>> tablesMap = new HashMap<String,List<BeanMapper>>();
            List<BeanMapper> list = null;
            //正在读取一张表
            boolean readingOneTable = false;
          //正在读取的表名
            String tableName = null;
            logger.info("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            //一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null){
                tempString = tempString.toLowerCase().trim();
                int index = tempString.indexOf("create table");
                //读表开始
                if(readingOneTable == false && index >= 0) {
                	readingOneTable = true;
                	tableName = tempString.substring(index+12);
                	if(tableName.endsWith("(")) {
                		tableName = tableName.substring(0,tableName.length()-1).trim();
                	}
                	list = new ArrayList<BeanMapper>();
                }else {
                	for(int j = 0;j<columns.length;j++){
                        if(tempString.indexOf(columns[j]) > 0){
                            BeanMapper beanMapper = new BeanMapper();
                            String[] columnRead = tempString.split(" ");
                            if(columnRead[1].indexOf(columns[j]) >= 0){
                                String columnName = columnRead[0].toLowerCase();
                                String javaName = columnNameToJavaName(columnName,false);

                                beanMapper.setColumnName(columnName);
                                beanMapper.setJavaName(javaName);
                                beanMapper.setJavaType(columnsJava[j]);
                                beanMapper.setJdbcType(columnsJdbc[j]);
                            }
                            if(tempString.toLowerCase().indexOf("comment") > 0){
                                int idx = tempString.indexOf("comment");
                                beanMapper.setJavabz(tempString.substring(idx+9,tempString.length()-2));
                            }
                            list.add(beanMapper);
                        }
                    }
                	//读表结束
                	if(tempString.indexOf(";") >= 0) {
                		tablesMap.put(tableName, list);
                		readingOneTable = false;
                	}
                }
                
                //显示行号
                logger.info("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
            if(!tablesMap.isEmpty()){
                return tablesMap;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return null;
    }

    /**
     * 分隔符拼接
     * @param columnName
     * @param isFirstNameUpper
     * @return
     */
    public static String columnNameToJavaName(String columnName,boolean isFirstNameUpper){
        String[] javaNames = columnName.split("_");
        String javaName = javaNames[0];
        if(isFirstNameUpper){
            javaName = firstNameUpperCase(javaName);
        }
        for(int i=1;i<javaNames.length;i++){
            javaName = javaName.concat(firstNameUpperCase(javaNames[i]));
        }
        return javaName;
    }

    /**
     * 首字母大写
     * @param str
     * @return
     */
    public static String firstNameUpperCase(String str){
//        return str.substring(0,1).toUpperCase().concat(str.substring(1));
    	char[] ch = str.toCharArray();  
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }
}