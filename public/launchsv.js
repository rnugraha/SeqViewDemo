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
      "feature_clicked": function(view){console.log("event 'feature_clicked'");},
      "panorama_image_loaded": function(view){console.log("event 'panorama_image_loaded'");},
      "graphical_image_loaded": function(view){console.log("event 'graphical_image_loaded'");},
      "marker_created": function(view){console.log("event 'marker_created'");},
      "marker_deleted": function(view){console.log("event 'marker_deleted'");},
      "origin_changed": function(view){console.log("event 'origin_changed'");},
      "strand_changed": function(view){console.log("event 'strand_changed'");},
      "visible_range_changed": function(view){console.log("event 'visible_range_changed'");},
      "ui_visible_range_changed": function(view){console.log("event 'ui_visible_range_changed'");},
      "api_visible_range_changed": function(view){console.log("event 'api_visible_range_changed'");},
      "configuration_changed": function(view){console.log("event 'configuration_changed'");},
      "selection_changed": function(view){console.log("event 'selection_changed'");},
      "user_changed_selection": function(view){console.log("event 'user_changed_selection'");},
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

