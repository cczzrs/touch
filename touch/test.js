
/**
 * 获取最新全量数据
 * http://192.168.31.31:8000/t/jg
 */
 var t1_t = true;
function t1(){ 
  $.ajax({url:"http://192.168.31.31:8000/t/jg",success:function(d){
    console.log(d.data);
    if(d.code==200){
      const newNodes = d.data.nodes;
      const newLinks = d.data.links;
      const { nodes, links } = Graph.graphData();
      // 现有数据对比最新数据的变化数据，以便动态更新节点的最小浮动
      if(t1_t){
        t1_1(nodes, links, newNodes, newLinks);
      }
      Graph.graphData({
        nodes: nodes,
        links: links
      });
    }
  }});
}

/* d.data: {
      "nodes": {
        "111111111": {
          "g": 0
        },
        "111111112": {
          "g": 0
        }
      },
      "links": {
        "111111111": {
          "111111112": {
            "v": 0
          }
        }
      }
 */
// 现有数据对比最新数据的变化数据，以便动态更新节点的最小浮动    
function t1_1(nodes, links, newNodes, newLinks){
  for (var i = 0; i < nodes.length; i++) {
    if(newNodes[nodes[i].id]==undefined){
      nodes.remove(i);
      i--;
    } else {
      if(nodes[i].g != newNodes[nodes[i].id]){
        // 变动数据
        nodes[i].g = newNodes[nodes[i].id];
        // 是否重置 color
      }
      delete newNodes[nodes[i].id];
    }
  }
  for (var i = 0; i < links.length; i++) {
    if(newLinks[links[i].s]==undefined || newLinks[links[i].s][links[i].t]==undefined){
      links.remove(i);
      i--;
    } else {
      if(links[i].v != newLinks[links[i].s][links[i].t].v){
        // 变动数据
        links[i].v = newLinks[links[i].s][links[i].t].v;
        // 是否重置 color
      }
      delete newLinks[links[i].s][links[i].t];
      if(newLinks[links[i].s].length==0){
        delete newLinks[links[i].s];
      }
    }
  }
  for (var key in newNodes) {
    newNodes[key]["id"]=key;
    nodes.push(newNodes[key]);
  }
  for (var key in newLinks) {
    for (var key2 in newLinks[key]) {
      newLinks[key][key2]["s"]=key;
      newLinks[key][key2]["t"]=key2;
      links.push(newLinks[key][key2]);
    }
  }
}