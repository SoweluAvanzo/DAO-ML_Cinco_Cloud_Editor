

const tileSize = 48;
const levelWidth = Math.floor(config.width / tileSize) * tileSize;
const levelHeight = Math.floor(config.height / tileSize) * tileSize;

kaboom({
  width: levelWidth,
  height: levelHeight + 240, // 240 for ui
  global: true,
  fullscreen: false,
  scale: 1,
  debug: true,
  clearColor: [0, 0, 0, 1],
})


// setup level
if(config.levels == undefined) {
  const level = [];
  const w = levelWidth / tileSize - 2;
  const h = levelHeight / tileSize - 1;

  level.push(
    "y" + "c".repeat(w) + "w"
  )
  for(var i = 0; i<h; i++) {
    level.push(
      "a" + " ".repeat(w) + "b"
    )
  }
  level.push(
    "x" + "d".repeat(w) + "z"
  )
  config.levels = [];
  config.levels.push(level);
}

// Game Sprites
loadRoot('https://i.imgur.com/')
// DO NOT COMMENT OUT!
/*loadSprite('player-going-left', '1Xq9biB.png')
loadSprite('player-going-right', 'yZIb8O2.png')
loadSprite('player-going-down', 'tVtlP6y.png')
loadSprite('player-going-up', 'UkV0we0.png')*/
loadSprite('player-going-up', 'tTRbX6n.png')
loadSprite('player-going-down', 'vZirtIy.png')
loadSprite('player-going-left', '3pBYDoz.png')
loadSprite('player-going-right', 'QBKaKFP.png')
loadSprite('left-wall', 'gLc7Rur.png')
loadSprite('top-wall', 'uneRZQX.png')
loadSprite('bottom-wall', 'vArkOXf.png')
loadSprite('right-wall', 'DOa7b8V.png')
loadSprite('bottom-left-wall', 'awnTfNC.png')
loadSprite('bottom-right-wall', '84oyTFy.png')
loadSprite('top-left-wall', 'xlpUxIm.png')
loadSprite('top-right-wall', 'z0OmBd1.jpg')
loadSprite('obstacle2', '6zeF0D0.png')
loadSprite('obstacle1', '9kTbx0t.png')
loadSprite('slicer', 'i07VcQ7.png')
loadSprite('skullLeft', '2wNKrtI.png')
loadSprite('skullRight', '94d8IZy.png')
loadSprite('kaboom', 'o9WizfI.png')
loadSprite('stairs', 'VghkL08.png')
loadSprite('heart', '36CIwge.png')
loadSprite('bg', 'px1hm72.png')  // 703 x 582

// Speeds (ASSGINMENT PARAMETERS)

scene('game', ({ level, score }) => {
  layers(['bg', 'obj', 'ui'], 'obj')

  // setup ids
  const skulls = get("skull");
  for(const sk of skulls) {
    console.log(sk);
  } 

  /**
   * MAP Editor (ASSIGNMENT 1)
   */
  const maps = config.levels;

  const levelCfg = {
    width: 48,
    height: 48,
    a: [sprite('left-wall'), solid(), 'wall'],
    b: [sprite('right-wall'), solid(), 'wall'],
    c: [sprite('top-wall'), solid(), 'wall'],
    d: [sprite('bottom-wall'), solid(), 'wall'],
    w: [sprite('top-right-wall'), solid(), 'wall'],
    x: [sprite('bottom-left-wall'), solid(), 'wall'],
    y: [sprite('top-left-wall'), solid(), 'wall'],
    z: [sprite('bottom-right-wall'), solid(), 'wall'],
  }
  addLevel(maps[level], levelCfg)

  // add background
  const scaleX = (levelWidth) / 1041;
  const scaleY = (levelHeight) / 862;
  add([sprite('bg'), scale(scaleX, scaleY), pos(0, 0), layer('bg')])
  


  function getSprite(type, id = 'LEFT') {
    return type == 'skull' ? (
        id == 'LEFT' ? 'skullLeft' :
        id == 'RIGHT' ? 'skullRight' : 
        undefined
      )
      : type == 'slicer' ? 'slicer'
      : type == 'player' ? (
        id == 'LEFT' ? 'player-going-left' :
        id == 'RIGHT' ? 'player-going-right' : 
        id == 'UP' ? 'player-going-up' : 
        id == 'DOWN' ? 'player-going-down':
        undefined
      )
      : type == 'heart' ? 'heart'
      : type == 'obstacle1' ? 'obstacle1'
      : type == 'obstacle2' ? 'obstacle2'
      : undefined;
  }

  /**
   * PLAYER
   */
  const player = add([
    sprite(getSprite('player')),
    pos(config.player.x, config.player.y), // start position
    {
      // right by default
      dir: vec2(1, 0),
      speed: config.player.speed,
      maxLife: config.player.maxLife,
    },
  ])
  player.action(() => {
    player.resolve()
  })
  player.overlaps('dangerous', (s) => {
    if(s._tags.includes('slicer')) {
      burn(s.attack);
    } else {
      burn(s.attack);
    }
  })

  /**
   * Spawn Enemies
   */
  for(const e of config.enemies) {
    add([
      pos(e.x, e.y), // start position
      sprite(getSprite(e.type)), 'dangerous', e.type, { dir: -1, timer: 0, speed: e.speed, attack: e.attack},
    ])
  }

  /**
   * Spawn Items
   */
  for(const item of config.items) {
    add([
      pos(item.x, item.y), // start position
      sprite(getSprite(item.type)), item.type, { dir: -1, timer: 0, heal: item.heal},
    ])
  }
  player.overlaps('heart', (l) => {
    destroy(l)
    heal(l.heal);
  })

  /**
   * Spawn Obstacles
   */
  for(const obstacle of config.obstacles) {
    add([
      pos(obstacle.x, obstacle.y), // start position
      sprite(getSprite(obstacle.type)), obstacle.type, , solid(), 'wall'
    ])
  }


  /**
   * UI
   */
  const uiPositionY = levelHeight + 120;
  const scoreLabel = add([
    text('0 Points'),
    origin('center'),
    pos(width() / 2, uiPositionY),
    layer('ui'),
    {
      value: score,
    },
    scale(2),
  ])
  const lifeLabel = add([
    text(player.maxLife + ' Life'),
    origin('center'),
    pos(width() / 2, uiPositionY + 25),
    layer('ui'),
    {
      value: player.maxLife,
    },
    scale(2),
  ])
  add([
    text('level ' + parseInt(level + 1)),
    origin('center'),
    pos(width() / 2, uiPositionY + 50),
    scale(2)
  ])

  /**
   * CONTROLS
   */
  /* controls (node controller {
    up = w,
    down = s,
    left = a,
    right = d,
    spriteUp = 'player-going-up',
    spriteDown = 'player-going-down',
    spriteLeft = 'player-going-left',
    spriteRight = 'player-going-right',  
  })
  */
  keyDown('a', () => {
    player.changeSprite(getSprite('player', 'LEFT'))
    player.move(-player.speed, 0)
    player.dir = vec2(-1, 0)
  })
  keyDown('d', () => {
    player.changeSprite(getSprite('player', 'RIGHT'))
    player.move(player.speed, 0)
    player.dir = vec2(1, 0)
  })
  keyDown('w', () => {
    player.changeSprite(getSprite('player', 'UP'))
    player.move(0, -player.speed)
    player.dir = vec2(0, -1)
  })
  keyDown('s', () => {
    player.changeSprite(getSprite('player', 'DOWN'))
    player.move(0, player.speed)
    player.dir = vec2(0, 1)
  })

  /**
   * ATTACK
   */
  function spawnKaboom(p) {
    const obj = add([sprite('kaboom'), pos(p), 'kaboom'])
    wait(0.1, () => {
      destroy(obj)
    })
  }
  keyPress('space', () => {
    spawnKaboom(player.pos.add(player.dir.scale(48)))
  })
  collides('kaboom', 'skull', (k,s) => {
    camShake(1)
    wait(1, () => {
      destroy(k)
    })
    destroy(s)
    scoreLabel.value++
    scoreLabel.text = scoreLabel.value + (scoreLabel.value == 1 ?  ' Point'  : ' Points')

    const skullCount = get("skull").length;
    if(skullCount <= 0) {
      go('win', { score: scoreLabel.value })
    }
  })


  /**
   * ENEMIES
   */

  /**
   * SKULL
   */
  action('skull', (s) => {
    s.move((s.dir * (s['directionX'] ?? 1)) * s.speed, (s.dir * (s['directionY'] ?? 1)) * s.speed)
    s.timer -= dt()
    if (s.timer <= 0) {
      s['directionX'] = (rand(0, 1))
      s['directionY'] = (rand(0, 1))
      s.dir = -s.dir
      s.timer = rand(5)
    }
    if(s.animation == undefined) {
      s.animation = false;
    }
    if(s.animation == false) {
      s.animation = true
      if(s.dir < 0) {
        s.changeSprite('skullLeft')
      } else {
        s.changeSprite('skullRight')
      }
      wait(1, () => s.animation = false)
    }
  })
  // Skull movements
  collides('skull', 'wall', (s, w) => {
    s.dir = -s.dir
    s.move(s.dir * s['directionX'] * s.speed * 2, s.dir * s['directionY']  * s.speed * 2)
  })

  /**
   * SLICER
   */
  action('slicer', (s) => {
    s.move(s.dir * s.speed, 0)
  })
  // Slicer movements
  collides('slicer', 'wall', (s, w) => {
    s.dir = -s.dir
    s.move(s.dir * s.speed, s.dir * s.speed)
  })

  /**
   * Life Modifier
   */

  function burn(strength = 1) {
    const life = player.maxLife
    lifeLabel.value -= strength
    lifeLabel.text = lifeLabel.value + (lifeLabel.value == 1 ?  ' Point'  : ' Points')
    camShake(2)
    if(lifeLabel.value <= 0) {
      lifeLabel.value = life;
      go('lose', { score: scoreLabel.value })
    }
  }
  function heal(strength = 1) {
    const life = player.maxLife
    lifeLabel.value += strength
    if(lifeLabel.value >= life) {
      lifeLabel.value = life
    }
    lifeLabel.text = lifeLabel.value + (lifeLabel.value == 1 ?  ' Point'  : ' Points')
  }
})


/**
 * END SCREEN
 */

scene('win', ({ score }) => {
  const winText = 'YOU WIN';
  const scoreText = score + (score == 1 ?  ' Point'  : ' Points');
  add([text(winText, 32), origin('center'), pos(width() / 2, height() / 2 - 75), color(0,1,0)]);
  add([text(scoreText, 32), origin('center'), pos(width() / 2, height() / 2)]);
  wait(2, () => {
    const pressSpaceText = add([text("PRESS SPACE", 24), origin('center'), pos(width() / 2 , height() / 2 + 100)]);
    let isChanging = false;
    pressSpaceText.action(() => {
      if(!isChanging) {
        isChanging = true;
        wait(0.5, () => {
          pressSpaceText.hidden = !pressSpaceText.hidden;
          isChanging = false;
        })
      }
    });
    keyPress('space', () => {
      go('game', {
        level: 0,
        score: 0,
      })
    })
  })
})

scene('lose', ({ score }) => {
  const gameOverText = 'GAME OVER';
  const scoreText = score + (score == 1 ?  ' Point'  : ' Points');
  add([text(gameOverText, 32), origin('center'), pos(width() / 2, height() / 2 - 75), color(1,0,0)]);
  add([text(scoreText, 32), origin('center'), pos(width() / 2, height() / 2)]);
  wait(2, () => {
    const pressSpaceText = add([text("PRESS SPACE", 24), origin('center'), pos(width() / 2 , height() / 2 + 100)]);
    let isChanging = false;
    pressSpaceText.action(() => {
      if(!isChanging) {
        isChanging = true;
        wait(0.5, () => {
          pressSpaceText.hidden = !pressSpaceText.hidden;
          isChanging = false;
        })
      }
    });
    keyPress('space', () => {
      go('game', {
        level: 0,
        score: 0,
      })
    })
  })
})


/**
 * START
 */
start('game', { level: 0, score: 0 })

