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
        eventSource.onerror = function (event) {
            console.log('-> There is Error happens')
            printState(event.target.readyState);
            if (event.target.readyState !== EventSource.CLOSED) {
                disconnect(event.target);
            }
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

function  disconnect(es) {
    if (es !== null) {
        es.close();
        eventSource = null;
    }
    setConnected(false);
    console.log("-> connection closed");
}

$(function () {
    $("form").on('submit', function (e) {e.preventDefault();});
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(eventSource); });
});
