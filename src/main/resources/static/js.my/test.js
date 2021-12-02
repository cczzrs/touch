// .jsonUrl('../datasets/blocks.json')
// .nodeAutoColorBy('user')
// .nodeLabel(node => `${node.user}: ${node.description}`)

// const Graph = ForceGraph3D()(elem)
//     // .jsonUrl('../example/datasets/miserables.json')
//     .graphData(gData)
//     .nodeLabel('id')
//     .nodeAutoColorBy('group');
function addInterval(bl){
  if(!bl){
    setInterval(() => {
      const { nodes, links } = Graph.graphData();
      const ind = Math.round(Math.random() * (nodes.length-1));

      var node = JSON.parse(JSON.stringify(nodes[ind]));
      // debugger;
      node.id = guid();
      node.group = ind%9;
      node.index = nodes.length;
      node.x -= 85;


      // buildNode = buildNode(id, ind, nodes[ind]);
      // debugger;
      Graph.graphData({
        nodes: [...nodes, node],
        links: [...links, {"source": node.id, "target": nodes[ind].id, "value": ind%9}],
      });
    }, 5000);
  }
  return bl;
}

/**
 * 节点点击事件，移动镜头
 */
function myNodeClick(node) { // 节点点击事件，移动镜头
  // Aim at node from outside it
  const distance = 100;
  const distRatio = 1 + distance/Math.hypot(node.x, node.y, node.z);
  Graph.cameraPosition(
    { x: node.x * distRatio, y: node.y * distRatio, z: node.z * distRatio }, // new position
    node, // lookAt ({ x, y, z })
    1600  // ms transition duration
  );
}

/**
 * 获取最新全量数据
 */
var getDBInterval = null;
function getDBIntervalF(bl, t){
            debugger;
  if(bl){
    if(getDBInterval==null){
      getDBInterval = setInterval(() => {
          $.ajax({url:URL_BASE+"/t/1",success:function(d){
            // console.log(d.data);
            if(d.code==200){
              Graph.graphData(d.data);
            }
          }});
      }, t?t:5000);
    }    
  }else{
    if(getDBInterval!=null){
      clearInterval(getDBInterval);
      getDBInterval = null;
    }
  }
  return getDBInterval;
}

/**##################################################
 * 获取最新全量数据
 */
function setNewInsert(){ 
  $.ajax({url:URL_BASE+"/t/jg",success:function(d){
    if(d.code==200){
      const newNodes = d.data.nodes;
      const newLinks = d.data.links;
      const { nodes, links } = Graph.graphData();
      // 现有数据对比最新数据的变化数据，以便动态更新节点的最小浮动
      buildNewInsert(nodes, links, newNodes, newLinks);
      console.log(nodes, links);
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
function buildNewInsert(nodes, links, newNodes, newLinks){
  for (var i = 0; i < nodes.length; i++) {
    if(newNodes[nodes[i].id]==undefined){
      nodes.remove(i);
      i--;
    } else {
      if(nodes[i].g != newNodes[nodes[i].id].g){
        // 变动数据
        nodes[i].g = newNodes[nodes[i].id].g;
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
      if(Object.keys(newLinks[links[i].s]).length==0){
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