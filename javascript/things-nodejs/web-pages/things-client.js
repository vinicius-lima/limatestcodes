function getAllThings() {
    var xhttp = new XMLHttpRequest();

    xhttp.open('GET', 'http://127.0.0.1:8080/things', true);

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            document.getElementById("textarea").value = JSON.stringify(JSON.parse(xhttp.response), null, 2);
        }
        else {
            var errorMsg = 'Error:\n';
            errorMsg += xhttp.status + ' ' + xhttp.statusText;
            document.getElementById("textarea").value = errorMsg;
        }
    };

    xhttp.send();
}

function getThing() {
    var thingId = document.getElementById("thingId").value;
    var xhttp = new XMLHttpRequest();

    var thingUrl = 'http://127.0.0.1:8080/things/' + thingId;

    xhttp.open('GET', thingUrl, true);

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            document.getElementById("textarea").value = JSON.stringify(JSON.parse(xhttp.response), null, 2);
        }
        else {
            var errorMsg = 'Error:\n';
            errorMsg += xhttp.status + ' ' + xhttp.statusText;
            document.getElementById("textarea").value = errorMsg;
        }
    };

    xhttp.send();
}

function getToken() {
    var xhttp = new XMLHttpRequest();
    
    xhttp.open('POST', 'http://127.0.0.1:8080/oauth/token?grant_type=client_credentials', true);
    xhttp.setRequestHeader('Authorization', 'Basic ' + window.btoa('1855NOAMBJ4TU3FYDQ6IM0M2E:CuVDWJT++PPgp2TY92VqNX7/wTWxS3mR+rzySggG3yA'));

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            var res = JSON.parse(xhttp.response);
            var accessToken = res.access_token;
            /*document.getElementById("textarea").value = 'accessToken: ' + accessToken;*/
            
            document.getElementById("textarea").value = JSON.stringify(res, null, 2);
            
            var d = new Date();
            d.setTime(d.getTime() + (24*60*60*1000)); // Time unit is miliseconds.
            var expires = "expires=" + d.toUTCString();
            document.cookie = "accessToken=" + accessToken + ";" + expires;
        }
    };
    
    xhttp.send();
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    
    return "";
}
    
function addThing() {
    //var key = document.getElementById("key").value;
    var value = document.getElementById("value").value;

    var accessToken = getCookie('accessToken');
    if(accessToken == "") {
        document.getElementById("textarea").value = 'No token found!!';
        return;
    }

    var xhttp = new XMLHttpRequest();
    
    xhttp.open('POST', 'http://127.0.0.1:8080/things', true);
    xhttp.setRequestHeader('Authorization', 'Bearer ' + accessToken);
    xhttp.setRequestHeader('Content-Type', 'application/json');

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            var res = JSON.parse(xhttp.response);
            document.getElementById("textarea").value = JSON.stringify(res, null, 2);
        }
        else {
            var errorMsg = 'Error:\n';
            errorMsg += xhttp.status + ' ' + xhttp.statusText;
            document.getElementById("textarea").value = errorMsg;
        }
    };
    
    //xhttp.send(JSON.stringify({key: value}));
    xhttp.send(JSON.stringify({thing: value}));
}

function deleteThing() {
    var thingId = document.getElementById("thingId").value;
    
    var accessToken = getCookie('accessToken');
    if(accessToken == "") {
        document.getElementById("textarea").value = 'No token found!!';
        return;
    }

    var thingUrl = 'http://127.0.0.1:8080/things/' + thingId;

    var xhttp = new XMLHttpRequest();
    
    xhttp.open('DELETE', thingUrl, true);
    xhttp.setRequestHeader('Authorization', 'Bearer ' + accessToken);

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            var res = JSON.parse(xhttp.response);
            document.getElementById("textarea").value = JSON.stringify(res, null, 2);
        }
        else {
            var errorMsg = 'Error:\n';
            errorMsg += xhttp.status + ' ' + xhttp.statusText;
            document.getElementById("textarea").value = errorMsg;
        }
    };
    
    //xhttp.send(JSON.stringify({thing: value}));
    xhttp.send();
}

function getProfile() {
    var accessToken = getCookie('accessToken');
    if(accessToken == "") {
        document.getElementById("textarea").value = 'No token found!!';
        return;
    }

    var xhttp = new XMLHttpRequest();
    
    xhttp.open('GET', 'http://127.0.0.1:8080/me', true);
    xhttp.setRequestHeader('Authorization', 'Bearer ' + accessToken);

    xhttp.onreadystatechange = function() {
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            var res = JSON.parse(xhttp.response);
            document.getElementById("textarea").value = JSON.stringify(res, null, 2);
        }
        else {
            var errorMsg = 'Error:\n';
            errorMsg += xhttp.status + ' ' + xhttp.statusText;
            document.getElementById("textarea").value = errorMsg;
        }
    };
    
    xhttp.send();
}
