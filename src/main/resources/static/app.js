var eventSource = null;

function setConnected(connected) {
    $("#register").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#process_request").show();
        $("#request_answer").show();
    } else {
        $("#process_request").hide();
        // $("#request_answer").hide();
        $("#process").html("");
        // $("#answer").html("");
    }
}

function register() {
    if (window.EventSource == null) {
        alert('The browser does not support Server-Sent Events');
    } else if (!$("#name").val().length) {
        alert('User OID is empty, please fill it.');
    } else {
        eventSource = new EventSource("/api/register/" + $("#name").val() + "/start/");
        eventSource.onopen = function () {
            printState(eventSource.readyState);
            setConnected(true);
        };
        eventSource.addEventListener("REG_START", (regStartInfo) => {
            showRegStartInfo(`reqID: ${regStartInfo.lastEventId}. ${regStartInfo.data}`);
        });
        eventSource.addEventListener("REG_RESULT", (regResultInfo) => {
            showRegResultInfo(`reqID: ${regResultInfo.lastEventId}: ${regResultInfo.data}`);
            disconnect(regResultInfo.target);
        });
        eventSource.onmessage = (event) => {
            console.log(`reqID: ${event.lastEventId}: ${event.data}`);
        }
        eventSource.onerror = function (event) {
            console.log('-> There is Error happens. Last Event ID = ' + event.target.lastEventId)
            printState(event.target.readyState);
            if (event.target.readyState === EventSource.OPEN) {
                disconnect(event.target);
            }
        };
    }
}

function showRegStartInfo(content) {
    $("#process").append("<tr><td>" + content + "</td></tr>");
}

function showRegResultInfo(content) {
    $("#answer").append("<tr><td>" + content + "</td></tr>");
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

function disconnect(es) {
    if (es !== null) {
        es.close();
        eventSource = null;
    }
    setConnected(false);
    console.log("-> connection closed");
}

$(function () {
    $("form").on('submit', function(e) { e.preventDefault(); });

    $("#register").click(function() { register(); });
    $("#disconnect").click(function() { disconnect(); });
});
