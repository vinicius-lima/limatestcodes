var WebSocketServer = require('ws').Server;

var wss = new WebSocketServer({port: 8080});

wss.broadcast = function(data) {
    for(var i in this.clients) {
        this.clients[i].send(data);
    }
};

wss.on('connection', function(ws) {
    console.log("Host connected!");
    ws.on('message', function(message) {
        console.log('received: %s', message);
        wss.broadcast(message);
    });
});
