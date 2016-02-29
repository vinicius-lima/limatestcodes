var peerConnection;
var peerConnectionConfig = {
    'iceServers': [
        {'url': 'stun:stun.services.mozilla.com'},
        {'url': 'stun:stun.l.google.com:19302'}
    ]
};

window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
window.RTCIceCandidate = window.RTCIceCandidate || window.mozRTCIceCandidate || window.webkitRTCIceCandidate;
window.RTCSessionDescription = window.RTCSessionDescription || window.mozRTCSessionDescription || window.webkitRTCSessionDescription;

var textArea = document.querySelector('#textarea');
var msg = document.querySelector('#message');

// Connecting to a signaling server.
var conn = new WebSocket('ws://10.16.1.33:8080');

var dataChannel;

peerConnection = new RTCPeerConnection(peerConnectionConfig, {optional: [{RtpDataChannels: true}]});
peerConnection.onicecandidate = function (event) { 
    if (event.candidate) { 
        if(event.candidate != null) {
            conn.send(JSON.stringify({'ice': event.candidate}));
        }
    } 
};

peerConnection.ondatachannel = function (event) {
    dataChannel = event.channel;
}
        
conn.onopen = function () { 
   console.log("Connected to the signaling server");
};
 
// When receiving a message from a signaling server.
conn.onmessage = function (message) { 
   console.log("Got message", message.data);
   
   if(!peerConnection) start(false);

   var signal = JSON.parse(message.data);
   if(signal.offer) {
        peerConnection.setRemoteDescription(new RTCSessionDescription(signal.offer), function() {
            peerConnection.createAnswer(function(answer) {
                peerConnection.setLocalDescription(answer, function () {
                    conn.send(JSON.stringify({'answer': peerConnection.localDescription}));
                })
            }, errorHandler);
        }, errorHandler);
    }
   else if(signal.answer) {
        peerConnection.setRemoteDescription(new RTCSessionDescription(signal.answer));
   }
   else if(signal.ice) {
        peerConnection.addIceCandidate(new RTCIceCandidate(signal.ice));
   }
}; 

conn.onerror = function (err) { 
   console.log("Got error", err); 
};

function start(isCaller) {
    if(isCaller) {
        dataChannel = peerConnection.createDataChannel("stringChannel");
        
        peerConnection.createOffer(function (offer) {
                peerConnection.setLocalDescription(offer, function () {
                    conn.send(JSON.stringify({'offer': peerConnection.localDescription}));
                })
            }
        , errorHandler);
    }
    
    dataChannel.onerror = function (err) {
        console.error("Channel Error:", err);
    };

    dataChannel.onmessage = function (message) { 
        textArea.value += "Remote: " + message.data + "\n";
    };

    dataChannel.onclose = function () { 
        console.log("data channel is closed");
    };
}

function errorHandler(error) {
    console.log(error);
}

function send() {
    var data = msg.value;
    console.log("Sending value");
    textArea.value += "Local: " + data + "\n";
    dataChannel.send(data);
}
