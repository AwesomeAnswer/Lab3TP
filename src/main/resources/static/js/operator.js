var stompClient = null;
var canvas = document.getElementById('shipCanvas');
var ctx = canvas.getContext('2d');
var station = null;
var shipImage = new Image();
shipImage.src = '/images/ship.png';
var stationImage = new Image();
stationImage.src = '/images/station.png';

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/ship', function (shipData) {
            var ship = JSON.parse(shipData.body);
            document.getElementById('position').innerText = `X: ${ship.x}, Y: ${ship.y}`;
            document.getElementById('rotation').innerText = `Rotation: ${ship.rotation}`;
            drawScene(ship);
            if (ship.docked) {
                document.getElementById('status').innerText = 'Successful docking!';
            }
        });
        stompClient.subscribe('/topic/station', function (stationData) {
            station = JSON.parse(stationData.body);
            drawScene();
        });
        requestStationData();
    });
}

function sendMessage() {
    var message = document.getElementById('message').value;
    stompClient.send("/app/message", {}, JSON.stringify({'message': message}));
}

function requestStationData() {
    stompClient.send("/app/station", {}, {});
}

function drawScene(ship) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    if (station) {
        ctx.save();
        ctx.translate(canvas.width / 2 + station.x, canvas.height / 2 - station.y);
        ctx.rotate(station.rotation * Math.PI / 180);
        ctx.drawImage(stationImage, -stationImage.width / 2, 0);
        ctx.restore();
    }

    if (ship) {
        var canvasX = canvas.width / 2 + ship.x;
        var canvasY = canvas.height / 2 - ship.y;

        ctx.save();
        ctx.translate(canvasX, canvasY);
        ctx.rotate(ship.rotation * Math.PI / 180);
        ctx.drawImage(shipImage, -shipImage.width / 2, -shipImage.height/ 2);
        ctx.restore();
    }
}

window.onload = connect;