const outputEl = document.getElementById('game-output');
const inputEl = document.getElementById('player-input');
const submitBtn = document.getElementById('submit-btn');
const statusEl = document.getElementById('connection-status');

let socket;
const MAX_OUTPUT_LINES = 400;

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
        appendLines(event.data);
    });
};

const sendInput = () => {
    if (!socket || socket.readyState !== WebSocket.OPEN) {
        appendLines('[system] Cannot send command while disconnected.');
        return;
    }
    const text = inputEl.value.trim();
    if (!text.length) {
        appendLines('[client] Please type a command.');
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
