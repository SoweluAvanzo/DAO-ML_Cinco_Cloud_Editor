
/* ==============================================
 *
 *  Glue lines
 *
 * ============================================== */

window.pyroGlueLines = function(joint, graph, paper, _options = {}) {
  if (joint == null) throw 'joint could not be found';

  const options = Object.assign({
    shapeType: 'GlueLine',
    verticalLineColor: 'red',
    horizontalLineColor: 'blue',
    middleLineColor: 'green',
    offset: 10,
    filterFn: () => true
  }, _options);

  /**
   * Check if a value is in an interval.
   *
   * @param value {number}
   * @param from {number}
   * @param to {number}
   * @returns {boolean}
   */
  function isInRange(value, from, to) {
    return value >= from && value <= to;
  }

  /**
   * Get the coordinates of a jointjs element.
   *
   * @param el The element.
   * @returns {({x: number, y: number})[]}
   */
  function getRectangleCoordinates(el) {
    const x = el.attributes.position.x;
    const y = el.attributes.position.y;
    const width = el.attributes.size.width;
    const height = el.attributes.size.height;

    return [
      {x: x, y: y}, // top left
      {x: x + width, y: y}, // top right
      {x: x + width, y: y + height},  // bottom right
      {x: x, y: y + height} // bottom left
    ];
  }

  /**
   * Get the center point of an element.
   *
   * @param el The element.
   * @returns {{x: number, y: number}}
   */
  function getRectangleCenter(el) {
    return {
      x: el.attributes.position.x + Math.floor(el.attributes.size.width / 2),
      y: el.attributes.position.y + Math.floor(el.attributes.size.height / 2)
    };
  }

  /**
   * Test if any y coordinate in a set of points if near another y coordinate.
   *
   * @param points The list of points to use the y coordinates from.
   * @param y The target y coordinate.
   * @param offset The allowed distance to the y coordinate.
   * @returns {boolean}
   */
  function isAnyYCoordinateNear(points, y, offset) {
    for (let p of points) {
      if (isInRange(Math.abs(p.y - y), 0, offset)) return true;
    }
    return false;
  }

  /**
   * Test if any x coordinate in a set of points if near another x coordinate.
   *
   * @param points The list of points to use the x coordinates from.
   * @param x The target x coordinate.
   * @param offset The allowed distance to the x coordinate.
   * @returns {boolean}
   */
  function isAnyXCoordinateNear(points, x, offset) {
    for (let p of points) {
      if (isInRange(Math.abs(p.x - x), 0, offset)) return true;
    }
    return false;
  }

  /**
   * Contains glue lines on the x axis.
   *
   * @type {{coordinate: number, element: dia.Element}}
   */
  let horizontalGlueLines = {};

  /**
   * Contains glue lines on the y axis.
   *
   * @type {{coordinate: number, element: dia.Element}}
   */
  let verticalGlueLines = {};

  let enabled = false;
  let elementPointerUpHandler;
  let cellPointerUpHandler;

  /**
   * Removes all glue lines from the paper.
   */
  function removeAllGlueLines() {
    for (let c in horizontalGlueLines)
      graph.removeCells(horizontalGlueLines[c]);

    for (let c in verticalGlueLines)
      graph.removeCells(verticalGlueLines[c]);

    horizontalGlueLines = {};
    verticalGlueLines = {};
  }

  /**
   * Removes a glue line.
   *
   * @param glueLines The map of glue lines to remove a line from.
   * @param c The x or y value
   */
  function _removeGlueLine(glueLines, c) {
    if (glueLines[c] != null) {
      glueLines[c].forEach(c2 => graph.removeCells(c2));
      delete glueLines[c];
    }
  }

  /**
   * Remove a horizontal glue line.
   *
   * @param y The y coordinate of the glue line.
   */
  function removeHorizontalGlueLine(y) {
    _removeGlueLine(horizontalGlueLines, y);
  }

  /**
   * Remove a vertical glue line.
   *
   * @param x The x coordinate of the glue line.
   */
  function removeVerticalGlueLine(x) {
    _removeGlueLine(verticalGlueLines, x);
  }

  /**
   * Draws a glue line to the paper.
   *
   * @param glueLines The map of glue lines to add a new line to.
   * @param c The x or y value where the glue line is added.
   * @param start The width of the glue line.
   * @param end The height of the glue line.
   * @param color The color the of the glue line.
   */
  function _addGlueLine(glueLines, c, start, end, color) {
    // if a glue line exists at the given coordinates, don't add another one
    if (glueLines[c] == null) {

      const startNode = new joint.shapes.standard.Rectangle();
      startNode.attr({
        type: `${options.shapeType}.glueLineEndNode`
      });
      startNode.position(start.x, start.y);
      startNode.resize(0, 0);

      const endNode = new joint.shapes.standard.Rectangle();
      endNode.attr({
        type: `${options.shapeType}.glueLineEndNode`
      });
      endNode.position(end.x, end.y);
      endNode.resize(0, 0);

      const link = new joint.shapes.standard.Link();
      link.attr({
        line: {
          stroke: color,
          sourceMarker: null,
          targetMarker: null,
          strokeWidth: 1
        }
      });
      link.source(startNode);
      link.target(endNode);

      startNode.addTo(graph);
      endNode.addTo(graph);
      link.addTo(graph);

      glueLines[c] = [startNode, endNode, link];
    }
  }

  /**
   * Add a horizontal glue line.
   *
   * @param y The y coordinate.
   * @param color The color of the glue line.
   */
  function addHorizontalGlueLine(y, color) {
    color = color == null ? options.horizontalLineColor : color;

    const rect = paper.el.getBoundingClientRect();
    const start = {x: paper.clientToLocalPoint(rect.x, 0).x, y: y};
    const end = {x: paper.clientToLocalPoint(rect.x + rect.width, 0).x, y: y};
    
    _addGlueLine(horizontalGlueLines, y, start, end, color);
  }

  /**
   * Add a vertical glue line.
   *
   * @param x The x coordinate.
   * @param color The color of the glue line.
   */
  function addVerticalGlueLine(x, color) {
    color = color == null ? options.verticalLineColor : color;

    const rect = paper.el.getBoundingClientRect();
    const start = {x: x, y: paper.clientToLocalPoint(0, rect.y).y};
    const end = {x: x, y: paper.clientToLocalPoint(0, rect.y + rect.height).y};

    _addGlueLine(verticalGlueLines, x, start, end, color);
  }

  function updateHorizontalGlueLine(isVisible, y, color) {
    if (isVisible) {
      addHorizontalGlueLine(y, color);
    } else {
      removeHorizontalGlueLine(y);
    }
  }

  function updateVerticalGlueLine(isVisible, x, color) {
    if (isVisible) {
      addVerticalGlueLine(x, color);
    } else {
      removeVerticalGlueLine(x);
    }
  }

  function getProximity(el1, el2) {
    const points = getRectangleCoordinates(el1);
    const pointsWithMiddle = [...points, getRectangleCenter(el1)];

    const centerEl2 = getRectangleCenter(el2);

    return  {
      el: el2,
      top: isAnyYCoordinateNear(points, el2.attributes.position.y, options.offset),
      bottom: isAnyYCoordinateNear(points, el2.attributes.position.y + el2.attributes.size.height, options.offset),
      left: isAnyXCoordinateNear(points, el2.attributes.position.x, options.offset),
      right: isAnyXCoordinateNear(points, el2.attributes.position.x + el2.attributes.size.width, options.offset),
      centerV: isAnyXCoordinateNear(pointsWithMiddle, centerEl2.x, options.offset) || isAnyXCoordinateNear(pointsWithMiddle, centerEl2.x, options.offset),
      centerH: isAnyYCoordinateNear(pointsWithMiddle, centerEl2.y, options.offset) || isAnyYCoordinateNear(pointsWithMiddle, centerEl2.y, options.offset),
    }
  }

  function _isElementIn(el1, el2) {
    return (el2.attributes.position.x + el2.attributes.size.width) > (el1.attributes.position.x + el1.attributes.size.width)
        && (el2.attributes.position.x) < (el1.attributes.position.x)
        && (el2.attributes.position.y) < (el1.attributes.position.y)
        && (el2.attributes.position.y + el2.attributes.size.height) > (el1.attributes.position.y + el1.attributes.size.height)    
  }

  function getElementsNear(el) {
    return graph.getElements()
      .filter(e => {        
        return e !== el  // element is not near itself
          && e.attributes.attrs.type !== `${options.shapeType}.glueLineEndNode` // element is not glue line node
          && !_isElementIn(e, el)
      })
      .filter(options.filterFn) // apply filter function
      .map(e => getProximity(el, e)) // calculate proximity to other elements
      .filter(p => p.top || p.bottom || p.left || p.right || p.centerV || p.centerH)
  }

  function handleChangePosition(el, pos) {
    if (!enabled) return;

    const nearElements = getElementsNear(el, pos);

    if (nearElements.length === 0) {
      removeAllGlueLines();
    } else {
      nearElements.forEach(p => {
        const leftX = p.el.attributes.position.x;
        updateVerticalGlueLine(p.left, leftX);

        const rightX = p.el.attributes.position.x + p.el.attributes.size.width;
        updateVerticalGlueLine(p.right, rightX);

        const centerV = p.el.attributes.position.x + Math.floor(p.el.attributes.size.width / 2);
        updateVerticalGlueLine(p.centerV, centerV, options.middleLineColor);

        const topY = p.el.attributes.position.y;
        updateHorizontalGlueLine(p.top, topY);

        const bottomY = p.el.attributes.position.y + p.el.attributes.size.height;
        updateHorizontalGlueLine(p.bottom, bottomY);

        const centerH = p.el.attributes.position.y + Math.floor(p.el.attributes.size.height / 2);
        updateHorizontalGlueLine(p.centerH, centerH, options.middleLineColor);
      });
    }
  }

  function enable() {
    if (!enabled) {
      elementPointerUpHandler = paper.on('element:pointerup', removeAllGlueLines);
      cellPointerUpHandler = paper.on('cell:pointerup', removeAllGlueLines);
      enabled = true;
    }
  }

  function disable() {
    if (enabled) {
      paper.off('element:pointerup', elementPointerUpHandler);
      paper.off('cell:pointerup', cellPointerUpHandler);
      removeAllGlueLines();
      enabled = false;
    }
  }

  /* Return lib */
  return {
    handleChangePosition,
    enable,
    disable
  }
};





