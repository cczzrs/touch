package org.cczzrs.touch.controller;

import java.util.LinkedHashMap;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.cczzrs.core.redis.CacheUtil;
import org.cczzrs.touch.IRegistry;
import org.cczzrs.touch.IRegistry.Pipeline;
import org.cczzrs.touch.controller.result.IResult;
import org.cczzrs.touch.dnas.IManDna;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.netty.util.internal.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "测试初始功能", description = "测试初始功能 api")
@RestController
@RequestMapping("/t")
public class TestController {

    @Resource
    CacheUtil cacheUtil;
    
    public JSONObject newJO(){
        return new JSONObject(new LinkedHashMap<>());
    }
    public JSONObject newJO(String key, Object value){
        return new JSONObject(new LinkedHashMap<>()).fluentPut(key, value);
    }
    
    /**
     * 查询已有所有节点和关系线
     * 
    const tree_sl = { 
        nodes: [
          {"id": "Myriel", "group": 1},
          {"id": "Napoleon", "group": 5},
          {"id": "Mlle.Baptistine", "group": 1},
          {"id": "CountessdeLo", "group": 5},
          {"id": "Geborand", "group": 5},
          {"id": "Champtercier", "group": 1},
          {"id": "Cravatte", "group": 3},
          {"id": "Count", "group": 3},
          {"id": "OldMan", "group": 3},
          {"id": "Labarre", "group": 2},
          {"id": "Valjean", "group": 2},
          {"id": "Mme.Magloire", "group": 1},
          {"id": "tow-1", "group": 11},
          {"id": "tow-2", "group": 13},
          {"id": "tow-21", "group": 14},
          {"id": "tow-3", "group": 15},
          {"id": "tow-31", "group": 16},
          {"id": "tow-4", "group": 15}
        ], 
        links: [
          {"source": "Napoleon", "target": "Myriel", "value": 1},
          {"source": "Mlle.Baptistine", "target": "Myriel", "value": 8},
          {"source": "Mme.Magloire", "target": "Myriel", "value": 10},
          {"source": "Mme.Magloire", "target": "Mlle.Baptistine", "value": 6},
          {"source": "CountessdeLo", "target": "Myriel", "value": 1},
          {"source": "Geborand", "target": "Myriel", "value": 1},
          {"source": "Champtercier", "target": "Myriel", "value": 1},
          {"source": "Cravatte", "target": "Myriel", "value": 1},
          {"source": "Count", "target": "Myriel", "value": 2},
          {"source": "OldMan", "target": "Myriel", "value": 1},
          {"source": "Valjean", "target": "Labarre", "value": 1},
          {"source": "Valjean", "target": "Mme.Magloire", "value": 3},
          {"source": "Valjean", "target": "Mlle.Baptistine", "value": 3},
          {"source": "Valjean", "target": "Myriel", "value": 5},
          {"source": "tow-1", "target": "tow-2", "value": 11},
          {"source": "tow-1", "target": "tow-3", "value": 11},
          {"source": "tow-1", "target": "tow-4", "value": 11},
          {"source": "tow-2", "target": "tow-21", "value": 11},
          {"source": "tow-3", "target": "tow-31", "value": 11}
        ] 
      };
     * @return T
     */
    @ApiOperation(value = "查询已有所有节点和关系线", notes = "get")
    @GetMapping("/{type}")
    public IResult<?> getBy(@PathVariable("type") String type) {
        JSONArray nodes = new JSONArray();
        JSONArray links = new JSONArray();
        for (String id : IRegistry.bangd.keySet()) {
                // IRegistry.bangd.forEach((id, pl) -> {
            // pl.weis().forEach(plpl -> links.add(new JSONObject().fluentPut("s",id).fluentPut("t",plpl.DB().get("id")).fluentPut("v",plpl.q().poll())));
            for (Pipeline<?> plpl : IRegistry.bangd.get(id).weis()) {
                links.add(newJO("s",id).fluentPut("t",plpl.ID()).fluentPut("v",plpl.q().poll()));
            }
            nodes.add(newJO("id",id).fluentPut("g",IRegistry.bangd.get(id).DB().get("g")));
        }
        return IResult.Generator.genSuccessResult("SUCCESS", newJO("nodes", nodes).fluentPut("links", links));
    }
    @ApiOperation(value = "查询已有所有节点和关系线 - 便于更新对比数据结构", notes = "getJG")
    @GetMapping("/jg")
    public IResult<?> getJG(String type) {
        JSONObject nodes = newJO();
        JSONObject links = newJO();
        IRegistry.bangd.forEach((id, pl) -> {
            nodes.put(id, newJO("g",pl.DB().get("g")));
            JSONObject links_ = newJO();
            if(pl.weis().size()>0){
                // links.put(id, pl.weis().stream().map(plpl -> newJO(plpl.ID(), newJO("v",plpl.q().poll()))).toList());
                pl.weis().forEach(plpl -> links_.put(plpl.ID(), newJO("v",plpl.q().poll())));
                links.put(id, links_);
            }
        });
        return IResult.Generator.genSuccessResult("SUCCESS", newJO("nodes", nodes).fluentPut("links", links));
    }

    @ApiOperation(value = "初始化默认数据格式", notes = "initNodes")
    @PostMapping("/init")
    public IResult<?> initNodes(String id) {
        // 构建初始化数据
        /* 
            _CODE = db.getDoubleValue("code"); // 获取当前节点权重值
            _CODE_LS = db.getDoubleValue("code_ls"); // 获取当前节点临时权重值
            _PIDS = db.getJSONArray("pids").toJavaList(String.class);// 需连接到的位置（所有上级节点）
            dominant = new Dominant(db.getJSONObject("dominant")); // 初始化显性基因对象
                this._ID = db.getString("id");
                this._CODE = db.getDoubleValue("code");
                this._CODE_LS = db.getDoubleValue("codels");
                this.ODB = db.getJSONObject("odb");
            recessive = new Recessive(db.getJSONObject("recessive")); // 初始化隐性基因对象
                this._ID = db.getString("id");
                this._CODE = db.getDoubleValue("code");
                this._CODE_LS = db.getDoubleValue("codels");
                this.ODB = db.getJSONObject("odb");
            IRegistry.init(_ID, buildPipeline(), new HashSet<String>(_PIDS));// 注册 连接到自己的位置
         */
        JSONObject initDB = new JSONObject();
        initDB.put("id", "111111111"); // 获取当前节点ID
        initDB.put("code", 0.2); // 获取当前节点权重值
        initDB.put("code_ls", 0.01); // 获取当前节点临时权重值
        initDB.put("pids", new JSONArray()); // 需连接到的位置（所有上级节点）
        // initDB.put("pids", new JSONArray().fluentAdd("e")); // 需连接到的位置（所有上级节点）
        initDB.put("dominant", new JSONObject()
            .fluentPut("id", "111111111.1")
            .fluentPut("code", 0.002)
            .fluentPut("codels", 0.0001)
            .fluentPut("odb", new JSONObject())); // 初始化显性基因对象
        initDB.put("recessive", new JSONObject()
            .fluentPut("id", "111111111.01")
            .fluentPut("code", 0.002)
            .fluentPut("codels", 0.0001)
            .fluentPut("odb", new JSONObject())); // 初始化隐性基因对象

        JSONObject initDB2 = new JSONObject();
        initDB2.put("id", "111111112"); // 获取当前节点ID
        initDB2.put("code", 0.2); // 获取当前节点权重值
        initDB2.put("code_ls", 0.01); // 获取当前节点临时权重值
        initDB2.put("pids", new JSONArray().fluentAdd("111111111")); // 需连接到的位置（所有上级节点）
        initDB2.put("dominant", new JSONObject()
            .fluentPut("id", "111111112.1")
            .fluentPut("code", 0.002)
            .fluentPut("codels", 0.0001)
            .fluentPut("odb", new JSONObject())); // 初始化显性基因对象
        initDB2.put("recessive", new JSONObject()
            .fluentPut("id", "111111112.01")
            .fluentPut("code", 0.002)
            .fluentPut("codels", 0.0001)
            .fluentPut("odb", new JSONObject())); // 初始化隐性基因对象
    
        return IResult.Generator.genSuccessResult("init", new JSONObject().fluentPut("mdna", IManDna.init(initDB)).fluentPut("mdna2", IManDna.init(initDB2)));
    }
    
    @ApiOperation(value = "新增一个节点（默认随机，随机链接）", notes = "postNode")
    @PostMapping("/node")
    public IResult<?> postNode(String id) {
        // 构建初始化数据
        JSONObject initDB = new JSONObject();
        initDB.put("id", StringUtil.isNullOrEmpty(id)?System.currentTimeMillis()+"":id); // 获取当前节点ID
        initDB.put("code", 0.2); // 获取当前节点权重值
        initDB.put("code_ls", 0.01); // 获取当前节点临时权重值
        initDB.put("pids", new JSONArray()
            .fluentAdd(IManDna.iManDnas.get(0)._ID)
            .fluentAdd(IManDna.iManDnas.get(IManDna.iManDnas.size()-1)._ID)); // 需连接到的位置（所有上级节点）
        initDB.put("dominant", new JSONObject()
            .fluentPut("id", initDB.get("id")+".1")
            .fluentPut("code", 0.002)
            .fluentPut("codels", 0.0001)
            .fluentPut("odb", new JSONObject())); // 初始化显性基因对象
        initDB.put("recessive", new JSONObject()
            .fluentPut("id", initDB.get("id")+".2")
            .fluentPut("code", 0.002)
            .fluentPut("codels", 0.0001)
            .fluentPut("odb", new JSONObject())); // 初始化隐性基因对象

        return IResult.Generator.genSuccessResult("init", new JSONObject().fluentPut("mdna", IManDna.init(initDB)));
    }

    @PostMapping("/{id}")
    @ApiOperation(value = "指定节点发送模拟数据", notes = "sendData")
    public IResult<?> sendData(@PathVariable("id") String id) {
        for (IManDna imdna : IManDna.iManDnas) {
            if(imdna._ID.equals(id)){
                imdna.submitNextNodes();
            }
        }
        return IResult.Generator.genSuccessResult("ID:"+id);
    }

}