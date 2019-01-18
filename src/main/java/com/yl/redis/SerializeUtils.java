package com.yl.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializeUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(SerializeUtils.class);
	public static final String UTF_8 = "utf-8";
    /** 
     * 序列化 
     *  
     * @param object 
     * @return 
     */  
    public static byte[] serialize(Object object) 
    {  
        ObjectOutputStream objectOutputStream = null;  
        ByteArrayOutputStream byteArrayOutputStream = null;  
        try
        {  
            // 序列化  
            byteArrayOutputStream = new ByteArrayOutputStream();  
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);  
            objectOutputStream.writeObject(object);  
            byte[] bytes = byteArrayOutputStream.toByteArray();  
            return bytes;  
        }
        catch (Exception e)
        {  
        	logger.error("对象序列化失败！！！");
        }  
        return null;  
    }  
  
    /** 
     * 反序列化 
     *  
     * @param bytes 
     * @return 
     */  
    public static Object unserialize(byte[] bytes) 
    {  
        ByteArrayInputStream byteArrayInputStream = null;  
        try 
        {  
            // 反序列化  
            byteArrayInputStream = new ByteArrayInputStream(bytes);  
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);  
            return objectInputStream.readObject();  
        } 
        catch (Exception e)
        {  
        	logger.error("对象反序列化失败！！！");
        }  
        return null;  
    } 
    
    /** 
     * 序列化 
     *  
     * @param object 
     * @return 
     */  
    public static byte[] serializeStr(String str,String charset) 
    {  
        try
        {  
            byte[] bytes = str.getBytes(charset);  
            return bytes;  
        }
        catch (Exception e)
        {  
        	logger.error("字符串序列化失败！！！");
        }  
        return null;  
    }  
  
    /** 
     * 反序列化 
     *  
     * @param bytes 
     * @return 
     */  
    public static String unserializeStr(byte[] bytes,String charset) 
    {  
        try 
        {  
        	String str = new String(bytes,charset);
            return str;  
        } 
        catch (Exception e)
        {  
        	logger.error("字符串反序列化失败！！！");
        }  
        return null;  
    } 
}
