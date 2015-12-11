# JSweet Node.js / Socket.IO server + Angular web client example

The classic Socket.IO example: a simple instant messenger. Server is spawned by Node.js and Web UI is powered by Angular. Both are linked together by Socket.IO

Find more info about this example on the original tutorial by following this link: http://socket.io/get-started/chat/

All these example are written in Java using the JSweet APIs (candies). They are then transpiled to JavaScript by the JSweet transpiler.

## Build it
Just build it using Gulp:

```
> git clone https://github.com/lgrignon/jsweet-node-example.git
> cd jsweet-node-example
> bower install
> npm install
> gulp full
```

`gulp full` executes behind the scene `gulp buildServer` and `gulp buildClient` which runs respectively `mvn generate-sources -P server` and `mvn generate-sources -P client`. Other less significants tasks are launched, such as jade transpilation, resources copy, bower libs concat & minify.
The output folder is `build`.

## Run it
Once built: 
```
> cd build
> node server/module.js
```

Then, one could browse the chat by accessing [http://localhost:1337/](http://localhost:1337/) in a browser.

## Prerequisites

The `node` and `npm` executables must be in the path (https://nodejs.org). `bower` and `gulp` should be installed as well.
Install Maven (https://maven.apache.org/install.html).
