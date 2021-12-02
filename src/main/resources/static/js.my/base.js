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
