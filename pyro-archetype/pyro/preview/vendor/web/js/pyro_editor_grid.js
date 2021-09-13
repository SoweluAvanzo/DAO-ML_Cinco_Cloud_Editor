(function(){

  var grid = null;
  var gridSelector = '.grid-stack';
  var gridEl = null;
  var widgetEls = [];
  
  var gridOptions = {
	cellHeight: 80,
	verticalMargin: 10,
	draggable: {
	  handle: '.grid-stack-item-header'
	},
	auto: false
  };
  
  function handleGridChange(e, items) {
    if (gridEl.is($(e.target))) { // change events may bubble up from elements inside the gridstack
  	  document.dispatchEvent(new CustomEvent("editor:grid-change", {
  	    detail: {items: items}
      }));
  	}
  }

  function initGrid(event) { 
 	gridEl = $(gridSelector);
    if (gridEl[0] == null) {
      window.setTimeout(function() {
        initGrid(event);
      }, 100);
    } else {
      gridEl.gridstack(gridOptions);
      gridEl.on('change', handleGridChange);
      
      grid = gridEl.data('gridstack');
      grid.enable();
      
      function setResizeMutex() {
      	$cursor_manager_resizestart = true;
      }
      
      function releaseResizeMutex() {
        $cursor_manager_resizestart = false;
      }
            
      gridEl.on('dragstart', setResizeMutex);
      gridEl.on('dragstop', releaseResizeMutex);
      gridEl.on('resizestart', setResizeMutex);
      gridEl.on('gsresizestop', releaseResizeMutex);
    }
  }
  
  function reinitGrid(e) {  
    if (grid != null && gridEl != null) {
      var els = Array.from(gridEl[0].querySelectorAll('.grid-stack-item'));
      if (els.reduce((acc, val) => acc && widgetEls.indexOf(val) > -1, true)) return;
      gridEl.off('change');
      
      var idMap = {};      
      e.detail.items.forEach((item) => {
        idMap["" + item.id] = item;

      });
                    
      grid.removeAll(false);
      els.forEach((n) => {
        var id = n.getAttribute("data-gs-id");
        if(id != null) {
          grid.addWidget(n, idMap[id].x, idMap[id].y, idMap[id].width, idMap[id].height, false);
        }
      	
      });
      widgetEls = els;      
      gridEl.on('change', handleGridChange); 
    }
  }

  document.addEventListener("editor:grid-init", initGrid);
  document.addEventListener("editor:grid-reinit", reinitGrid);
}());
