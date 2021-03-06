package org.cczzrs.touch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.cczzrs.touch.dnas.IDna.Ret;

/**
 * 注册中心
 */
public class IRegistry {

    static {// 静态初始化操作
        _ID = "";
    }
    /**
     * 数据ID
     */
    public static final String _ID;
    /**
     * 待绑定对象
     */
    // public static List<String> stay = new ArrayList<>();
    public static Map<String, Set<String>> stay = new HashMap<>();
    /**
     * 已绑定对象
     */
    public static Map<String, Pipeline<?>> bangd = new HashMap<>();
    /**
     * 绑定关系对象
     */
    public static Map<String, Set<String>> nexus = new HashMap<>();
    
    /**
     * 初始化节点，注册到指定（多个）上级节点的CallBack
     * @param <T>
     * @param ID
     * @param cb
     * @param PID
     */
    public static <T> void init(String ID, Pipeline<T> cb, Set<String> PID) {
        // 验证节点数据
        if(PID == null){
            PID = new HashSet<>();
        }
        // 录入对象
        bangd.put(ID, cb);

        // 录入上级关系（ID-PID）
        if(nexus.containsKey(ID)){
            nexus.get(ID).addAll(PID);
        } else {
            nexus.put(ID, PID);
        }
        for (String ipid : PID) {
            if(bangd.containsKey(ipid)){
                // 绑定节点
                bangd.get(ipid).addWei(cb);
            } else {
                // 把未找到上级节点的关系添加到 待绑定对象
                if(!stay.containsKey(ID)){
                    stay.put(ID, new HashSet<>());
                }
                stay.get(ID).add(ipid);
            }
        }

    }

    /**
     * 异常链接的节点及数据
     */
    public static JSONObject whereIs = new JSONObject();
    /**
     * 处理异常链接的节点及数据
     * @param _ID
     * @param id
     * @param db
     */
    public static Ret whereIs(String id, String pid, JSONObject db) {
        Ret normal = Ret.b(_ID, Double.MIN_NORMAL);
        JSONObject pdb = new JSONObject();
        pdb.put("id", id);
        pdb.put("pid", pid);
        pdb.put("time", System.currentTimeMillis());
        pdb.put("db", db);
        pdb.put("ret", normal);
        if(whereIs.containsKey(id)){
            whereIs.getJSONArray(id).add(pdb);
        } else {
            JSONArray pdbs = new JSONArray();
            pdbs.add(pdb);
            whereIs.put(id, pdbs);
        }
        return normal;
    }

    @FunctionalInterface
    public interface Pipeline<T> {
        
        default Queue<Integer> q(){
            return new LinkedList<>(){
                @Override
                public Integer poll() {
                    Integer sp = super.poll();
                    return sp==null?0:sp;
                }
            };
        }
        // Queue<Integer> q = new LinkedList<>();
        // queue.offer(1); //队尾加入
        // int num = queue.poll; //队首弹出

        /**
         * 上级节点的数据传递
         * @param db
         * @return
         */
        Ret wai(String id, JSONObject db);
        /**
         * 把已处理的数据传递到下级所有节点中
         * @param db
         * @return
         */
        default boolean wei(JSONObject db) {
            q().offer(db.size());
            // 异步，并发。发送
            // TODO 待线程池支持
            weis().forEach(wei -> wei.wai(ID(), db)); 
            return true;
        }
        /**
         * 添加下级节点
         * @param db
         * @return
         */
        default boolean addWei(Pipeline<?> cb) {
            Objects.requireNonNull(cb);
            return weis().add(cb);
            //    return (String id, String db) -> { wai(id, db); after.wai(id, db); };
        }
        /**
         * 当前下级所有节点
         * @see 实现类需重写该函数
         * @return
         */
        default List<Pipeline<?>> weis() {
            return null;
        }
        /**
         * 当前节点ID 
         * @see 实现类需重写该函数
         * @return
         */
        default String ID() {
            return null;
        }
        /**
         * 当前节点基础数据
         * @see 实现类需重写该函数
         * @return
         */
        default JSONObject DB() {
            return null;
        }
    }
}