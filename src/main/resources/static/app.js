var ws = new WebSocket("ws://localhost:8080/ws");
ws.onopen = function() {
    console.log("ws.onopen", ws);
    console.log("ws.readyState", "websocketstatus");
    //ws.send("event-me-from-browser");
}
ws.onclose = function(error) {
    console.log("ws.onclose", ws, error);
    events("Closing connection");
}
ws.onerror = function(error) {
    console.log("ws.onerror", ws, error);
    events("An error occured");
}
ws.onmessage = function(error) {
    console.log("ws.onmessage", ws, error);
    events(error.data);
}
function events(responseEvent) {
    document.querySelector(".events").innerHTML += responseEvent + "<br>";
}

function sendMyMessage() {
    let message = "this message sent from client";
    ws.send(JSON.stringify({'message': message, 'name':'DatDT'}));
}