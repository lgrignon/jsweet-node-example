/* 
 * Copyright (C) 2015 Louis Grignon <louis.grignon@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server;

import static def.body_parser.body_parser.Globals.json;
import static def.body_parser.body_parser.Globals.urlencoded;
import static def.errorhandler.Globals.errorhandler;
import static def.express.Globals.express_lib_express;
import static def.express.Globals.express_serve_static;
import static def.dom.Globals.console;
import static def.node.Globals.__dirname;
import static def.node.Globals.process;
import static def.node.http.Globals.createServer;
import static def.socket_io.Globals.socket_io;
import static def.socket_io.StringTypes.connect;
import static jsweet.util.Globals.function;
import static jsweet.util.Globals.number;
import static jsweet.util.Globals.object;
import static jsweet.util.Globals.any;
import static jsweet.util.Globals.union;

import def.body_parser.body_parser.OptionsDto;
import def.errorhandler.errorhandler.Options;
import def.express.express_lib_application.Application;
import def.express.express_lib_express.Express;
import def.express.express_lib_request.Request;
import def.express.express_lib_response.Response;
import def.express.express_lib_response.Response.Locals;
import def.express.express_lib_router_index.NextFunction;
import def.express.express_lib_router_index.RequestHandler;
import def.js.Array;
import def.js.Date;
import def.js.Object;
import def.node.http.IncomingMessage;
import def.node.http.Server;
import def.node.http.ServerResponse;
import def.socket_io.socketio.Socket;
import jsweet.lang.Interface;
import model.Message;
import model.User;

/**
 * Typing interface for socket connection / request. Since API is not
 * "typed enough", we define additional interfaces in order to be safe.
 * 
 * @author Louis Grignon
 *
 */
@Interface
abstract class SocketConnection {
	public String remoteAddress;
}

/**
 * @see SocketConnection
 * @author Louis Grignon
 *
 */
@Interface
abstract class SocketRequest {
	public SocketConnection connection;
}

/**
 * Server side code. Configures Node.js / Express application and handles views
 * transport and socket connections.
 * 
 * @author Louis Grignon
 *
 */
public class Globals {

	public static void main(String[] args) {
		
		console.log("running server");

		Application app = express_lib_express();
		Express express = express_lib_express;

		// Configuration
		app.set("views", __dirname + "/../");
		app.set("view engine", "jade");
		app.use(urlencoded(new OptionsDto() {
			{
				extended = true;
			}
		}));
		app.use(json());
		// the URL throught which you want to access to you staticcontent
		// where your static content is located in your filesystem
		app.use("/", (RequestHandler) express_serve_static(__dirname + "/../"));

		console.log("express app ", app, " dirname=" + __dirname);

		Object processEnv = object(process.env);
		String env = (String) processEnv.$get("NODE_ENV");
		if (env == null) {
			env = "development";
		}

		console.log("environment: " + env);

		if (env == "development") {
			app.set("port", "1337");

			Options options = new Options() {
				{
					log = union(true);
				}
			};

			options.$set("dumpExceptions", true);
			options.$set("showStack", true);

			app.use(errorhandler(options));
		} else if (env == "production") {
			app.set("port", "3000");
			app.use(errorhandler());
		}

		// ROUTING
		// ===========================
		app.get("/", (Request req, Response res, NextFunction next) -> {
			console.log("request on /");
			res.render("index", new Locals() {
				{
					$set("title", "Express");
				}
			});
			return null;
		});

		// CREATE SERVER & LISTEN
		// ===========================

		console.log("starting server on port: " + app.get("port"));
		Server server = createServer((IncomingMessage msg, ServerResponse resp) -> {
			console.log("server received request - url=" + msg.url + " method=" + msg.method);
			RequestHandler delegate = any(app);
			delegate.apply(any(msg), any(resp), null);
		});

		server.listen(app.get("port"), function(() -> {
			console.log("server listening on port [" + app.get("port") + "]");
		}));

		def.socket_io.socketio.Server ioServer = socket_io.listen.apply(server);

		Object users = new Object();
		Array<Message> messages = new Array<>();
		int MAX_MESSAGES_BUFFER = 15;

		// Connexion socket
		ioServer.sockets.on(connect, (Socket socket) -> {

			// Web socket etablie
			double timeStamp = new Date().getTime();
			SocketRequest socketRequest = (SocketRequest) socket.request;
			String ip = socketRequest.connection.remoteAddress;
			console.log("[CONNECTED] " + timeStamp + " | client IP " + ip + " |");

			// refresh all available users
			int usersCount = Object.keys(users).length;
			for (String userIp : Object.keys(users)) {
				socket.emit("newuser", users.$get(userIp), usersCount);
			}

			socket.on("login:attempt", function((User user) -> {
				console.log("received: login attempt");

				User identity = new User(user.name, ip, guid());
				users.$set(ip, identity);
				ioServer.emit("newuser", identity, Object.keys(users).length);
				socket.emit("login:success", identity);
			}));

			socket.on("request:loggedUser", function(() -> {
				console.log("received: request logged user");

				socket.emit("response:loggedUser", users.$get(ip));
			}));

			socket.on("request:lastMessages", function(() -> {
				console.info("requesting last messages... Sending");
				socket.emit("response:lastMessages", messages);
			}));

			socket.on("request:loggedUsers", function(() -> {
				console.info("requesting logged user... sending");
				int i = 0;
				for (String userIp : Object.keys(users)) {
					socket.emit("newuser", users.$get(userIp), ++i);
				}
			}));

			socket.on("disconnect", function((User user) -> {
				User identity = (User) users.$get(ip);
				if (identity == null) {
					return false;
				}

				users.$delete(identity.id);
				console.info("disconnection");
				ioServer.emit("userdisconnect", user, Object.keys(users).length);

				return null;
			}));

			socket.on("newmsg", function((Message message) -> {
				console.log("received message: " + message);

				message.user = (User) users.$get(ip);
				// message.id = guid();
				messages.push(message);
				if (messages.length > MAX_MESSAGES_BUFFER) {
					messages.shift();
				}
				ioServer.emit("newmsg", message);
			}));
		});
	}

	private static String s4() {
		return number(Math.floor((1 + Math.random()) * 0x10000)).toString(16).substring(1);
	}

	private static String guid() {
		return s4() + s4() + "-" + s4() + "-" + s4() + "-" + s4() + "-" + s4() + s4() + s4();
	}
}
