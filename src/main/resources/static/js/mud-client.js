const outputEl = document.getElementById('game-output');
const inputEl = document.getElementById('player-input');
const submitBtn = document.getElementById('submit-btn');
const statusEl = document.getElementById('connection-status');

let socket;

const isAtBottom = () => {
    return outputEl.scrollTop + outputEl.clientHeight >= outputEl.scrollHeight - 4;
};

const appendLine = (text) => {
    const stickToBottom = isAtBottom();
    const previousScroll = outputEl.scrollTop;

    outputEl.value = `${outputEl.value}${outputEl.value ? '\n' : ''}${text}`;

    if (stickToBottom) {
        outputEl.scrollTop = outputEl.scrollHeight;
    } else {
        outputEl.scrollTop = previousScroll;
    }
};

const setConnectionState = (text) => {
    statusEl.textContent = text;
};

const connect = () => {
    const scheme = window.location.protocol === 'https:' ? 'wss' : 'ws';
    const url = `${scheme}://${window.location.host}/ws/mud`;
    socket = new WebSocket(url);

    socket.addEventListener('open', () => {
        setConnectionState('Connected to server');
        submitBtn.disabled = false;
    });

    socket.addEventListener('close', () => {
        setConnectionState('Disconnected. Reconnecting…');
        submitBtn.disabled = true;
        setTimeout(connect, 1500);
    });

    socket.addEventListener('error', () => {
        setConnectionState('Connection error, retrying…');
    });

    socket.addEventListener('message', (event) => {
        appendLine(event.data);
    });
};

const sendInput = () => {
    if (!socket || socket.readyState !== WebSocket.OPEN) {
        appendLine('[system] Cannot send command while disconnected.');
        return;
    }
    const text = inputEl.value.trim();
    if (!text.length) {
        appendLine('[system] Please type a command.');
        return;
    }
    socket.send(text);
    inputEl.value = '';
};

submitBtn.addEventListener('click', sendInput);

inputEl.addEventListener('keydown', (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        sendInput();
    }
});

connect();
