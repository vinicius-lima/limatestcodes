// WebRTC configuration parameters.
var peerConnection;
var peerConnectionConfig = {
    'iceServers': [
        {'urls': 'stun:stun.services.mozilla.com'},
        {'urls': 'stun:stun.l.google.com:19302'}
    ]
};

// Shims. API is at the very beginning.
window.RTCPeerConnection = window.RTCPeerConnection || window.mozRTCPeerConnection || window.webkitRTCPeerConnection;
window.RTCIceCandidate = window.RTCIceCandidate || window.mozRTCIceCandidate || window.webkitRTCIceCandidate;
window.RTCSessionDescription = window.RTCSessionDescription || window.mozRTCSessionDescription || window.webkitRTCSessionDescription;

// Get HTML page components.
var textArea = document.querySelector('#textarea');
var msg = document.querySelector('#message');

// Connecting to a signaling server.
var conn = new WebSocket('ws://10.16.1.33:8080');

// WebRTC channel for sending and receiving any data.
var dataChannel;

// Setting WebSocket connection callbacks.
conn.onopen = function () { 
   console.log("Connected to the signaling server");
};
 
// Callback for receiving a message from a signaling server.
conn.onmessage = function (message) { 
   console.log("Got message", message.data);
   
   if(!peerConnection) start(false);

   var signal = JSON.parse(message.data);
   // If a peer connection offer was received.
   if(signal.offer) {
        peerConnection.setRemoteDescription(new RTCSessionDescription(signal.offer), function() {
            peerConnection.createAnswer(function(answer) {
                peerConnection.setLocalDescription(answer, function () {
                    conn.send(JSON.stringify({'answer': peerConnection.localDescription}));
                }, errorHandler);
            }, errorHandler);
        }, errorHandler);
    }
   // If an answer was received for a previous offer sent.
   else if(signal.answer) {
        peerConnection.setRemoteDescription(new RTCSessionDescription(signal.answer));
   }
   // If an ice candidate was received in order to start a peer connection.
   else if(signal.ice) {
        peerConnection.addIceCandidate(new RTCIceCandidate(signal.ice));
   }
}; 

conn.onerror = function (err) { 
   console.log("Got error", err); 
};

// Initialize WebRTC peer connection.
function start(isCaller) {
    peerConnection = new RTCPeerConnection(peerConnectionConfig, {optional: [{RtpDataChannels: true}]});
    peerConnection.onicecandidate = function (event) { 
        if (event.candidate) { 
            if(event.candidate != null) {
                conn.send(JSON.stringify({'ice': event.candidate}));
            }
        }
    };

    /* The offerer should be the peer who creates the channel.
       The answerer will receive the channel in the callback ondatachannel
       on PeerConnection.
    */
    if(isCaller) {
        dataChannel = peerConnection.createDataChannel("stringChannel");
        
        peerConnection.createOffer(function (offer) {
                peerConnection.setLocalDescription(offer, function () {
                    conn.send(JSON.stringify({'offer': peerConnection.localDescription}));
                }, errorHandler);
            }
        , errorHandler);

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
    else {
        peerConnection.ondatachannel = function (event) {
            dataChannel = event.channel;

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
    }
}

// If something goes wrong.
function errorHandler(error) {
    console.log(error);
}

// Send data through channel.
function send() {
    var data = msg.value;
    console.log("Sending value");
    textArea.value += "Local: " + data + "\n";
    dataChannel.send(data);
}
