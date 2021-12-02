package org.cczzrs.touch.dnas;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * dna雄性基础类
 */
public class IManDna extends IDna {
    
    public static List<IManDna> iManDnas = new ArrayList<>();
    /**
     * 云加载DB（主导数据）
     */
    private IManDna(JSONObject db) {
        super(db);
    }
    public static IManDna init(JSONObject db){
        IManDna newMan = new IManDna(db);
        iManDnas.add(newMan);
        return newMan;
    }
    
    @Override
    public boolean submitNextNodes() {
        JSONObject outdb = new JSONObject();
        outdb.put("value", 1.5);
        BuildDB.put("outdb", outdb);
        return super.submitNextNodes();
    }
}
