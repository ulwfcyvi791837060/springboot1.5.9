package com.yyx.aio.common.file;

import java.io.*;

//如果文件存在，则追加内容；如果文件不存在，则创建文件，追加内容的三种方法
public class AppendContentToFile {
    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        AppendContentToFile a = new AppendContentToFile();
        a.method1("45555");
        a.method2("D:\\1.txt", "222222222222222");
        a.method3("D:\\1.txt", "33333333333");
    }

    public void method1(String conent) {
        FileWriter fw = null;
        try {

            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f=new File("D:\\1.txt");

            LineNumberReader lnr = new LineNumberReader(new FileReader(f));
            lnr.skip(Long.MAX_VALUE);
            int lineNo = lnr.getLineNumber() + 1;
            lnr.close();
            if(lineNo>100000){
                fw = new FileWriter(f, false);
            }else{
                fw = new FileWriter(f, true);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(conent);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void method2(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void method4(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, false)));
            out.write(conent+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void method3(String fileName, String content) {
        try {
// 打开一个随机访问文件流，按读写方式
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
// 文件长度，字节数
            long fileLength = randomFile.length();
// 将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.writeBytes(content+"\r\n");
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
