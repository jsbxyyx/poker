var config = {
    type: Phaser.AUTO,
    parent: 'game',
    width: 960,
    heigth: 540,
    scale: {
        mode: Phaser.Scale.FIT,
    },
    scene: {
        init: init,
        preload: preload,
        create: create,
        update: update,
    },
    dom: {
        createContainer: true
    },
    physics: {
        default: 'arcade',
        arcade: {
            gravity: false
        },
    }
};
var game = new Phaser.Game(config);
var g;

var room = {
    'roomFirst': 0,
    'roomStart': '',
    'player': [{}, {}],
    'scoreText': null,
}

function init() {
    console.log('init...');
    g = this;
}

function preload() {
    console.log('preload...');
    this.load.image('bg', 'img/bg.png');
    this.load.image('user', 'img/user.jpg');

    this.load.image('p0-1', 'img/0-1.jpg');
    this.load.image('p0-2', 'img/0-2.jpg');

    this.load.image('p1-2', 'img/1-2.jpg');
    this.load.image('p1-3', 'img/1-3.jpg');
    this.load.image('p1-4', 'img/1-4.jpg');
    this.load.image('p1-5', 'img/1-5.jpg');
    this.load.image('p1-6', 'img/1-6.jpg');
    this.load.image('p1-7', 'img/1-7.jpg');
    this.load.image('p1-8', 'img/1-8.jpg');
    this.load.image('p1-9', 'img/1-9.jpg');
    this.load.image('p1-10', 'img/1-10.jpg');
    this.load.image('p1-j', 'img/1-j.jpg');
    this.load.image('p1-q', 'img/1-q.jpg');
    this.load.image('p1-k', 'img/1-k.jpg');
    this.load.image('p1-a', 'img/1-a.jpg');

    this.load.image('p2-2', 'img/2-2.jpg');
    this.load.image('p2-3', 'img/2-3.jpg');
    this.load.image('p2-4', 'img/2-4.jpg');
    this.load.image('p2-5', 'img/2-5.jpg');
    this.load.image('p2-6', 'img/2-6.jpg');
    this.load.image('p2-7', 'img/2-7.jpg');
    this.load.image('p2-8', 'img/2-8.jpg');
    this.load.image('p2-9', 'img/2-9.jpg');
    this.load.image('p2-10', 'img/2-10.jpg');
    this.load.image('p2-j', 'img/2-j.jpg');
    this.load.image('p2-q', 'img/2-q.jpg');
    this.load.image('p2-k', 'img/2-k.jpg');
    this.load.image('p2-a', 'img/2-a.jpg');

    this.load.image('p3-2', 'img/3-2.jpg');
    this.load.image('p3-3', 'img/3-3.jpg');
    this.load.image('p3-4', 'img/3-4.jpg');
    this.load.image('p3-5', 'img/3-5.jpg');
    this.load.image('p3-6', 'img/3-6.jpg');
    this.load.image('p3-7', 'img/3-7.jpg');
    this.load.image('p3-8', 'img/3-8.jpg');
    this.load.image('p3-9', 'img/3-9.jpg');
    this.load.image('p3-10', 'img/3-10.jpg');
    this.load.image('p3-j', 'img/3-j.jpg');
    this.load.image('p3-q', 'img/3-q.jpg');
    this.load.image('p3-k', 'img/3-k.jpg');
    this.load.image('p3-a', 'img/3-a.jpg');

    this.load.image('p4-2', 'img/4-2.jpg');
    this.load.image('p4-3', 'img/4-3.jpg');
    this.load.image('p4-4', 'img/4-4.jpg');
    this.load.image('p4-5', 'img/4-5.jpg');
    this.load.image('p4-6', 'img/4-6.jpg');
    this.load.image('p4-7', 'img/4-7.jpg');
    this.load.image('p4-8', 'img/4-8.jpg');
    this.load.image('p4-9', 'img/4-9.jpg');
    this.load.image('p4-10', 'img/4-10.jpg');
    this.load.image('p4-j', 'img/4-j.jpg');
    this.load.image('p4-q', 'img/4-q.jpg');
    this.load.image('p4-k', 'img/4-k.jpg');
    this.load.image('p4-a', 'img/4-a.jpg');

    this.load.image('pback', 'img/back.png');
}

function create() {
    console.log('create...');
    $('#g-start').on('click', function (e) {
        new_ws(g);
    });
    $('#g-exit').on('click', function (e) {
        close_ws(g);
    });

    var bg = this.add.sprite(this.game.config.width / 2, 0, 'bg');
    bg.setOrigin(0.5, 0);
}

function update() {

}

var cmd_map = {
    'PING': function (msg) {
        ping_();
    },
    'JOIN_ROOM': function (roomid, userid, nickname) {
        send(g.websocket, {
            'type': 'JOIN_ROOM',
            'data': {
                'roomid': roomid,
                'userid': userid,
                'nickname': nickname
            }
        });
    },
    'JOIN_ROOM_RESP': function (msg) {
        if (msg.data == 1) {
            console.log('wait user join room...');
        }
    },
    'ROOM_START': function (msg) {
        console.log('room start...');
        room['roomStart'] = msg.data;

        if (room['jiaoText'] == null) {
            var jiaoText = g.add.text(280, g.game.config.height / 2, '叫', {
                'fontSize': '40px'
            }).setVisible(false);
            jiaoText.setInteractive(
                new Phaser.Geom.Rectangle(0, 0, jiaoText.width, jiaoText.height),
                Phaser.Geom.Rectangle.Contains
            ).on('pointerdown', function () {
                jiao();
            });
            room['jiaoText'] = jiaoText;
        }

        if (room['genText'] == null) {
            var genText = g.add.text(280, g.game.config.height / 2, '跟', {
                'fontSize': '40px'
            }).setVisible(false);
            genText.setInteractive(
                new Phaser.Geom.Rectangle(0, 0, genText.width, genText.height),
                Phaser.Geom.Rectangle.Contains
            ).on('pointerdown', function () {
                gen();
            });
            room['genText'] = genText;
        }

        if (room['buYaoText'] == null) {
            var buYaoText = g.add.text(280, g.game.config.height / 2 + 50, '不要', {
                'fontSize': '20px'
            }).setVisible(false);
            buYaoText.setInteractive(
                new Phaser.Geom.Rectangle(0, 0, buYaoText.width, buYaoText.height),
                Phaser.Geom.Rectangle.Contains
            ).on('pointerdown', function () {
                buYao();
            });
            room['buYaoText'] = buYaoText;
        }

        if (room['kaiText'] == null) {
            var kaiText = g.add.text(280, g.game.config.height / 2 + 50 + 20 + 10, '开', {
                'fontSize': '20px',
                'color': 'gray',
            }).setVisible(false);
            kaiText.setInteractive(
                new Phaser.Geom.Rectangle(0, 0, kaiText.width, kaiText.height),
                Phaser.Geom.Rectangle.Contains
            ).on('pointerdown', function () {
                kai();
            });
            room['kaiText'] = kaiText;
        }
    },
    'TAKE_CARD': function (msg) {
        var idx = room['roomStart'] == 0 ? 0 : 1;
        var player = room['player'][idx];
        var player1 = room['player'][(idx + 1) > 1 ? 0 : 1];
        player['c'] = [];
        player1['c'] = [];
        for (var i in msg.data) {
            var c = g.add.sprite(100 + (i * 30), g.game.config.height / 2, 'p' + msg.data[i]);
            player['c'].push(c);
            var c1 = g.add.sprite(g.game.config.width - 100 - (i * 30), g.game.config.height / 2, 'pback');
            player1['c'].push(c1);
        }
    },
    'ROOM_FIRST': function (msg) {
        room['roomFirst'] = msg.data;
        if (room['roomFirst'] == room['roomStart']) {
            console.log('ROOM_FIRST visible true...');
            room['jiaoText'].setVisible(true);
            room['genText'].setVisible(false);
            room['buYaoText'].setVisible(true);
            room['kaiText'].setVisible(true);
        } else {
            console.log('ROOM_FIRST visible false...');
            room['jiaoText'].setVisible(false);
            room['genText'].setVisible(false);
            room['buYaoText'].setVisible(false);
            room['kaiText'].setVisible(false);
        }
    },
    'ROOM_JIAO': function (roomid, userid) {
        send(g.websocket, {
            'type': 'ROOM_JIAO',
            'data': {
                'roomid': roomid,
                'userid': userid,
                'score': 5
            }
        });
    },
    'ROOM_GEN': function (roomid, userid) {
        send(g.websocket, {
            'type': 'ROOM_GEN',
            'data': {
                'roomid': roomid,
                'userid': userid,
            }
        });
    },
    'ROOM_KAI': function (roomid, userid) {
        send(g.websocket, {
            'type': 'ROOM_KAI',
            'data': {
                'roomid': roomid,
                'userid': userid,
            }
        });
    },
    'ROOM_BU_YAO': function (roomid, userid) {
        send(g.websocket, {
            'type': 'ROOM_BU_YAO',
            'data': {
                'roomid': roomid,
                'userid': userid,
            }
        });
    },
    'ROOM_SCORE_RESP': function (msg) {
        if (room['scoreText'] == null) {
            room['scoreText'] = g.add.text(g.game.config.width / 2 - 50, 5, 'score: 0', {
                'fontSize': '30px',
                'color': 'red',
            });
        }
        room['scoreText'].setText('score: ' + msg.data);
    },
    'ROOM_JIAO_RESP': function (msg) {
        var next = msg.data;
        if (next == room['roomStart']) {
            console.log('ROOM_JIAO_RESP visible true...');
            if (room['roomFirst'] == room['roomStart']) {
                room['jiaoText'].setVisible(true);
            } else {
                room['genText'].setVisible(true);
            }
            room['buYaoText'].setVisible(true);
            room['kaiText'].setVisible(true);
        } else {
            console.log('ROOM_JIAO_RESP visible false...');
            room['jiaoText'].setVisible(false);
            room['genText'].setVisible(false);
            room['buYaoText'].setVisible(false);
            room['kaiText'].setVisible(false);
        }
    },
    'ROOM_WIN': function (msg) {
        var str = '';
        var winIdx = msg.data['winIdx'];
        if (winIdx == -1) {
            str = '平';
        } else if (winIdx == room['roomStart']) {
            str = '您赢了';
        } else {
            str = '对方赢了';
        }

        room['jiaoText'].setVisible(false);
        room['genText'].setVisible(false);
        room['buYaoText'].setVisible(false);
        room['kaiText'].setVisible(false);

        if (room['winText'] == null) {
            var winText = g.add.text(g.game.config.width / 2 - 50, g.game.config.height / 2, '', {
                'fontSize': '50px',
                'color': 'red',
            });
            room['winText'] = winText;
        }
        room['winText'].setText(str);
        room['winText'].setVisible(true);

        if (room['againText'] == null) {
            var againText = g.add.text(g.game.config.width / 2, g.game.config.height / 2 + 50 + 20, 'again', {
                'fontSize': '20px',
                'color': 'red',
            });
            againText.setInteractive(
                new Phaser.Geom.Rectangle(0, 0, againText.width, againText.height),
                Phaser.Geom.Rectangle.Contains
            ).on('pointerdown', function () {
                winText.setVisible(false);
                closeText.setVisible(false);
                again();
            });
            room['againText'] = againText;
        }
        room['againText'].setVisible(true);

        if (room['closeText'] == null) {
            var closeText = g.add.text(g.game.config.width / 2, g.game.config.height / 2 + 50 + 20 + 20, 'close', {
                'fontSize': '20px',
                'color': 'gray',
            });
            closeText.setInteractive(
                new Phaser.Geom.Rectangle(0, 0, closeText.width, closeText.height),
                Phaser.Geom.Rectangle.Contains
            ).on('pointerdown', function () {
                winText.setVisible(false);
                againText.setVisible(false);
                closeText.setVisible(false);
            });
            room['closeText'] = closeText;
        }
        room['closeText'].setVisible(true);

        var idx = room['roomStart'] == 0 ? 0 : 1;
        var player = room['player'][idx];
        var player1 = room['player'][(idx + 1) > 1 ? 0 : 1];

        for (var i in player['c']) {
            var c = player['c'].pop();
            c.setVisible(false);
        }
        for (var i in player1['c']) {
            var c1 = player1['c'].pop();
            c1.setVisible(false);
        }

        var card0 = 'card' + room['roomStart'];
        for (var i in msg.data[card0]) {
            var c = g.add.sprite(100 + (i * 30), g.game.config.height / 2, 'p' + msg.data[card0][i]);
            player['c'].push(c);
        }
        var card1 = 'card' + (room['roomStart'] + 1 > 1 ? '0' : '1');
        for (var i in msg.data[card1]) {
            var c1 = g.add.sprite(g.game.config.width - 100 - ((msg.data[card1].length - 1 - i) * 30), g.game.config.height / 2, 'p' + msg.data[card1][i]);
            player1['c'].push(c1);
        }
    },
    'ROOM_AGAIN': function (roomid, userid) {
        send(g.websocket, {
            'type': 'ROOM_AGAIN',
            'data': {
                'roomid': roomid,
                'userid': userid
            }
        });
    },
    'ROOM_AGAIN_RESP': function (msg) {
        room['winText'].setText('').setVisible(false);
        room['againText'].setVisible(false);
        room['closeText'].setVisible(false);

        room['jiaoText'].setVisible(false);
        room['genText'].setVisible(false);
        room['buYaoText'].setVisible(false);
        room['kaiText'].setVisible(false);

        var idx = room['roomStart'] == 0 ? 0 : 1;
        var player = room['player'][idx];
        var player1 = room['player'][(idx + 1) > 1 ? 0 : 1];

        for (var i in player['c']) {
            player['c'][i].setVisible(false);
        }
        for (var i in player1['c']) {
            player1['c'][i].setVisible(false);
        }

    }
}

function ping_() {
    send(g.websocket, {
        'type': 'PING',
        'data': {
            'value': 'PING'
        }
    });
}

function joinRoom() {
    var roomid = $('#g-roomid').val();
    var userid = $('#g-userid').val();
    var nickname = $('#g-nickname').val();
    cmd_map['JOIN_ROOM'](roomid, userid, nickname);
}

function jiao() {
    var roomid = $('#g-roomid').val();
    var userid = $('#g-userid').val();
    cmd_map['ROOM_JIAO'](roomid, userid);
}

function gen() {
    var roomid = $('#g-roomid').val();
    var userid = $('#g-userid').val();
    cmd_map['ROOM_GEN'](roomid, userid);
}

function buYao() {
    var roomid = $('#g-roomid').val();
    var userid = $('#g-userid').val();
    cmd_map['ROOM_BU_YAO'](roomid, userid);
}

function kai() {
    var roomid = $('#g-roomid').val();
    var userid = $('#g-userid').val();
    cmd_map['ROOM_KAI'](roomid, userid);
}

function again() {
    var roomid = $('#g-roomid').val();
    var userid = $('#g-userid').val();
    cmd_map['ROOM_AGAIN'](roomid, userid);
}

function onopen_() {
    ping_();
    joinRoom();
}

function onerror_() {

}

function onclose_() {
}

function onmessage_(msg) {
    var type = msg.type;
    if (typeof cmd_map[type] === 'function') {
        cmd_map[type](msg);
    } else {
        console.log('cmd type : ' + type);
    }
}

function new_ws(g) {
    var protocol = window.location.protocol === 'https' ? 'wss://' : 'ws://';
    var url = protocol + window.location.hostname + ':8001/game';
    g.websocket = new WebSocket(url);
    g.websocket.binaryType = 'arraybuffer';

    g.websocket.onopen = function (e) {
        console.log('onopen');
        onopen_();
    };

    g.websocket.onerror = function (e) {
        console.log('onerror');
        onerror_();
    };

    g.websocket.onclose = function (e) {
        console.log('onclose');
        onclose_();
    };

    g.websocket.onmessage = function (e) {
        console.log('onmessage' + e.data);
        onmessage_(JSON.parse(e.data));
    };
}

function close_ws(g) {
    if (g.websocket != null) {
        g.websocket.close();
    }
}

function send(ws, msg) {
    var str = JSON.stringify(msg);
    console.log('send:', str);
    ws.send(str);
}
