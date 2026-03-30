
# Project Description:
MudVibesProto is a prototype Spring Boot service that handles running a MUD-style text game. It processes player input, controls the game state, and sends output messages. The Front-end is served as a web page to be loaded in a browser.


## Front-end
- Transmits commands via the WebSocket connection to the backend.
- Receives non-polled action text updates from the WebSocket connection the backend.
- Polls REST endpoints for information such as room descriptions and character state.
- Keep style in separate files from the page templates.
- Keep scripts in separate files from the page templates.

## Back-end
- Leverages Spring Boot components to handle the needs of running the game. E.g. a method decorated with @Scheduled to serve as the game update clock.


## Coding style
- Log at debug level entering and exiting of methods. Log parameters passed in if they're not too verbose or unwieldy.