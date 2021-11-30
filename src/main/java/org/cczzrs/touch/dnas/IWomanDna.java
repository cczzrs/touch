package org.cczzrs.touch.dnas;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

/**
 * dna雌性基础类
 */
public class IWomanDna extends IDna {
    
    public static List<IWomanDna> iWomanDna = new ArrayList<>();
    /**
     * 云加载DB（主导数据）
     */
    private IWomanDna(JSONObject db) {
        super(db);
    }
    public IWomanDna init(JSONObject db){
        iWomanDna.add(new IWomanDna(db));
        return iWomanDna.get(iWomanDna.size());
    }
    @Override
    public boolean submitNextNodes() {
        JSONObject outdb = new JSONObject();
        outdb.put("value", 1.1);
        BuildDB.put("outdb", outdb);
        return super.submitNextNodes();
    }
}
