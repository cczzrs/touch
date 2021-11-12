package org.cczzrs.touch.dnas;

import com.alibaba.fastjson.JSONObject;

import org.cczzrs.touch.dnas.IDna.Ret;

public class Psychology {

    /**
     * 根据数据计算得出临时权重值的平滑偏差值
     * @param req
     * @param code_ls
     * @param db
     * @return
     */
    public static double dominant_behavior(Ret req, double code_ls, JSONObject db) {
        return code_ls/(req.code+code_ls)/5*2;
    }

    public static class Behavior {
            
        public static Ret all(Ret req, double code_ls, JSONObject db) {
            return null;
        }
    }

}
