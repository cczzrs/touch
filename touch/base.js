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
