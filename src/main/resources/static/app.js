var eventSource = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
}

function connect() {
    if (window.EventSource == null) {
        alert('The browser does not support Server-Sent Events');
    } else {
        eventSource = new EventSource("/api/connect");
        printState(eventSource.readyState);
        eventSource.onopen = function () {
            printState(eventSource.readyState);
            setConnected(true);
        };
    }
}

function printState(state) {
    if (state === EventSource.CONNECTING) { //      0
        console.log('connection state: ' + eventSource.readyState + ' -> connection has not yet been established.');
    } else if (state === EventSource.OPEN) { //     1
        console.log('connection state: ' + eventSource.readyState + ' -> open.');
    } else if (state === EventSource.CLOSED) { //   2
        console.log('connection state: ' + eventSource.readyState + ' -> connection closed.');
    }

}

function disconnect() {
    if (eventSource !== null) {
        eventSource.close();
        eventSource = null;
    }
    setConnected(false);
    console.log("Disconnected");
}

$(function () {
    $("form").on('submit', function (e) {e.preventDefault();});
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
});
