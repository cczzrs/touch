package org.cczzrs.touch.dnas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import org.apache.tomcat.util.security.MD5Encoder;
import org.cczzrs.touch.IRegistry;
import org.cczzrs.touch.IRegistry.CallBack;

/**
 * dna基础类
 */
public class IDna {
    
    /**
     * 云加载DB（主导数据）
     */
    public IDna(JSONObject db) {
        _ID = MD5Encoder.encode(db.toJSONString().getBytes()); // 获取核心的ID，以定位自己的位置
        _CODE = db.getDoubleValue("code"); // 获取当前节点权重值
        _CODE_LS = db.getDoubleValue("code_ls"); // 获取当前节点临时权重值
        // 注册 连接到自己的位置
        _PIDS = db.getJSONArray("pid").toJavaList(String.class);
        IRegistry.init(_ID, buildCallBack(), new HashSet<String>(_PIDS));
        dominant = new Dominant(db.getJSONObject("dominant")); // 初始化显性基因对象
        recessive = new Recessive(db.getJSONObject("recessive")); // 初始化隐性基因对象
    }
    protected List<String> _PIDS = null;
    protected CallBack<IDna> _buildCallBack = null;
    /**
     * 所有上级节点
     */
    public CallBack<IDna> buildCallBack() {
        if(_buildCallBack==null){
            _buildCallBack = new CallBack<IDna>(){
                /**
                 * 当前下级所有节点（传递处理好的数据）
                 */
                List<CallBack<?>> weis = new ArrayList<>();
                @Override
                public List<CallBack<?>> weis() {
                    return weis;
                }
                /**
                 * 接收上级节点的数据传递
                 */
                @Override
                public ret wai(String id, String db) {
                    if(!_PIDS.contains(id)){ // 未知连接节点
                        // Registry.whereIs(_ID, id, db);// 上报到注册中心，
                        if(!pgBoolean(IRegistry.whereIs(_ID, id, db))){
                            return ret.b(_CODE - _CODE_LS);
                        }
                    }
                    ODB.put(id, db);
                    return ret.b(_CODE);
                }
            };
        }
        return _buildCallBack;
    }

    protected boolean pgBoolean(ret r) {
        return pg(r).code > _CODE;
    }
    /**
     * 数据评估中心，决策树，
     * @param r
     * @return
     */
    protected ret pg(ret r) {
        return ret.b(_CODE + _CODE_LS);
    }
    /**
     * 数据ID，数据位置
     */
    public final String _ID;
    /**
     * 当前节点权重值
     */
    public final double _CODE;
    /**
     * 当前节点临时权重值
     */
    public double _CODE_LS;
    /**
     * 全局否定，紧急刹车控制
     */
    public static boolean _GlobalNegation = true;
    /**
     * 原数据（输入）
     */
    public static JSONObject ODB = new JSONObject();
    /**
     * 构建数据（处理）
     */
    public static JSONObject BuildDB = new JSONObject();
    
    /**
     * 显性基因对象
     */
    public Dominant dominant;
    /**
     * 隐性基因对象
     */
    public Recessive recessive;

    /**
     * 显性基因类
     */
    public class Dominant {
        /**
         * 云加载DB，主要提供给behavior等其他函数作为主导数据
         */
        public Dominant(JSONObject db){
            
        }
        
    }
    /**
     * 隐性基因类
     */
    public class Recessive {
        /**
         * 云加载DB，主要提供给behavior等其他函数作为主导数据
         */
        public Recessive(JSONObject db){
            
        }
        
    }

    /**
     * 行为（习性）
     * @param db
     * @return
     */
    public String behavior(String db) {
        nextNode(db);
        return "";
    }
    /**
     * 提交处理好的数据到下级所有节点
     * @param db
     * @return
     */
    public boolean nextNode(String db) {
        return buildCallBack().wei(_ID, db);
    }
    /**
     * 反馈
     * @param db
     * @return
     */
    public String feedback(String db) {
        return "";
    }
    /**
     * 回报率
     * @param db
     * @return
     */
    public String RepayRate(String db) {
        return "";
    }
    /**
     * 记录
     * @param db
     * @return
     */
    public String record(String db) {
        return "";
    }
    /**
     * 函数之间传递对象
     */
    public static class ret {
        public static ret b(Double code){
            return b(code, null, null);
        }
        public static ret b(Double code, String msg){
            return b(code, msg, null);
        }
        public static ret b(Double code, JSONObject db){
            return b(code, null, db);
        }
        public static ret b(Double code, String msg, JSONObject db){
            return new ret(code, msg, db);
        }
        private ret(Double code, String msg, JSONObject db){
            this.code = code;
            this.msg = msg;
            this.db = db;
        }
        /**
         * 权重值（非空）
         */
        public double code;
        /**
         * 权重值解释
         */
        public String msg;
        /**
         * 数据
         */
        public JSONObject db;
        public ret setCode(Double code){
            this.code = code;
            return this;
        }
        public ret setMsg(String msg){
            this.msg = msg;
            return this;
        }
        public ret setJSONObject(JSONObject db){
            this.db = db;
            return this;
        }
    }
}
