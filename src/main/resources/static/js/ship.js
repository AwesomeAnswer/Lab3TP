var stompClient = null;

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            document.getElementById('operatorMessage').innerText = JSON.parse(message.body).message;
        });
        stompClient.subscribe('/topic/ship', function (shipData) {
            var ship = JSON.parse(shipData.body);
            if (ship.docked) {
                alert('Successful docking!');
            }
        });
    });
}

function moveShip(dx, dy) {
    stompClient.send("/app/move", {}, JSON.stringify({'dx': dx, 'dy': dy}));
}

function rotateShip(dr) {
    stompClient.send("/app/rotate", {}, JSON.stringify({'dr': dr}));
}

window.onload = connect;