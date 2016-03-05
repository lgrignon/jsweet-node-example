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
package app.controllers;

import static def.jquery.Globals.$;
import static jsweet.dom.Globals.console;
import static jsweet.util.Globals.function;

import java.util.function.Function;

import def.angularjs.ng.ILocationService;
import def.jquery.JQueryEventObject;
import def.socket_io.socketio.Socket;
import model.User;

/**
 * Controller for the login view.
 * 
 * @author Louis Grignon
 *
 */
public class LoginController {
	private Socket socket;
	private ILocationService $location;

	LoginController(Socket socket, ILocationService $location) {
		this.socket = socket;
		this.$location = $location;

		this.init();
	}

	private void init() {

		console.log("initializing LoginController - connected=", this.socket.connected, this.socket);
		$("#loginForm").submit(this::attemptLogin);

		this.socket.on("newuser", function((User user, Number usersCount) -> this.refreshCount(usersCount)));

		Function<User, Void> onUserLogged = this::welcomeUser;
		this.socket.on("login:success", function(onUserLogged));

		this.socket.on("login:error", function(this::refuseLogin));

		$("#username").focus();

		console.log("LoginController initialized");
	}

	public Void attemptLogin(JQueryEventObject event) {
		console.log("attemptLogin");

		if (event != null) {
			event.preventDefault();
			this.socket.emit("login:attempt", new jsweet.lang.Object() {
				{
					$set("name", $("#username").val());
					$set("password", $("#password").val());
				}
			});
		}

		return null;
	}

	private Void refreshCount(Number usersCount) {
		$("#usersCount").text("" + usersCount);
		return null;
	}

	private Void welcomeUser(User user) {
		this.$location.path("chatroom");
		return null;
	}

	private void refuseLogin() {
		console.log("login refused");
	}
}
