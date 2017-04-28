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
package app.services;

import static def.dom.Globals.console;

import def.socket_io.socketio.Socket;
import model.Message;
import model.User;

/**
 * Angular service for sending message
 * 
 * @author Louis Grignon
 *
 */
public class MessageService {
	private Socket socket;

	static String[] $inject = { "socket" };

	MessageService(Socket socket) {
		console.info(socket);
		this.socket = socket;
	}

	public void sendMessage(User user, String message) {
		console.log("sending message: " + message);
		this.socket.emit("newmsg", new Message(user, message));
	}
}
