package springioc.ioc;

import springioc.stereotype.Autowired;
import springioc.stereotype.Component;


import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class SpringIOC {
    private List<String> beanNames;

    private List<String> filePaths;

    private String basePath;

    private String basePackage;

    private Map<String, Object> beans = new HashMap<>();

    /**
     * 扫描指定的文件系统路径（basePath），并收集所有文件信息，存到了 filePaths
     * 若不存在则抛出异常
     */
    private void scan() throws FileNotFoundException {
        File file = new File(basePath);
        filePaths = new ArrayList<>();//存储文件信息
        if (file.exists()) {
            scanRecursively(file); // 从基础路径开始递归扫描
        } else {
            throw new FileNotFoundException("Base path does not exist: " + basePath);
        }
    }

    private void scanRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();// 如果是目录，则获取其下的所有文件和目录
            if (files != null) {
                for (File child : files) {
                    scanRecursively(child);// 递归调用，继续检查子目录或文件
                }
            }
        } else {
            filePaths.add(file.getPath()); // 如果是文件，则添加其路径到列表中
        }
    }

    /**
     * 将所有的后缀名为.java的文件的全限定名放到 beanNames
     * 为对对象有调用权的文件转移给容器做准备
     */
    public void initBeanNames() {
        for (String filepath : filePaths) {
            String replace = filepath.replace(basePath, "");//提取文件的名称（去除基础路径    包名+a.md形式）
            if (replace.endsWith(".java")) {
                replace = replace.substring(0, replace.length() - 5);
            }//检查处理后的路径字符串是否以.java 结尾，如果是，则截取掉这个后缀，因为类名中不包含文件扩展名。

            char[] chars = replace.toCharArray();//将剩余部分转为char类型，存储到数组chars中
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == '\\') {//用"."替换"\"
                    chars[i] = '.';
                }
            }
            beanNames.add(basePackage + "." + new String(chars));//构造全限类定名，存入beansName
        }

    }

    /**
     * 依赖注入，创建Bean实例
     * 遍历每个字段的所有注解，检查是否有@Autowired注解，有的实现自动注入依赖
     */
    public void initBeans() {
        //遍历全限类定名
        for (String beanName : beanNames) {
            try {
                Class<?> aClass = Class.forName(beanName);
                Annotation[] declaredAnnotations = aClass.getDeclaredAnnotations();
                for (Annotation declaredAnnotation : declaredAnnotations) {
                    if (declaredAnnotation instanceof Component) {
                        Object o = aClass.newInstance();
                        beans.put(aClass.getName(), o);
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : declaredFields) {

                Annotation[] declaredAnnotations = field.getDeclaredAnnotations();

                for (Annotation annotation : declaredAnnotations) {
                    if (annotation instanceof Autowired) {
                        //field 需要由我们来赋值
                        // 我们所持有的所有对象 在beans中
                        // 根据当前域中的类型
                        // 的名字
                        String name = field.getType().getName();
                        // 从beans 中获得对应的对象
                        Object o = beans.get(name);
                        field.setAccessible(true);
                        try {
                            field.set(entry.getValue(), o);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }


        }

    }

    /**
     * 从beans映射中获取并返回与beanName对应的Bean实例
     */
    public Object getInstance(String beanName) {
        return beans.get(beanName);
    }

    /**
     * 初始化容器的基础路径和基础包名
     */
    private void initPath() {
        basePath="E:\\qcby\\ioc1\\src\\main\\java\\springioc\\";
        basePackage="springioc";
    }

    /**
     * 构造函数
     */
    public SpringIOC() {
        initPath();
        try {
            scan();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        beanNames = new ArrayList<>();
        initBeanNames();
    }


}