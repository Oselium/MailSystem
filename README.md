# MailSystem

A simple project for my university class using Maven, JavaFX and Java. It runs locally but it can be used from another machine.


It's a badly written project so it has bugs and a lot of janky ideas.

## Description

It works as a Client-Server platform, where the Client does have a form of cache (actually, it's just a ListView that will not be updated) so every time the server goes down you will only be able to view the emails and nothing more. 

It reconnect automatically once the server goes online again.

It stores the emails in a single json file while the users are stored in a separate json.

Changing the number of thread supported changes the number of clients at the same time.

It handles:
- New emails
- Forward
- Reply
- Reply All
- Delete
- Changing from received to sent

The server can be turned off by a Button, and it writes logs for every primary action including logins and logouts, but they are not stored in any way so when you close the window you will lose the entire history.

## License
[MIT](LICENSE)
