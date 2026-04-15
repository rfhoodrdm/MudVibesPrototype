const outputEl = document.getElementById('game-output');
const inputEl = document.getElementById('player-input');
const submitBtn = document.getElementById('submit-btn');
const statusEl = document.getElementById('connection-status');
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

let socket;
const MAX_OUTPUT_LINES = 400;
const RECONNECT_INTERVAL_MS = 2000;
const LIVE_UPDATE_PING_INTERVAL_MS = 15000;
const LIVE_UPDATE_PONG_TIMEOUT_MS = 5000;
const LIVE_UPDATE_PING_MESSAGE = '__mudvibes_ping__';
const LIVE_UPDATE_PONG_MESSAGE = '__mudvibes_pong__';
let reconnectTimerId;
let liveUpdatePingTimerId;
let liveUpdatePongTimeoutId;

const isAtBottom = () => {
    return outputEl.scrollTop + outputEl.clientHeight >= outputEl.scrollHeight - 4;
};

const appendLines = (rawText) => {
    const linesToAdd = rawText.split(/\r?\n/);
    const stickToBottom = isAtBottom();
    const previousScroll = outputEl.scrollTop;

    linesToAdd.forEach((text) => {
        const combined = `${outputEl.value}${outputEl.value ? '\n' : ''}${text}`;
        const lines = combined.split('\n');
        const trimmedValue =
            lines.length > MAX_OUTPUT_LINES
                ? lines.slice(lines.length - MAX_OUTPUT_LINES).join('\n')
                : combined;

        outputEl.value = trimmedValue;
    });

    if (stickToBottom) {
        outputEl.scrollTop = outputEl.scrollHeight;
        return;
    }

    const restoreScroll = () => {
        const maxScroll = Math.max(0, outputEl.scrollHeight - outputEl.clientHeight);
        outputEl.scrollTop = Math.min(previousScroll, maxScroll);
    };

    if (typeof window.requestAnimationFrame === 'function') {
        window.requestAnimationFrame(restoreScroll);
    } else {
        restoreScroll();
    }
};

const setConnectionState = (text) => {
    statusEl.textContent = text;
};

const clearReconnectTimer = () => {
    if (reconnectTimerId) {
        window.clearTimeout(reconnectTimerId);
        reconnectTimerId = undefined;
    }
};

const clearLiveUpdateHeartbeat = () => {
    if (liveUpdatePingTimerId) {
        window.clearInterval(liveUpdatePingTimerId);
        liveUpdatePingTimerId = undefined;
    }

    if (liveUpdatePongTimeoutId) {
        window.clearTimeout(liveUpdatePongTimeoutId);
        liveUpdatePongTimeoutId = undefined;
    }
};

const scheduleReconnect = (statusText) => {
    setConnectionState(statusText);

    if (reconnectTimerId) {
        return;
    }

    reconnectTimerId = window.setTimeout(() => {
        reconnectTimerId = undefined;
        connect();
    }, RECONNECT_INTERVAL_MS);
};

const reconnectLiveUpdates = (statusText) => {
    clearLiveUpdateHeartbeat();
    scheduleReconnect(statusText);

    if (socket && ![WebSocket.CLOSING, WebSocket.CLOSED].includes(socket.readyState)) {
        socket.close();
    }
};

const sendLiveUpdatePing = () => {
    if (!socket || socket.readyState !== WebSocket.OPEN) {
        reconnectLiveUpdates('Reconnecting for live updates…');
        return;
    }

    try {
        socket.send(LIVE_UPDATE_PING_MESSAGE);
    } catch (error) {
        reconnectLiveUpdates('Reconnecting for live updates…');
        return;
    }

    if (liveUpdatePongTimeoutId) {
        window.clearTimeout(liveUpdatePongTimeoutId);
    }

    liveUpdatePongTimeoutId = window.setTimeout(() => {
        liveUpdatePongTimeoutId = undefined;
        reconnectLiveUpdates('Reconnecting for live updates…');
    }, LIVE_UPDATE_PONG_TIMEOUT_MS);
};

const startLiveUpdateHeartbeat = () => {
    clearLiveUpdateHeartbeat();
    liveUpdatePingTimerId = window.setInterval(sendLiveUpdatePing, LIVE_UPDATE_PING_INTERVAL_MS);
};

const connect = () => {
    if (socket && [WebSocket.CONNECTING, WebSocket.OPEN].includes(socket.readyState)) {
        return;
    }

    const scheme = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const url = `${scheme}://${window.location.host}/ws/mud`;
    let nextSocket;

    try {
        nextSocket = new WebSocket(url);
    } catch (error) {
        scheduleReconnect('Unable to open live updates, retrying…');
        return;
    }

    socket = nextSocket;

    nextSocket.addEventListener('open', () => {
        if (socket !== nextSocket) {
            return;
        }

        clearReconnectTimer();
        setConnectionState('Receiving live updates');
        startLiveUpdateHeartbeat();
    });

    nextSocket.addEventListener('close', () => {
        if (socket !== nextSocket) {
            return;
        }

        clearLiveUpdateHeartbeat();
        scheduleReconnect('Reconnecting for live updates…');
    });

    nextSocket.addEventListener('error', () => {
        if (socket !== nextSocket) {
            return;
        }

        reconnectLiveUpdates('Update stream errored, retrying…');

        if (![WebSocket.CLOSING, WebSocket.CLOSED].includes(nextSocket.readyState)) {
            nextSocket.close();
        }
    });

    nextSocket.addEventListener('message', (event) => {
        if (socket !== nextSocket) {
            return;
        }

        if (event.data === LIVE_UPDATE_PONG_MESSAGE) {
            if (liveUpdatePongTimeoutId) {
                window.clearTimeout(liveUpdatePongTimeoutId);
                liveUpdatePongTimeoutId = undefined;
            }
            return;
        }

        appendLines(event.data);
    });
};

const sendInput = async () => {
    const text = inputEl.value.trim();
    if (!text.length) {
        appendLines('[client] Please type a command.');
        return;
    }

    const headers = {'Content-Type': 'text/plain'};
    if (csrfToken && csrfHeader) {
        headers[csrfHeader] = csrfToken;
    }

    try {
        const response = await fetch('/api/commands', {
            method: 'POST',
            headers,
            body: text
        });

        if (response.ok) {
            inputEl.value = '';
            return;
        }

        appendLines('[system] Unable to send command.');
    } catch (error) {
        appendLines('[system] Unable to send command.');
    }
};

submitBtn.addEventListener('click', sendInput);

inputEl.addEventListener('keydown', (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendInput();
    }
});

connect();
