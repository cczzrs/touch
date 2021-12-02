package org.cczzrs.touch.dnas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.cczzrs.touch.IRegistry;
import org.cczzrs.touch.IRegistry.Pipeline;

/**
 * dna基础类
 */
public class IDna {
    /**全局待否定，紧急刹车控制*/
    public static boolean _GlobalNegation = true;
    
    /**
     * 云加载DB（主导数据）
     */
    public IDna(JSONObject db) {
        // _ID = MD5Encoder.encode(db.toJSONString().getBytes()); // 获取核心的ID，以定位自己的位置
        _ID = db.getString("id");
        _CODE = db.getDoubleValue("code"); // 获取当前节点权重值
        _CODE_LS = db.getDoubleValue("code_ls"); // 获取当前节点临时权重值
        _PIDS = db.getJSONArray("pids").toJavaList(String.class);// 需连接到的位置（所有上级节点）
        dominant = new Dominant(db.getJSONObject("dominant")); // 初始化显性基因对象
        recessive = new Recessive(db.getJSONObject("recessive")); // 初始化隐性基因对象
        IRegistry.init(_ID, buildPipeline(), new HashSet<String>(_PIDS));// 注册 连接到自己的位置
    }
    protected List<String> _PIDS = null;
    protected Pipeline<IDna> _buildPipeline = null;
    /**
     * 所有上级节点
     */
    public Pipeline<IDna> buildPipeline() {
        if(_buildPipeline==null){
            _buildPipeline = new Pipeline<IDna>(){
                /**当前下级所有节点（传递处理好的数据）*/
                List<Pipeline<?>> weis = new ArrayList<>();
                /**当前下级所有节点*/
                @Override
                public List<Pipeline<?>> weis() {
                    return weis;
                }
                /**数据传递大小*/
                Queue<Integer> q = null;
                @Override
                public Queue<Integer> q() {
                    if(q==null){
                        q = Pipeline.super.q();
                    }
                    return q;
                }
                
                /**
                 * 接收上级节点的数据传递
                 */
                @Override
                public Ret wai(String id, JSONObject db) {
                    if(!_PIDS.contains(id)){ // 未知连接节点
                        // Registry.whereIs(_ID, id, db);// 上报到注册中心，
                        if(!pgBoolean(IRegistry.whereIs(_ID, id, db))){
                            return Ret.b(_ID, _CODE - _CODE_LS);
                        }
                    }
                    ODB_puts(id, db);
                    return Ret.b(_ID, _CODE + _CODE_LS);
                }
                @Override
                public String ID() {
                    return _ID;
                }
                @Override
                public JSONObject DB() {
                    if(!BuildDB.containsKey("show")){
                        BuildDB.put("show", new JSONObject().fluentPut("id", _ID).fluentPut("g", 0));
                    }
                    return BuildDB.getJSONObject("show");
                }
            };
        }
        return _buildPipeline;
    }

    /**
     * 保存接收的数据到 原数据 的集合中
     * @param key
     * @param db
     */
    protected void ODB_puts(String key, JSONObject... db) {
        if(ODB.containsKey(key)) {
            ODB.getJSONArray(key).addAll(Arrays.asList(db));
        } else {
            // ODB.put(key, Arrays.asList(db));
            ODB.put(key, JSONArray.parseArray(JSONArray.toJSONString(db)));
        }
    }
    protected void ODB_puts(String key, int ppd, JSONObject... db) {
        String ikey = key.substring(0, ppd); // 以 ikey 开始的key的值
        if(ODB.containsKey(ikey)) {
            ODB.getJSONArray(ikey).addAll(Arrays.asList(db));
        } else {
            // ODB.put(ikey, Arrays.asList(db));
            ODB.put(ikey, db);
        }
    }
    protected JSONArray ODB_gets(String key) {
        return ODB.getJSONArray(key);
    }
    protected JSONArray ODB_gets(String key, int ppd) {
        String ikey = key.substring(0, ppd); // 以 ikey 开始的key的值
        return ODB.getJSONArray(ikey);
    }

    /**
     * 评估决策 是或否
     * @param r
     * @return
     */
    protected boolean pgBoolean(Ret r) {
        return pg(r).code > _CODE;
    }
    /**
     * 数据评估中心，决策树，
     * @param r
     * @return
     */
    protected Ret pg(Ret r) {
        return Ret.b(_ID, _CODE + _CODE_LS);
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
     * 原数据（输入）
     */
    private JSONObject ODB = new JSONObject();
    /**
     * 构建数据（处理）
     */
    protected JSONArray outdb = new JSONArray();
    protected JSONObject BuildDB = newJO("outdb", outdb);
    
    /**
     * 显性基因对象
     */
    protected final Dominant dominant;
    /**
     * 隐性基因对象
     */
    protected final Recessive recessive;

    /**
     * 显性基因类
     */
    public class Dominant {
        /**
         * 云加载DB，主要提供给behavior等其他函数作为主导数据
         */
        public Dominant(JSONObject db){
            this._ID = db.getString("id");
            this._CODE = db.getDoubleValue("code");
            this._CODE_LS = db.getDoubleValue("codels");
            this.ODB = db.getJSONObject("odb");
            req = Ret.b(this._ID, this._CODE, this.getClass().getName(), this.ODB);
        }
        
        /**ID*/
        private final String _ID;
        /**权重值*/
        private final double _CODE;
        /**临时权重值*/
        public double _CODE_LS;
        /**原数据（固定经验数据）*/
        private final JSONObject ODB;
        /**该对象数据传递对象-不变参数*/
        public final Ret req;

        /**
         * 行为（习性）-分析临时权重值变动值
         * @param db
         * @return
         */
        public Ret behavior(JSONObject db) {
            // 根据数据计算得出临时权重值的平滑偏差值
            this._CODE_LS += Psychology.dominant_behavior(req, this._CODE_LS, db);
            return Ret.b(this._ID, this._CODE + this._CODE_LS);
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
            this._ID = db.getString("id");
            this._CODE = db.getDoubleValue("code");
            this._CODE_LS = db.getDoubleValue("codels");
            this.ODB = db.getJSONObject("odb");
            ret = Ret.b(this._ID, this._CODE, this.getClass().getName(), this.ODB);
        }
        
        /**ID*/
        private final String _ID;
        /**权重值*/
        private final double _CODE;
        /**临时权重值*/
        public double _CODE_LS;
        /**原数据（固定经验数据）*/
        private final JSONObject ODB;
        /**该对象数据传递对象-不变参数*/
        public final Ret ret;
    }

    // 线程逻辑.
    // 从历史数据中寻找匹配的当前（情况）数据（参数中包涵匹配度，默认为最匹配的数据）的历史数据 behavior
    // 没有匹配到即为新事物，记录数据为待定（待定数据会在异步反馈中更新可输出数据 feedback <= 异步回报率以提高数据匹配度 repayRate） record
    // 输出数据传递到所有绑定的下级节点 submitNextNodes();
    // 如果父母级有高度匹配数据，即再次传递输出数据到下级节点 => 异步进行父母级匹配数据 <= 把数据提到历史数据中

    /**
     * 行为（习性）
     * 从历史数据中寻找匹配的当前（情况）数据（参数中包涵匹配度，默认为最匹配的数据）的历史数据 behavior
     * @param db
     * @return
     */
    public JSONObject behavior(JSONObject db) {
        JSONObject ndb = newJO();
        String tz = db.getString("tz");
        JSONArray odbs = ODB_gets(tz, db.getIntValue("ppd"));
        if(odbs == null || odbs.size() < 1) {
            // 没有匹配到即为新事物，记录数据为待定（待定数据会在异步反馈中更新可输出数据 feedback <= 异步回报率以提高数据匹配度 repayRate） record
            ODB_puts(tz, db.getIntValue("ppd"), db.fluentPut("req", 1).fluentPut("state", 0).fluentPut("r", 0).fluentPut("j", 0));
            return newJO();
        } else {
            JSONObject odb;
            JSONObject tdb;
            for (int i = 0; i < odbs.size(); i++) {
                odb = odbs.getJSONObject(i);
                if(odb.getDouble("state") == 0) {// 数据为待定  // 正负为方向、大小为频率、0为待定
                    tdb = ODB.getJSONObject(tz);
                    tdb.put("req", tdb.getIntValue("req")+1);
                    continue;
                } else{
                    // 构建传递数据，计算特征tz，匹配度ppd
                    ndb.fluentPut("tz", tz).fluentPut("ppd", 111);
                    outdb.add(ndb);
                }
            }
        }
        return ndb;
    }
    public String behavior1(JSONObject db) {
        // 根据数据计算得出临时权重值的平滑偏差值
        // TODO
        // this._CODE_LS += Psychology.Behavior.all(req, this._CODE_LS, db);
        // return Ret.b(this._ID, this._CODE + this._CODE_LS);
        dominant.behavior(db);

        JSONObject outdb = new JSONObject();
        outdb.put("value", 1);
        BuildDB.put("outdb", outdb);
        submitNextNodes();



        return "";
    }
    /**
     * 提交处理好的数据到下级所有节点
     * @param db
     * @return
     */
    public boolean submitNextNodes() {
        return buildPipeline().wei(BuildDB.getJSONObject("outdb"));
    }
    /**
     * 反馈（异步） TODO
     * @param db
     * @return
     */
    public String feedback(Ret r) {
        
        repayRate(r.db);
        record(r.db);
        return "";
    }
    /**
     * 回报率 TODO
     * @param db
     * @return
     */
    public String repayRate(JSONObject db) {
        return "";
    }
    /**
     * 记录 TODO
     * @param db
     * @return
     */
    public String record(JSONObject db) {
        return "";
    }
    /**
     * 函数之间传递对象
     */
    public static class Ret {
        public static Ret b(String id, Double code){
            return b(id, code, null, null);
        }
        public static Ret b(String id, Double code, String msg){
            return b(id, code, msg, null);
        }
        public static Ret b(String id, Double code, JSONObject db){
            return b(id, code, null, db);
        }
        public static Ret b(String id, Double code, String msg, JSONObject db){
            return new Ret(id, code, msg, db);
        }
        private Ret(String id, Double code, String msg, JSONObject db){
            this.id = id;
            this.code = code;
            this.msg = msg;
            this.db = db;
        }
        /**ID*/
        public String id;
        /**权重值（非空）*/
        public double code;
        /**权重值解释*/
        public String msg;
        /**数据*/
        public JSONObject db;
        public Ret setCode(Double code){
            this.code = code;
            return this;
        }
        public Ret setMsg(String msg){
            this.msg = msg;
            return this;
        }
        public Ret setJSONObject(JSONObject db){
            this.db = db;
            return this;
        }
    }

    public JSONObject newJO(){
        return new JSONObject(new LinkedHashMap<>());
    }
    public JSONObject newJO(String key, Object value){
        return new JSONObject(new LinkedHashMap<>()).fluentPut(key, value);
    }
    
}
