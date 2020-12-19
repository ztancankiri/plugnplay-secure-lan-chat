# plugnplay-lan-chat

- A simple plug-n-play chat app developed in Java.

- The user interface uses Java Swing.

- The chat messages between clients are secured with symmetric encryption (AES-256 with CBC mode and pkcs5 padding).

- All clients should enter the same passphrase to join to chat room. All communication will be encrypted based on the key generated with this passphrase (PBKDF2).

- The first client who starts the application acts as also the chat server on the local area network.

- Other clients discover the server by multicasting magical (! lol) discovery packets and getting their responses.
