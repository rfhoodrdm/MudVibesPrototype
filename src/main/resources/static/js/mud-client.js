const outputEl = document.getElementById('game-output');
const inputEl = document.getElementById('player-input');
const submitBtn = document.getElementById('submit-btn');
const statusEl = document.getElementById('connection-status');
const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

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
        setConnectionState('Receiving live updates');
    });

    socket.addEventListener('close', () => {
        setConnectionState('Reconnecting for live updates…');
        setTimeout(connect, 1500);
    });

    socket.addEventListener('error', () => {
        setConnectionState('Update stream errored, retrying…');
    });

    socket.addEventListener('message', (event) => {
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
        }
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
