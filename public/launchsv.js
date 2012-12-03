window.show_sequence_viewer = function(conf){
  var divId = "seqviewer01";
  var app, div, body, link;
  if (document.readyState === 'complete') {
    div = Ext.get(divId);
    if (div){
      div.remove();
      body = Ext.getBody();
      body.createChild({ tag: 'div', id: divId });
    }
    link = "?embedded=panorama&appname=FimmWidget";
    link += "&id=".concat(conf.id);
    link += "&v=".concat(conf.start);
    link += ":".concat(conf.end);
    for (var p in conf) {
      if (p === 'id' || p === 'start' || p === 'end') {
        continue;
      }
      if (conf.hasOwnProperty(p)) {
        link = link.concat('&'.concat(p.concat("=" + conf[p])));
      }
    }
    app = new SeqView.App(divId);
    app.on({
      /*
      'feature_clicked': function(view) {
        var cache = [];
        // http://stackoverflow.com/questions/11616630/json-stringify-avoid-typeerror-converting-circular-structure-to-json
        var s = JSON.stringify(view, function(key, value) {
          if (typeof value === 'object' && value !== null) {
              if (cache.indexOf(value) !== -1) {
                  // Circular reference found, discard key
                  return;
              }
              // Store value in our collection
              cache.push(value);
          }
          return value;
        });
        cache = null; // Enable garbage collection
        console.log(s);
      }
      */
    });
    app.load(link);
  }
};

