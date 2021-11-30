Array.prototype.remove=function(dx) {
  if(isNaN(dx)||dx>this.length){return false;}
    for(var i=this[dx].index;i<this.length-1;i++) {
        this[i]=this[i+1]
    }
    this.length-=1
  }

function guid() {
    return 'xxxxxxxx-yxxx-yxxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => c == 'x' ?(Math.random() * 16 | 0).toString(16) : ((Math.random() * 16 | 0) & 0x3 | 0x8).toString(16));
}

// .jsonUrl('../datasets/blocks.json')
// .nodeAutoColorBy('user')
// .nodeLabel(node => `${node.user}: ${node.description}`)


// const Graph = ForceGraph3D()(elem)
//     // .jsonUrl('../example/datasets/miserables.json')
//     .graphData(gData)
//     .nodeLabel('id')
//     .nodeAutoColorBy('group');


// function buildNode(id, rnum, node){
//   var ret = {node:{"id": id, "group": rnum%9}
//     ,link:{"source": id, "target": node.id, "value": rnum%9}
//   };
//   return ret;
// }
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
 * http://192.168.31.31:8000/t/1
 */
var getDBInterval = null;
var getDBInterval_url = "http://192.168.31.31:8000/t/1";
function getDBIntervalF(bl, t){
            debugger;
  if(bl){
    if(getDBInterval==null){
      getDBInterval = setInterval(() => {
          $.ajax({url:getDBInterval_url,success:function(d){
            console.log(d.data);
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