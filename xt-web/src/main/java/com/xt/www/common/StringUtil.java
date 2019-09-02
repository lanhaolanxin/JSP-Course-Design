package com.xt.www.common;

public class StringUtil {
    //判断字符串是否为空
    public static boolean isBlank(String code){
        if (code==null||code.equals("")){
            return true;
        }
        else
            return false;
    }
    //字符串重新组合函数，根据字符","为界，删除id
    public static String deleteId(String str,Long id ){
        String arr[] = str.split(",");
        String delete = id.toString();
        String s = "";
        if (arr!=null &&arr.length>0){
            for (int i = 0;i<arr.length;i++){
                if (arr[i].equals(delete)){
                    System.out.println(arr[i].equals(delete));
                    System.out.println(arr[i]);
                    continue;
                }
                else {
                    if (i==arr.length-1){
                        s = s+arr[i];
                    }
                    else{
                        s = s+arr[i]+",";
                    }
                }
            }
            if (s.endsWith(",")){
                s = s.substring(0,s.length()-1);
            }
        }
        return s;
    }

    public static void main(String[] args) {
        String test = "1,2,3,4,5";
        String str = StringUtil.deleteId(test, (long)4);
        System.out.println(str);
    }
}
