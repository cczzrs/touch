package org.cczzrs.touch.dnas;

import com.alibaba.fastjson.JSONObject;

/**
 * dna雄性基础类
 */
public class IManDna extends IDna {
    
    /**
     * 云加载DB（主导数据）
     */
    public IManDna(JSONObject db) {
        super(db);
    }

    public void d() {
        // super.dominant=null;
        // super.recessive=null;
    }
}
